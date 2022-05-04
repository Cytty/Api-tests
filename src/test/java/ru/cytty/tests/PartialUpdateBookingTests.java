package ru.cytty.tests;

import io.qameta.allure.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

@Severity(SeverityLevel.NORMAL)
@Story("Change positions on a booking")
@Feature("Tests for changes to the booking")

public class PartialUpdateBookingTests extends BaseTest {

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
    }

    @Test
    @io.qameta.allure.Muted
    @Step("Changing the lastname and the firstname on the booking")
    void PartialUpdateBookingLastAndFirstnameChangePositiveTest() {
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
    }

    @Test
    @io.qameta.allure.Muted
    @Step("Changing the firstname on the booking")
    void PartialUpdateBookingFirstnameChangePositiveTest() {
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
    }

    @Test
    @io.qameta.allure.Muted
    @Step("Changing the lastname on the booking")
    void PartialUpdateBookingLastnameChangePositiveTest() {
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
    }

    @Test
    @io.qameta.allure.Muted
    @Step("Changing the totalprice on the booking")
    void PartialUpdateBookingTotalpriceChangePositiveTest() {
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
    }

    @Test
    @io.qameta.allure.Muted
    @Step("Changing the depositpaid on the booking")
    void PartialUpdateBookingDepositpaidChangePositiveTest() {
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
    }

    @Test
    @io.qameta.allure.Muted
    @Step("Changing the bookingdates on the booking")
    void PartialUpdateBookingAllBookingdatesChangePositiveTest() {
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
    }

    @Test
    @io.qameta.allure.Muted
    @Step("Changing the checkin on the booking")
    void PartialUpdateBookingOnlyCheckinChangePositiveTest() {
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
    }

    @Test
    @io.qameta.allure.Muted
    @Step("Changing the checkout on the booking")
    void PartialUpdateBookingOnlyCheckoutChangePositiveTest() {
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
    }
}

