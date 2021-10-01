package eu.xenit.actuators.webscripts.classical;

import static eu.xenit.actuators.Health.KEY_ERROR;

import eu.xenit.actuators.Health;
import eu.xenit.actuators.HealthIndicator;
import eu.xenit.actuators.HealthStatus;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

public class HealthWebScript extends DeclarativeWebScript implements ManifestSettingWebScript {

    private static final Logger logger = LoggerFactory.getLogger(HealthWebScript.class);

    @Autowired
    ApplicationContext applicationContext;

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        setManifestProperties(req);
        final Map<String, Object> model = new HashMap<>();
        model.put("health", "UP");

        String message = "";
        Map<String, HealthIndicator> indicators = (Map) applicationContext.getBeansOfType(HealthIndicator.class);
        for (HealthIndicator indicator : indicators.values()) {
            Health health = indicator.isHealthy();
            if (health.getStatus().equals(HealthStatus.DOWN)) {
                model.put("health", health.getStatus());
                status.setCode(Status.STATUS_INTERNAL_SERVER_ERROR);
                status.setMessage(health.getDetails().get(KEY_ERROR));
                break;
            }
        }
        model.put("message", message);

        return model;
    }
}
