package eu.xenit.actuators.services;

import eu.xenit.actuators.Health;
import eu.xenit.actuators.HealthDetailsError;
import eu.xenit.actuators.HealthIndicator;
import eu.xenit.actuators.HealthStatus;
import eu.xenit.actuators.model.gen.LicenseInfo;
import org.alfresco.service.descriptor.DescriptorService;
import org.alfresco.service.license.LicenseDescriptor;
import org.alfresco.service.license.LicenseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class LicenseInfoService implements HealthIndicator {

    private static final Logger logger = LoggerFactory.getLogger(LicenseInfoService.class);

    @Autowired(required = false)
    private LicenseService licenseService;
    @Autowired
    private ApplicationContext appContext;
    @Autowired
    @Qualifier("DescriptorService")
    private DescriptorService descriptorService;

    public LicenseInfo retrieveLicenseInfo() {
        if (licenseService == null) {
            licenseService = appContext.getBeansOfType(LicenseService.class).get("licenseService");
        }
        LicenseDescriptor licenseDescriptor = descriptorService.getLicenseDescriptor();

        // community
        if (licenseService == null || licenseDescriptor == null) {
            return null;
        }

        return new LicenseInfo()
                .valid(licenseService.isLicenseValid())
                .remainingDays(licenseDescriptor.getRemainingDays())
                .holder(licenseDescriptor.getHolder().getName())
                .organisation(licenseDescriptor.getHolderOrganisation())
                .maxUsers(licenseDescriptor.getMaxUsers())
                .clusteringEnabled(licenseDescriptor.isClusterEnabled())
                .encryptionEnabled(licenseDescriptor.isCryptodocEnabled())
                .heartbeatDisabled(licenseDescriptor.isHeartBeatDisabled());
    }

    @Override
    public Health isHealthy() {
        Health health = new Health();
        try {
            LicenseInfo licenseInfo = retrieveLicenseInfo();
            //community
            if (licenseInfo != null) {
                health.setDetails(licenseInfo);
            } else {
                health.setDetails(new HealthDetailsError("No license required for community"));
            }
            health.setStatus(HealthStatus.UP);
        } catch (Exception exception) {
            logger.error("Problem retrieving license info",exception);
            health.setStatus(HealthStatus.DOWN);
            health.setDetails(new HealthDetailsError(exception.getMessage()));
        }
        return health;
    }
}
