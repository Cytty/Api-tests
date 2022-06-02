package ru.cytty.tests;

import io.qameta.allure.*;
import lombok.ToString;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.given;

@Severity(SeverityLevel.MINOR)
@Story("Delete a booking")
@Feature("Tests for booking deletion")
@ToString

public class DeleteBookingTests extends BaseTest {
    final static Logger logger = LoggerFactory.getLogger(DeleteBookingTests.class);

    @BeforeAll
    static void beforeSuit() {
        logger.info("Start of DeleteBookingTests");
    }

    @AfterAll
    static void afterSuit() {
        logger.info("End of DeleteBookingTests");
    }

    @Test
    @io.qameta.allure.Muted
    @Step("Deleting a booking with a cookie")
    void deleteBookingCookieAuthPositiveTest() {
        logger.info("Start of tests 'Deleting a booking with a cookie'");
        given()
                .log()
                .headers()
                //.all()
                .header("Cookie", "token=" + token)
                .when()
                .delete("/booking/" + id)
                // .prettyPeek()
                .then().statusCode(201);
        logger.info("End of tests 'Deleting a booking with a cookie'");
    }

    @Test
    @io.qameta.allure.Muted
    @Step("Deleting a booking with a token")
    void deleteBookingAuthorizationPositiveTest() {
        logger.info("Start of tests 'Deleting a booking with a token'");
        given()
                .log()
                .all()
                .header("Authorization", "Basic YWRtaW46cGFzc3dvcmQxMjM=")
                .when()
                .delete("/booking/" + id)
                .prettyPeek()
                .then().statusCode(201);
        logger.info("End of tests 'Deleting a booking with a token'");
    }

    @Test
    @io.qameta.allure.Muted
    @Step("Deleting a booking  without authorisation")
    void deleteBookingWithoutAuthNegativeTest() {
        logger.info("Start of tests 'Deleting a booking  without authorisation'");
        given()
                .log()
                .all()
                .when()
                .delete("/booking/" + id)
                .prettyPeek()
                .then().statusCode(403);
        logger.info("End of tests 'Deleting a booking  without authorisation'");
    }

}
