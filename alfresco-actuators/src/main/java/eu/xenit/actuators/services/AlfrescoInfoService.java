package eu.xenit.actuators.services;

import eu.xenit.actuators.Health;
import eu.xenit.actuators.HealthDetailsError;
import eu.xenit.actuators.HealthIndicator;
import eu.xenit.actuators.HealthStatus;
import eu.xenit.actuators.model.gen.AlfrescoInfo;
import eu.xenit.actuators.model.gen.ModuleInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

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
    @Qualifier("DescriptorService")
    private DescriptorService descriptorService;
    @Autowired
    private ModuleService moduleService;
    @Autowired
    @Qualifier("global-properties")
    private Properties globalProperties;

    AlfrescoInfo getAlfrescoInfo() {

        ManifestInfo manifestInfo = ManifestInfo.getInstance();
        Descriptor serverDescriptor = descriptorService.getServerDescriptor();
        Descriptor repositoryDescriptor = descriptorService.getCurrentRepositoryDescriptor();
        String id = repositoryDescriptor.getId();
        String version = serverDescriptor.getVersion();
        String edition = serverDescriptor.getEdition();

        return new AlfrescoInfo()
                .id(id)
                .version(version)
                .warManifest(manifestInfo.getManifestProperties())
                .edition(edition)
                .modules(this.retrieveAlfrescoModules())
                .globalProperties(this.retrieveFilteredProperties());
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

    private Map<String, String> retrieveFilteredProperties() {
        final String PROP_FILTERED_PREFIX = "prefix.properties.filtered";

        String propFilteredPrefix = globalProperties.getProperty(PROP_FILTERED_PREFIX);
        if (Objects.isNull(propFilteredPrefix)) {
            logger.debug("Property '{}' is not set, you'll see no properties...", PROP_FILTERED_PREFIX);
            return Collections.emptyMap();
        }
        return globalProperties.entrySet().stream()
                .filter(entry -> entry.getKey().toString().startsWith(propFilteredPrefix))
                .collect(
                        Collectors.toMap(
                                entry -> entry.getKey().toString(),
                                entry -> entry.getValue().toString()));
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
            health.setDetails(getAlfrescoInfo());
            health.setStatus(HealthStatus.UP);
        } catch (Exception exception) {
            health.setStatus(HealthStatus.DOWN);
            health.setDetails(new HealthDetailsError(exception.getMessage()));
        }
        return health;
    }
}
