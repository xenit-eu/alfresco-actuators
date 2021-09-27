package eu.xenit.actuators.webscripts.classical;

import static eu.xenit.actuators.webscripts.classical.HealthgroupsWebScript.REQUEST_PATH_PARAM_TARGETGROUP;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.xenit.actuators.Health;
import eu.xenit.actuators.HealthIndicator;
import eu.xenit.actuators.HealthStatus;
import eu.xenit.actuators.services.HealthGroupService;
import eu.xenit.actuators.util.HealthUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

public class HealthgroupsdetailsWebScript extends DeclarativeWebScript implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(HealthgroupsdetailsWebScript.class);

    @Autowired
    private HealthGroupService healthGroupService;

    private ObjectMapper objectMapper;

    @Override
    public void afterPropertiesSet() throws Exception {
        objectMapper = new ObjectMapper();
    }

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest request, Status status, Cache cache) {
        Map<String, Object> model = new HashMap<>();
        String json = "{}";

        String targetGroup = request.getServiceMatch().getTemplateVars().get(REQUEST_PATH_PARAM_TARGETGROUP);
        List<HealthIndicator> indicatorList = healthGroupService.getHealthIndicatorsForGroup(targetGroup);
        try {
            json = HealthUtil.processHealthIndicatorsToJson(indicatorList, objectMapper);
        } catch (JsonProcessingException e) {
            String message = "Exception writing health to json";
            logger.error(message, e);
            status.setCode(Status.STATUS_INTERNAL_SERVER_ERROR, "Exception writing health to json");
        }
        model.put("json", json);
        return model;
    }

}
