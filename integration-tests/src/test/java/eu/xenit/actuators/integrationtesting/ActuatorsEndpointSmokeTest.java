package eu.xenit.actuators.integrationtesting;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ActuatorsEndpointSmokeTest extends RestAssuredTest {

    @Test
    void testDEEndpoint() {
        final Object os = given()
                .log().ifValidationFails()
                .when()
                .get("/s/xenit/api/v1/health/information")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .extract()
                .jsonPath()
                .get("system.os.name");

        assertEquals(os,"Linux");
    }

    @Test
    void testClassicalEndpoint() {
        final String healthInfo = given()
                .log().ifValidationFails()
                .when()
                .get("/s/xenit/actuators/health")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body(containsString("Linux"))
                .toString();
    }

}
