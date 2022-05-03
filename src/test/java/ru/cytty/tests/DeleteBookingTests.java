package ru.cytty.tests;

import com.github.javafaker.Faker;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.cytty.dao.BookingdatesRequest;
import ru.cytty.dao.CreateAccountRequest;
import ru.cytty.dao.CreateTokenRequest;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
@Severity(SeverityLevel.BLOCKER)
@Story("delete a booking")
@Feature("Tests for booking deletion")

public class DeleteBookingTests {
    private static final String PROPERTIES_FILE_PATH = "src/test/resources/application.properties";
    private static CreateTokenRequest request;
    private static CreateAccountRequest requestAccount;
    private static BookingdatesRequest requestBookingdates;
    static Properties properties = new Properties();

    static SimpleDateFormat formater = new SimpleDateFormat("dd-MM-yyyy");
    static Faker faker = new Faker();
    static String token;
    String id;

    @Severity(SeverityLevel.BLOCKER)
    @Story("delete a booking")
    @Feature("Tests for booking delete")


    @BeforeAll
    static void beforeAll() throws IOException {
        properties.load(new FileInputStream(PROPERTIES_FILE_PATH));
        RestAssured.baseURI = properties.getProperty("base.url");
        request = CreateTokenRequest.builder()
                .username(properties.getProperty("username"))
                .password(properties.getProperty("password"))
                .build();
        requestBookingdates = BookingdatesRequest.builder()
                .checkin(formater.format(faker.date().birthday().getDate()))
                .checkout(formater.format(faker.date().birthday().getDate()))
                .build();
        requestAccount = CreateAccountRequest.builder()
                .firstname(faker.name().fullName())
                .lastname(faker.name().lastName())
                .totalprice(faker.hashCode())
                .depositpaid(faker.bool().bool())
                .bookingdates(requestBookingdates)
                .additionalneeds(faker.chuckNorris().fact())
                .build();
        token = given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .body(request)
                .expect()
                .statusCode(200)
                .body("token", is(not(nullValue())))
                .when() // шаг(и)
                .post("/auth")
                .prettyPeek()
                .body()
                .jsonPath()
                .get("token")
                .toString();
    }

    @BeforeEach
    void setUp() {
        id = given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .body(requestAccount)
                .expect()
                .statusCode(200)
                .when()
                .post("/booking")
                .prettyPeek()
                .body()
                .jsonPath()
                .get("bookingid")
                .toString();
    }

    @Test
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
