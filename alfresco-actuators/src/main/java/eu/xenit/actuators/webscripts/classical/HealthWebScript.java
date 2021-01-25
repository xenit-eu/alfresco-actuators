package eu.xenit.actuators.webscripts.classical;

import eu.xenit.actuators.services.HealthInfoService;
import eu.xenit.actuators.services.ManifestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.*;
import org.springframework.extensions.webscripts.servlet.WebScriptServletRequest;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Map;

public class HealthWebScript extends DeclarativeWebScript {

    private final HealthInfoService healthInfoService;
    private static final Logger logger = LoggerFactory.getLogger(HealthWebScript.class);

    public HealthWebScript(HealthInfoService healthInfoService) {
        this.healthInfoService = healthInfoService;
    }


    @Override
    protected Map<String,Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        WebScriptServletRequest webScriptServletRequest = (WebScriptServletRequest) req;
        ServletContext servletContext = webScriptServletRequest.getHttpServletRequest().getSession().getServletContext();
        ManifestInfo.getInstance().setManifestProperties(servletContext);
        final Map<String, Object> model = new HashMap();
        model.put("healthInfo", healthInfoService.getHealthInfo().toString());
        return model;
    }
}
