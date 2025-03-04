package eu.xenit.actuators.services;

import eu.xenit.actuators.HealthIndicator;
import eu.xenit.actuators.model.gen.LicenseInfo;
import org.alfresco.service.descriptor.DescriptorService;
import org.alfresco.service.license.LicenseDescriptor;
import org.alfresco.service.license.LicenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class LicenseInfoService extends HealthIndicator {

    @Autowired(required = false)
    private LicenseService licenseService;
    @Autowired
    private ApplicationContext appContext;
    @Autowired
    @Qualifier("DescriptorService")
    private DescriptorService descriptorService;

    @Override
    protected Object getHealthDetails() {
        LicenseInfo licenseInfo = retrieveLicenseInfo();
        //community
        if (licenseInfo != null) {
            return licenseInfo;
        } else {
            return "No license required for community";
        }
    }

    private LicenseInfo retrieveLicenseInfo() {
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


}
