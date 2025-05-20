package eu.xenit.actuators;

import eu.xenit.actuators.model.gen.HealthInfo;
import eu.xenit.actuators.model.gen.StatusEnum;
import java.util.Map;


public abstract class HealthIndicator {
    public HealthInfo isHealthy() {
        HealthInfo health = new HealthInfo();
        health.setName(this.getClass().getSimpleName());
        try {
            health.setDetails(ghd());
            health.setStatus(StatusEnum.UP);
        } catch (Exception exception) {
            health.setStatus(StatusEnum.DOWN);
            health.setError(exception.getMessage());
        }
        return health;
    }
    private Map<String, Object> ghd() throws Exception {
        return Map.of("testing", getHealthDetails());

    }


    protected abstract Object getHealthDetails() throws Exception;
}
