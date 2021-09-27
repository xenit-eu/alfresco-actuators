package eu.xenit.actuators;

import java.util.Map;

public class Health {

    public static final String KEY_OUTPUT = "output";
    public static final String KEY_ERROR = "error";
    public static final String GLOBAL_ACTUATORS_CONFIG_PREFIX = "eu.xenit.actuators";

    HealthStatus status;
    Map<String, String> details;

    public HealthStatus getStatus() {
        return status;
    }

    public void setStatus(HealthStatus status) {
        this.status = status;
    }

    public Map<String, String> getDetails() {
        return details;
    }

    public void setDetails(Map<String, String> details) {
        this.details = details;
    }
}
