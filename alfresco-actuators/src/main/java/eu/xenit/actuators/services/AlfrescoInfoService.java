package eu.xenit.actuators.services;

import eu.xenit.actuators.Health;
import eu.xenit.actuators.HealthIndicator;
import eu.xenit.actuators.model.gen.AlfrescoInfo;
import eu.xenit.actuators.model.gen.LicenseInfo;
import eu.xenit.actuators.model.gen.ModuleInfo;
import eu.xenit.actuators.model.gen.StatusInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.admin.RepoAdminService;
import org.alfresco.service.cmr.audit.AuditService;
import org.alfresco.service.cmr.module.ModuleDetails;
import org.alfresco.service.cmr.module.ModuleService;
import org.alfresco.service.cmr.thumbnail.ThumbnailService;
import org.alfresco.service.descriptor.Descriptor;
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
public class AlfrescoInfoService implements HealthIndicator {

    private static final Logger logger = LoggerFactory.getLogger(AlfrescoInfoService.class);

    @Autowired
    private ServiceRegistry serviceRegistry;
    @Autowired
    @Qualifier("DescriptorService")
    private DescriptorService descriptorService;
    @Autowired
    private ModuleService moduleService;
    @Autowired(required=false)
    private LicenseService licenseService;
    @Autowired
    @Qualifier("global-properties")
    private Properties globalProperties;
    @Autowired
    private ApplicationContext appContext;
    @Autowired
    private RepoAdminService repoAdminService;
    @Autowired
    private AuditService auditService;
    @Autowired
    private ThumbnailService thumbnailService;
    @Autowired
    private RetryingTransactionHelper retryingTransactionHelper;


    AlfrescoInfo getAlfrescoInfo() {

        ManifestInfo manifestInfo = ManifestInfo.getInstance();
        String id = null;
        String version = null;
        String edition = null;
        LicenseInfo license = null;
        Descriptor serverDescriptor = descriptorService.getServerDescriptor();
        Descriptor repositoryDescriptor = descriptorService.getCurrentRepositoryDescriptor();
        id = repositoryDescriptor.getId();
        version = serverDescriptor.getVersion();
        edition = serverDescriptor.getEdition();
        license = retrieveLicenseInfo();

        return new AlfrescoInfo()
                .id(id)
                .version(version)
                .warManifest(manifestInfo.getManifestProperties())
                .edition(edition)
                .license(license)
                .modules(this.retrieveAlfrescoModules())
//                .globalProperties(this.retrievePropertiesFiltered())
                .status(this.retrieveStatusInfo());
    }


    String getVersion() {
        Descriptor serverDescriptor = descriptorService.getServerDescriptor();
        return serverDescriptor.getVersion();
    }

    String getEdition() {
        Descriptor serverDescriptor = descriptorService.getServerDescriptor();
        return serverDescriptor.getEdition();
    }

    Map<String, String> getManifestProperties() {
        return ManifestInfo.getInstance().getManifestProperties();
    }

    // Not used
    private Map<String, String> retrievePropertiesFiltered() {
        final String PROP_STARTS_WITH = "prefix.properties.filtered";

        String propsShouldStartWith = globalProperties.getProperty(PROP_STARTS_WITH);
        if (propsShouldStartWith == null) {
            logger.error("Property '{}' is not set, you'll see no properties...", PROP_STARTS_WITH);
            return null;
        }

        Map<String, String> map = new TreeMap<>();

        for (Map.Entry<Object,Object> entry : globalProperties.entrySet()) {
            String key = entry.getKey().toString();
            if (key.startsWith(propsShouldStartWith)
                    && !map.containsKey(key)) { // the first wins
                map.put(key,entry.getValue().toString());
            }
        }

        return map;
    }


    private LicenseInfo retrieveLicenseInfo() {
        if(licenseService==null) {
            licenseService=appContext.getBeansOfType(LicenseService.class).get("licenseService");
        }
        LicenseDescriptor licenseDescriptor = descriptorService.getLicenseDescriptor();

        // community
        if(licenseService == null || licenseDescriptor == null) {
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

    // needs authentication
    private StatusInfo retrieveStatusInfo() {
        final StatusInfo statusInfo = new StatusInfo();

        retryingTransactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Object>() {

            @Override
            public Object execute() throws Throwable {
                AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Object>() {

                    @Override
                    public Object doWork() throws Exception {
                        statusInfo
                                .readOnly(repoAdminService.getUsage().isReadOnly())
                                .auditEnabled(auditService.isAuditEnabled())
                                .thumbnailGeneration(thumbnailService.getThumbnailsEnabled());

                        return null;
                    }
                }, AuthenticationUtil.getAdminUserName());
                return null;
            }
        },true);

        return statusInfo;

    }

    private List<ModuleInfo> retrieveAlfrescoModules() {

        List<ModuleDetails> allModules = moduleService.getAllModules();
        List<ModuleInfo> list = new ArrayList<>(allModules.size());

        for(ModuleDetails details : allModules) {
            list.add(toAlfrescoModule(details));
        }

        return list;
    }

    private static ModuleInfo toAlfrescoModule(ModuleDetails moduleDetails) {
        return new ModuleInfo()
                .title(moduleDetails.getTitle())
                .description(moduleDetails.getDescription())
                .installState(moduleDetails.getInstallState().toString())
                .version(moduleDetails.getProperties().getProperty(ModuleDetails.PROP_VERSION));
    }

    @Override
    public Health isHealthy() {
        Health health = new Health();
        try {
            AlfrescoInfo alfrescoInfo = getAlfrescoInfo();
            health.setStatus("UP");
            health.setDetails(Collections.singletonMap("output",alfrescoInfo.toString()));
        } catch (Exception e) {
            health.setStatus("DOWN");
            health.setDetails(Collections.singletonMap("error",e.getMessage()));
        }
        return health;
    }
}
