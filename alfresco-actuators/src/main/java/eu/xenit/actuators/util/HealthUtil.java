package eu.xenit.actuators.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.xenit.actuators.Health;
import eu.xenit.actuators.HealthIndicator;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HealthUtil {

    public static String processHealthIndicatorsToJson(Collection<HealthIndicator> indicators, ObjectMapper objectMapper)
            throws JsonProcessingException {
        List<Map<String, Health>> healthList = indicators.stream()
                .map(it -> {
                    Map<String, Health> healthMap = new HashMap<>();
                    healthMap.put(it.getClass().getName(), it.isHealthy());
                    return healthMap;
                }).collect(Collectors.toList());
        return objectMapper.writeValueAsString(healthList);
    }

}
