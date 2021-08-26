package eu.xenit.actuators.services;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;
import javax.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManifestInfo {

    private static Logger logger = LoggerFactory.getLogger(ManifestInfo.class);

    private static ManifestInfo instance;

    private ManifestInfo() {
    }

    public static ManifestInfo getInstance() {
        if (instance == null) {
            instance = new ManifestInfo();
        }
        return instance;
    }

    private Map<String, String> manifestProperties;

    public Map<String, String> getManifestProperties() {
        if (manifestProperties == null || manifestProperties.isEmpty()) {
            logger.error("Requesting MANIFEST properties but these are not (yet) set");
        }
        return manifestProperties;
    }

    public void setManifestProperties(final ServletContext servletContext) {
        if (manifestProperties != null && !manifestProperties.isEmpty()) {
            logger.debug("MANIFEST properties already loaded");
            return;
        }

        final String name = "/META-INF/MANIFEST.MF";
        final Properties props = new Properties();
        try {
            props.load(servletContext.getResourceAsStream(name));


            manifestProperties = new TreeMap<>();

            for (Entry<Object,Object> entry : props.entrySet()) {
                manifestProperties.put(entry.getKey().toString(),entry.getValue().toString());
            }
            if (manifestProperties.isEmpty()) {
                logger.debug("MANIFEST info set but still empty");
            }
        } catch (final IOException e) {
            logger.error("Unable to retrieve MANIFEST properties", e);
        }
    }
}
