package eu.xenit.actuators.integrationtesting;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ActuatorsEndpointSmokeTest extends RestAssuredTest {
    @Test
    void testClassicalEndpoint() {
        final String healthInfo = given()
                .log().ifValidationFails()
                .when()
                .get("/s/xenit/actuators/health")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body(containsString("{\"status\":\"UP\"}"))
                .toString();
    }

}
