package eu.xenit.actuators.webscripts.classical;

import eu.xenit.actuators.HealthIndicator;
import eu.xenit.actuators.model.gen.HealthInfo;
import org.springframework.extensions.webscripts.Status;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HealthDetailsWebScript extends AbstractHealthWebScript {
    @Override
    protected List<HealthInfo> executeHealth(Map<String, HealthIndicator> indicators, Status status) {
        return indicators
                .values()
                .stream()
                .map(HealthIndicator::isHealthy)
                .collect(Collectors.toList());
    }
}
