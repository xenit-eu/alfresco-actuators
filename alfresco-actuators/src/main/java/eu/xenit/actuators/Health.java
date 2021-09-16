package eu.xenit.actuators;

import java.util.Map;

public class Health {

    public static final String KEY_OUTPUT = "output";
    public static final String KEY_ERROR = "error";

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
