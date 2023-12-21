package eu.xenit.actuators.integrationtesting;

import eu.xenit.actuators.model.gen.HealthInfo;
import eu.xenit.actuators.model.gen.StatusEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;


class ActuatorsEndpointSmokeTest extends RestAssuredTest {
    @Test
    void testClassicalEndpoint() {
        final HealthInfo healthInfo = given()
                .log().ifValidationFails()
                .when()
                .get("/s/xenit/actuators/health")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .extract().body().as(HealthInfo.class);
        Assertions.assertEquals(StatusEnum.UP, healthInfo.getStatus());
    }

    @Test
    void testDetailsEndpoint() {
        final ArrayList<HealthInfo> healthInfos = given()
                .log().ifValidationFails()
                .auth().preemptive().basic("admin", "admin")
                .when()
                .get("/s/xenit/actuators/health/details")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .extract().body().as(HealthInfoList.class);
        Assertions.assertTrue(healthInfos
                .stream()
                .map(HealthInfo::getName)
                .collect(Collectors.toSet())
                .containsAll(
                        Set.of("SystemInfoService",
                                "AlfrescoInfoService",
                                "ContentInfoService",
                                "LicenseInfoService",
                                "StatusInfoService")));
    }

    @Test
    void testDisabledDetailsEndpoint() {
        final ArrayList<HealthInfo> healthInfos = given()
                .log().ifValidationFails()
                .auth().preemptive().basic("admin", "admin")
                .when()
                .get("/s/xenit/actuators/health/details?disabled=ContentInfoService,AlfrescoInfoService")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .extract().body().as(HealthInfoList.class);
        Set<String> names = healthInfos
                .stream()
                .map(HealthInfo::getName)
                .collect(Collectors.toSet());
        Assertions.assertTrue(names
                .containsAll(
                        Set.of("SystemInfoService",
                                "LicenseInfoService",
                                "StatusInfoService")));
        Assertions.assertFalse(names.contains("AlfrescoInfoService"));
        Assertions.assertFalse(names.contains("ContentInfoService"));

    }

    public static class HealthInfoList extends ArrayList<HealthInfo> {

    }
}
