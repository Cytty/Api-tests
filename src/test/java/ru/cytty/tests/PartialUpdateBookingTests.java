package ru.cytty.tests;

import io.qameta.allure.*;
import lombok.ToString;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

@Severity(SeverityLevel.NORMAL)
@Story("Change positions on a booking")
@Feature("Tests for changes to the booking")
@ToString
public class PartialUpdateBookingTests extends BaseTest {
    final static Logger logger = LoggerFactory.getLogger(PartialUpdateBookingTests.class);

    @BeforeAll
    static void beforeSuit() {
        logger.info("Start of PartialUpdateBookingTests");
    }

    @AfterAll
    static void afterSuit() {
        logger.info("End of PartialUpdateBookingTests");
    }
    @Step("Deleting a booking")
    @AfterEach
    void tearDown() {
        given()
                .log()
                .all()
                .header("Cookie", "token=" + token)
                .when()
                .delete("/booking/" + id)
                .prettyPeek()
                .then().statusCode(201);
        logger.info("The booking " + id + " deleted");
    }

    @Test
    @io.qameta.allure.Muted
    @Step("Changing the lastname and the firstname on the booking")
    void partialUpdateBookingLastAndFirstnameChangePositiveTest() {
        logger.info("Start of tests 'Changing the lastname and the firstname on the booking'");
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body(requestAccount.withFirstname("Bob").withLastname("Gray"))
                .when()
                .patch("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("firstname", equalTo("Bob"))
                .body("lastname", equalTo("Gray"));
        logger.info("End of tests 'Changing the lastname and the firstname on the booking'");
    }

    @Test
    @io.qameta.allure.Muted
    @Step("Changing the firstname on the booking")
    void partialUpdateBookingFirstnameChangePositiveTest() {
        logger.info("Start of tests 'Changing the firstname on the booking'");
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body(requestAccount.withFirstname("Bob"))
                .when()
                .patch("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("firstname", equalTo("Bob"));
        logger.info("End of tests 'Changing the firstname on the booking'");
    }

    @Test
    @io.qameta.allure.Muted
    @Step("Changing the lastname on the booking")
    void partialUpdateBookingLastnameChangePositiveTest() {
        logger.info("Start of tests 'Changing the lastname on the booking'");
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body(requestAccount.withLastname("Gray"))
                .when()
                .patch("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("lastname", equalTo("Gray"));
        logger.info("End of tests 'Changing the lastname on the booking'");
    }

    @Test
    @io.qameta.allure.Muted
    @Step("Changing the totalprice on the booking")
    void partialUpdateBookingTotalpriceChangePositiveTest() {
        logger.info("Start of tests 'Changing the totalprice on the booking'");
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body(requestAccount.withTotalprice(2000))
                .when()
                .patch("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("totalprice", equalTo(2000));
        logger.info("End of tests 'Changing the totalprice on the booking'");
    }

    @Test
    @io.qameta.allure.Muted
    @Step("Changing the depositpaid on the booking")
    void partialUpdateBookingDepositpaidChangePositiveTest() {
        logger.info("Start of tests 'Changing the depositpaid on the booking'");
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body(requestAccount.withDepositpaid(false))
                .when()
                .patch("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("depositpaid", equalTo(false));
        logger.info("End of tests 'Changing the depositpaid on the booking'");
    }

    @Test
    @io.qameta.allure.Muted
    @Step("Changing the bookingdates on the booking")
    void partialUpdateBookingAllBookingdatesChangePositiveTest() {
        logger.info("Start of tests 'Changing the bookingdates on the booking'");
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body(requestAccount.withBookingdates(requestBookingdates.withCheckin("2018-01-15").withCheckout("2018-01-15")))
                .when()
                .patch("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("bookingdates.checkin", equalTo("2018-01-15"))
                .body("bookingdates.checkout", equalTo("2018-01-15"));
        logger.info("End of tests 'Changing the bookingdates on the booking'");
    }

    @Test
    @io.qameta.allure.Muted
    @Step("Changing the checkin on the booking")
    void partialUpdateBookingOnlyCheckinChangePositiveTest() {
        logger.info("Start of tests 'Changing the checkin on the booking'");
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body(requestAccount.withBookingdates(requestBookingdates.withCheckin("2018-01-15")))
                .when()
                .patch("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("bookingdates.checkin", equalTo("2018-01-15"));
        logger.info("End of tests 'Changing the checkin on the booking'");
    }

    @Test
    @io.qameta.allure.Muted
    @Step("Changing the checkout on the booking")
    void partialUpdateBookingOnlyCheckoutChangePositiveTest() {
        logger.info("Start of tests 'Changing the checkout on the booking'");
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body(requestAccount.withBookingdates(requestBookingdates.withCheckout("2019-01-15")))
                .when()
                .patch("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("bookingdates.checkout", equalTo("2019-01-15"));
        logger.info("End of tests 'Changing the checkout on the booking'");
    }
}

