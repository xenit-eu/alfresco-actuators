package eu.xenit.actuators.webscripts.classical;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.xenit.actuators.Health;
import eu.xenit.actuators.HealthIndicator;
import eu.xenit.actuators.util.HealthUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

public class HealthdetailsWebScript extends DeclarativeWebScript implements InitializingBean, ManifestSettingWebScript {

    private static final Logger logger = LoggerFactory.getLogger(HealthdetailsWebScript.class);

    @Autowired
    ApplicationContext applicationContext;

    ObjectMapper objectMapper;

    @Override
    public void afterPropertiesSet() throws Exception {
        objectMapper = new ObjectMapper();
    }

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest request, Status status, Cache cache) {
        setManifestProperties(request);
        final Map<String, Object> model = new HashMap<>();
        Map<String, HealthIndicator> indicators = (Map) applicationContext.getBeansOfType(HealthIndicator.class);
        try {
            model.put("json", HealthUtil.processHealthIndicatorsToJson(indicators.values(), objectMapper));
        } catch (JsonProcessingException e) {
            String message = "Exception writing health to json";
            logger.error(message, e);
            status.setCode(Status.STATUS_INTERNAL_SERVER_ERROR, "Exception writing health to json");
        }
        return model;
    }
}
