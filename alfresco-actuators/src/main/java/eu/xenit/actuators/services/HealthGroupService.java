package eu.xenit.actuators.services;

import static eu.xenit.actuators.Health.GLOBAL_ACTUATORS_CONFIG_PREFIX;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import eu.xenit.actuators.HealthGroup;
import eu.xenit.actuators.HealthIndicator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

@Service
public class HealthGroupService implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(HealthGroupService.class);

    public static final String GLOBAL_ACTUATORS_GROUPS_CONFIG = GLOBAL_ACTUATORS_CONFIG_PREFIX.concat(".groups.configfile.path");
    private static final String ACTUATORS_GROUPS_CONFIG_ROOT = "groups";
    private static final String ACTUATORS_GROUPS_CONFIG_NAME = "name";
    private static final String ACTUATORS_GROUPS_CONFIG_INCLUDES = "includes";

    private ObjectMapper objectMapper;
    private Map<String, HealthGroup> healthGroupCache;

    @Autowired
    @Qualifier("global-properties")
    private Properties globalProperties;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private ResourceLoader resourceLoader;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.objectMapper = new ObjectMapper();
        this.healthGroupCache = new HashMap<>();
        loadGroups();
    }

    private void loadGroups() throws IOException {
        logger.info("Loading health group definitions from file.");
        String groupConfigPath = globalProperties.getProperty(GLOBAL_ACTUATORS_GROUPS_CONFIG);
        if (groupConfigPath == null || groupConfigPath.isEmpty()) {
            logger.info("No config path for actuator health groups.");
            return;
        }
        JsonNode groupConfig = objectMapper.readTree(resourceLoader.getResource(groupConfigPath).getInputStream());
        Iterator<JsonNode> groupsIterator = groupConfig.get(ACTUATORS_GROUPS_CONFIG_ROOT).iterator();
        groupsIterator.forEachRemaining(it -> {
            HealthGroup currentGroup = new HealthGroup();
            currentGroup.name = it.get(ACTUATORS_GROUPS_CONFIG_NAME).asText();
            List<String> currentIncludes = new ArrayList<String>();
            it.get(ACTUATORS_GROUPS_CONFIG_INCLUDES).iterator().forEachRemaining(include -> currentIncludes.add(include.asText()));
            currentGroup.includes = ImmutableList.copyOf(currentIncludes);
            healthGroupCache.put(currentGroup.name, currentGroup);
        });
        logger.info("Finished loading health group definitions.");
    }

    public List<HealthIndicator> getHealthIndicatorsForGroup(String targetGroup) {
        ArrayList<HealthIndicator> indicators = new ArrayList<>();
        if (healthGroupCache.containsKey(targetGroup)) {
            Map<String, HealthIndicator> beans = (Map) applicationContext.getBeansOfType(HealthIndicator.class);
            healthGroupCache.get(targetGroup).includes.stream()
                    .map(beans::get)
                    .filter(Objects::nonNull)
                    .forEach(indicators::add);
        }
        return indicators;
    }
}
