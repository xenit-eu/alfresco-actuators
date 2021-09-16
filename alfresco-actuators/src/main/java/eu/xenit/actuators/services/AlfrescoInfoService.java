package eu.xenit.actuators.services;

import static eu.xenit.actuators.Health.KEY_ERROR;
import static eu.xenit.actuators.Health.KEY_OUTPUT;

import eu.xenit.actuators.Health;
import eu.xenit.actuators.HealthIndicator;
import eu.xenit.actuators.HealthStatus;
import eu.xenit.actuators.model.gen.AlfrescoInfo;
import eu.xenit.actuators.model.gen.LicenseInfo;
import eu.xenit.actuators.model.gen.ModuleInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.module.ModuleDetails;
import org.alfresco.service.cmr.module.ModuleService;
import org.alfresco.service.descriptor.Descriptor;
import org.alfresco.service.descriptor.DescriptorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    @Autowired
    @Qualifier("global-properties")
    private Properties globalProperties;

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

        return new AlfrescoInfo()
                .id(id)
                .version(version)
                .warManifest(manifestInfo.getManifestProperties())
                .edition(edition)
                .modules(this.retrieveAlfrescoModules());
//                .globalProperties(this.retrievePropertiesFiltered())
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

        for (Map.Entry<Object, Object> entry : globalProperties.entrySet()) {
            String key = entry.getKey().toString();
            if (key.startsWith(propsShouldStartWith)
                    && !map.containsKey(key)) { // the first wins
                map.put(key, entry.getValue().toString());
            }
        }

        return map;
    }

    private List<ModuleInfo> retrieveAlfrescoModules() {

        List<ModuleDetails> allModules = moduleService.getAllModules();
        List<ModuleInfo> list = new ArrayList<>(allModules.size());

        for (ModuleDetails details : allModules) {
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
            health.setDetails(Collections.singletonMap(KEY_OUTPUT, alfrescoInfo.toString()));
            health.setStatus(HealthStatus.UP);
        } catch (Exception healthException) {
            health.setStatus(HealthStatus.DOWN);
            health.setDetails(Collections.singletonMap(KEY_ERROR, healthException.getMessage()));
        }
        return health;
    }
}
