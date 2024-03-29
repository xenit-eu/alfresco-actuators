package eu.xenit.actuators.integrationtesting;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class RestAssuredTest {

    private static final Logger logger = LoggerFactory.getLogger(RestAssuredTest.class);

    @BeforeAll
    public static void initializeRestAssured() {
        logger.info("Initializing REST-Assured for smoke tests");

        final String baseURI = "http://" + System.getProperty("alfresco.host", "localhost");
        RestAssured.baseURI = baseURI;
        int port = Integer.parseInt(System.getProperty("alfresco.tcp.8080", "8080"));
        RestAssured.port = port;
        final String basePath = "/alfresco";
        RestAssured.basePath = basePath;

        logger.info("REST-Assured initialized with following URI: {}:{}{}", baseURI, port, basePath);
    }

}
