package ru.cytty.tests;

import io.qameta.allure.*;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@Severity(SeverityLevel.MINOR)
@Story("Delete a booking")
@Feature("Tests for booking deletion")

public class DeleteBookingTests extends BaseTest {


    @Test
    @io.qameta.allure.Muted
    @Step("Deleting a booking with a cookie")
    void DeleteBookingCookieAuthPositiveTest() {
        given()
                .log()
                .all()
                .header("Cookie", "token=" + token)
                .when()
                .delete("/booking/" + id)
                .prettyPeek()
                .then().statusCode(201);
    }

    @Test
    @io.qameta.allure.Muted
    @Step("Deleting a booking with a token")
    void DeleteBookingAuthorizationPositiveTest() {
        given()
                .log()
                .all()
                .header("Authorization", "Basic YWRtaW46cGFzc3dvcmQxMjM=")
                .when()
                .delete("/booking/" + id)
                .prettyPeek()
                .then().statusCode(201);
    }

    @Test
    @io.qameta.allure.Muted
    @Step("Deleting a booking  without authorisation")
    void DeleteBookingWithoutAuthNegativeTest() {
        given()
                .log()
                .all()
                .when()
                .delete("/booking/" + id)
                .prettyPeek()
                .then().statusCode(403);

    }
}
