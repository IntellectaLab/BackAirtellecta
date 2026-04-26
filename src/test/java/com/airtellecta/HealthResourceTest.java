package com.airtellecta;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class HealthResourceTest {

    @Test
    void testPingEndpoint() {
        given()
            .when().get("/api/ping")
            .then()
            .statusCode(200)
            .body("status", is("ok"))
            .body("app", is("AirTellecta"));
    }
}
