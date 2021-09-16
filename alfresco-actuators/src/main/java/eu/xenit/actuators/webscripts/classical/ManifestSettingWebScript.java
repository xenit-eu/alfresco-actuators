package eu.xenit.actuators.webscripts.classical;

import eu.xenit.actuators.services.ManifestInfo;
import javax.servlet.ServletContext;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WrappingWebScriptRequest;
import org.springframework.extensions.webscripts.servlet.WebScriptServletRequest;

public interface ManifestSettingWebScript {

    default void setManifestProperties(WebScriptRequest request) {
        if (request instanceof WrappingWebScriptRequest) {
            request = ((WrappingWebScriptRequest) request).getNext();
        }
        WebScriptServletRequest webScriptServletRequest = (WebScriptServletRequest) request;
        ServletContext servletContext = webScriptServletRequest.getHttpServletRequest().getSession()
                .getServletContext();
        ManifestInfo.getInstance().setManifestProperties(servletContext);
    }

}
