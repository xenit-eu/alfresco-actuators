package eu.xenit.actuators;

import eu.xenit.actuators.model.gen.HealthInfo;
import eu.xenit.actuators.model.gen.StatusEnum;


public abstract class HealthIndicator {
    public HealthInfo isHealthy() {
        HealthInfo health = new HealthInfo();
        health.setName(this.getClass().getSimpleName());
        try {
            health.setDetails(getHealthDetails());
            health.setStatus(StatusEnum.UP);
        } catch (Exception exception) {
            health.setStatus(StatusEnum.DOWN);
            health.setError(exception.getMessage());
        }
        return health;
    }

    protected abstract Object getHealthDetails() throws Exception;
}
