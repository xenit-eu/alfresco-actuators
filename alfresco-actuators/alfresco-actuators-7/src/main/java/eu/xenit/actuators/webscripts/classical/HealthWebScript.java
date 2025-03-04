package eu.xenit.actuators.webscripts.classical;

import eu.xenit.actuators.HealthIndicator;
import eu.xenit.actuators.model.gen.HealthInfo;
import eu.xenit.actuators.model.gen.HealthStatus;
import eu.xenit.actuators.model.gen.StatusEnum;
import org.springframework.extensions.webscripts.Status;

import java.util.Map;

public class HealthWebScript extends AbstractHealthWebScript {


    @Override
    protected HealthStatus executeHealth(Map<String, HealthIndicator> indicators, Status status) {
        HealthStatus healthStatus = new HealthStatus();
        healthStatus.setStatus(StatusEnum.UP);
        for (HealthIndicator indicator : indicators.values()) {
            HealthInfo health = indicator.isHealthy();
            if (StatusEnum.DOWN.equals(health.getStatus())) {
                healthStatus.setStatus(health.getStatus());
                status.setCode(Status.STATUS_INTERNAL_SERVER_ERROR);
                healthStatus.setMessage(health.getName() + " failed with error: " + health.getError());
                status.setMessage(health.getError());
                break;
            }
        }
        return healthStatus;
    }

}
