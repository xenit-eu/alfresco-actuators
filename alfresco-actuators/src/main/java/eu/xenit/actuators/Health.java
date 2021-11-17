package eu.xenit.actuators;

public class Health {

    private HealthStatus status;
    private Object details;

    public Object getDetails() {
        return details;
    }

    public void setDetails(Object details) {
        this.details = details;
    }


    public HealthStatus getStatus() {
        return status;
    }

    public void setStatus(HealthStatus status) {
        this.status = status;
    }

}
