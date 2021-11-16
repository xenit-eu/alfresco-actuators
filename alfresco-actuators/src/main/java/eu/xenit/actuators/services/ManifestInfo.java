package eu.xenit.actuators.services;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManifestInfo {

    private static final Logger logger = LoggerFactory.getLogger(ManifestInfo.class);

    private static ManifestInfo instance;

    private final Map<String, String> manifestProperties = new ConcurrentHashMap<>();

    private ManifestInfo() {
    }

    public static ManifestInfo getInstance() {
        if (instance == null) {
            instance = new ManifestInfo();
        }
        return instance;
    }


    public Map<String, String> getManifestProperties() {
        if (manifestProperties.isEmpty()) {
            logger.error("Requesting MANIFEST properties but these are not (yet) set");
        }
        return manifestProperties;
    }

    public void setManifestProperties(final ServletContext servletContext) {

        final String name = "/META-INF/MANIFEST.MF";
        final Properties props = new Properties();

        try {
            props.load(servletContext.getResourceAsStream(name));
        } catch (IOException e) {
            logger.error("Unable to retrieve MANIFEST properties", e);
            return;
        }

        if (!manifestProperties.isEmpty()) {
            logger.debug("MANIFEST properties already loaded");
            return;
        }

        for (Entry<Object, Object> entry : props.entrySet()) {
            manifestProperties.putIfAbsent(entry.getKey().toString(), entry.getValue().toString());
        }
        if (manifestProperties.isEmpty()) {
            logger.debug("MANIFEST info set but still empty");
        }
    }
}
