package eu.xenit.actuators.webscripts.classical;

import eu.xenit.actuators.HealthIndicator;
import eu.xenit.actuators.services.ManifestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.servlet.WebScriptServletRequest;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class HealthWebScript extends DeclarativeWebScript {

    private static final Logger logger = LoggerFactory.getLogger(HealthWebScript.class);

    @Autowired
    ApplicationContext applicationContext;

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        WebScriptServletRequest webScriptServletRequest = (WebScriptServletRequest) req;
        ServletContext servletContext = webScriptServletRequest.getHttpServletRequest().getSession().getServletContext();
        ManifestInfo.getInstance().setManifestProperties(servletContext);
        final Map<String, Object> model = new HashMap();
        model.put("health", "UP");


        LinkedHashMap<String, HealthIndicator> indicators = (LinkedHashMap) applicationContext.getBeansOfType(HealthIndicator.class);
        for (HealthIndicator indicator : indicators.values()) {
            try {
                indicator.isHealthy();
            } catch (Exception e) {
                logger.error("Caught exception " + e.getMessage());
                e.printStackTrace();
                model.put("health", "DOWN");
                status.setCode(Status.STATUS_INTERNAL_SERVER_ERROR);
                status.setMessage(e.getMessage());
            }
        }

        return model;
    }
}
