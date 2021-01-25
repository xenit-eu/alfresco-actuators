package eu.xenit.actuators.integrationtesting;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

class ActuatorsEndpointSmokeTest extends RestAssuredTest {

    @Test
    void testDEEndpoint() {
/*        final Object healthInfo = given()
                .log().ifValidationFails()
                .when()
                .get("s/alfresco/s/xenit/api/v1/health/information")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .extract()
                .jsonPath()
                .get("system");

        System.out.println("healthInfo=" + healthInfo);*/
    }

    @Test
    void testClassicalEndpoint() {
/*        final String healthInfo = given()
                .log().ifValidationFails()
                .when()
                .get("s/alfresco/s/xenit/actuators/health")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .extract()
                .body()
                .toString();

        System.out.println("healthInfo=" + healthInfo);*/
    }

}
