package eu.xenit.actuators.webscripts.classical;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.xenit.actuators.Health;
import eu.xenit.actuators.HealthIndicator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

public class HealthdetailsWebScript extends DeclarativeWebScript implements ManifestSettingWebScript {

    private static final Logger logger = LoggerFactory.getLogger(HealthdetailsWebScript.class);

    private final ApplicationContext applicationContext;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public HealthdetailsWebScript(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest request, Status status, Cache cache) {
        setManifestProperties(request);
        final Map<String, Object> model = new HashMap<>();
        Map<String, HealthIndicator> indicators = applicationContext.getBeansOfType(HealthIndicator.class);
        List<Map<String, Health>> healthList = indicators.values().stream()
                .map(it -> {
                    Map<String, Health> healthMap = new HashMap<>();
                    healthMap.put(it.getClass().getName(), it.isHealthy());
                    return healthMap;
                }).collect(Collectors.toList());
        try {
            model.put("json", objectMapper.writeValueAsString(healthList));
        } catch (JsonProcessingException e) {
            String message = "Exception writing health to json";
            logger.error(message, e);
            status.setCode(Status.STATUS_INTERNAL_SERVER_ERROR, "Exception writing health to json");
        }
        return model;
    }
}
