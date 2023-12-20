package eu.xenit.actuators.webscripts.classical;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.xenit.actuators.HealthIndicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractHealthWebScript extends DeclarativeWebScript implements ManifestSettingWebScript {
    private static final Logger logger = LoggerFactory.getLogger(AbstractHealthWebScript.class);

    @Autowired
    protected ApplicationContext applicationContext;
    protected final ObjectMapper objectMapper = new ObjectMapper();

    AbstractHealthWebScript() {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    protected Map<String, HealthIndicator> getHealthIndicators() {
        return applicationContext.getBeansOfType(HealthIndicator.class);
    }

    protected Map<String, Object> fillModel(Status status, Object object) {
        final Map<String, Object> model = new HashMap<>();
        try {
            model.put("json", objectMapper.writeValueAsString(object));
        } catch (JsonProcessingException e) {
            String message = "Exception writing health to json";
            logger.error(message, e);
            status.setCode(Status.STATUS_INTERNAL_SERVER_ERROR, "Exception writing health to json");
        }
        return model;
    }

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest request, Status status, Cache cache) {
        setManifestProperties(request);
        Map<String, HealthIndicator> indicators = getHealthIndicators();
        String disableParam = request.getParameter("disable");
        if (disableParam != null && !disableParam.isEmpty()) {
            for (String indicatorName : disableParam.split(",")) {
                if (indicators.containsKey(indicatorName)) {
                    logger.debug("disabling indicator with name {}", indicatorName);
                    indicators.remove(indicatorName);
                } else {
                    logger.info("indicator with name {} was not found nothing will be removed", indicatorName);
                }
            }
        }
        return fillModel(status, executeHealth(indicators, status));
    }

    protected abstract Object executeHealth(Map<String, HealthIndicator> indicators, Status status);
}
