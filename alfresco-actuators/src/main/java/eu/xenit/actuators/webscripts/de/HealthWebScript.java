package eu.xenit.actuators.webscripts.de;

import com.github.dynamicextensionsalfresco.webscripts.annotations.*;
import eu.xenit.actuators.services.HealthInfoService;
import eu.xenit.actuators.services.ManifestInfo;
import eu.xenit.actuators.system.model.gen.HealthInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

@Component
@WebScript(baseUri = HealthV1Config.BASE_URL,
        families = {HealthV1Config.FAMILY},
        defaultFormat = HealthV1Config.FORMAT_JSON,
        description = "Xenit Health WebScripts")
@Authentication(AuthenticationType.NONE)
@SuppressWarnings("unused")
public class HealthWebScript {
 @Autowired
    private HealthInfoService healthInfoService;

    @Uri(value = {"/information", "/information/"}, method = HttpMethod.GET)
    @ResponseBody
    public ResponseEntity<HealthInfo> getServerInfo(HttpServletRequest httpRequest) {

        ServletContext servletContext = httpRequest.getSession().getServletContext();
        ManifestInfo.getInstance().setManifestProperties(servletContext);

        return new ResponseEntity<>(healthInfoService.getHealthInfo(), HttpStatus.OK);
    }


}
