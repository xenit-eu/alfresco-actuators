package eu.xenit.actuators.webscripts.classical;

import eu.xenit.actuators.Health;
import eu.xenit.actuators.HealthIndicator;
import eu.xenit.actuators.HealthStatus;
import eu.xenit.actuators.services.HealthGroupService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

public class HealthgroupsWebScript extends DeclarativeWebScript {

    private static final Logger logger = LoggerFactory.getLogger(HealthgroupsWebScript.class);

    protected static final String REQUEST_PATH_PARAM_TARGETGROUP = "targetGroup";

    @Autowired
    private HealthGroupService healthGroupService;

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest request, Status status, Cache cache) {
        String json = "{}";
        Map<String, Object> model = new HashMap<>();

        String targetGroup = request.getServiceMatch().getTemplateVars().get(REQUEST_PATH_PARAM_TARGETGROUP);
        List<HealthIndicator> indicatorList = healthGroupService.getHealthIndicatorsForGroup(targetGroup);
        HealthStatus groupStatus = HealthStatus.UP;
        for (HealthIndicator indicator : indicatorList) {
            Health currentHealth = indicator.isHealthy();
            if (currentHealth.getStatus().equals(HealthStatus.DOWN)) {
                groupStatus = HealthStatus.DOWN;
                break;
            }
        }
        json = String.format("{\"status\":\"%s\"}", groupStatus);
        model.put("json", json);
        return model;
    }
}
