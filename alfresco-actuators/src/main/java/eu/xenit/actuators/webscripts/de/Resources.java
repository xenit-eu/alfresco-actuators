package eu.xenit.actuators.webscripts.de;


import com.github.dynamicextensionsalfresco.webscripts.annotations.*;
import com.github.dynamicextensionsalfresco.webscripts.support.AbstractBundleResourceHandler;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.stereotype.Component;

@Component
@WebScript(families = HealthV1Config.FAMILY, baseUri = HealthV1Config.BASE_URL)
@Authentication(AuthenticationType.NONE)
@Transaction(TransactionType.NONE)
@SuppressWarnings("unused")
public class Resources extends AbstractBundleResourceHandler {

    @Uri(value = "/swagger/static/{path}", formatStyle = FormatStyle.ARGUMENT)
    public void handleStaticResources(@UriVariable String path, final WebScriptRequest request, final WebScriptResponse response) throws Exception {

        if (path == null) {
            path = "";
        }

        handleResource(path, request, response);
    }
}
