package ru.cytty.tests;

import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.*;
import ru.cytty.dao.CreateAccountRequest;
import ru.cytty.dao.CreateTokenRequest;
import ru.cytty.dao.BookingdatesRequest;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.text.SimpleDateFormat;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

public class PartialUpdateBookingTests {
    private static final String PROPERTIES_FILE_PATH = "src/test/resources/application.properties";
    private static CreateTokenRequest request;
    private static BookingdatesRequest requestBookingdates;
    private static CreateAccountRequest requestAccount;
    static Properties properties = new Properties();
    static SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
    static Faker faker = new Faker();
    static String token;
    String id;

    @BeforeAll
    static void beforeAll() throws IOException {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
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
                .depositpaid(true)      //оставила специально, чтобы случайное значение не совпало с заменяемым
                .bookingdates(requestBookingdates)
                .additionalneeds(faker.chuckNorris().fact())
                .build();
        token = given() //предусловия, подготовка
                .log()
                .all()
                .header("Content-Type", "application/json")
                .body(request)
                .expect()
                .statusCode(200)
                .body("token", is(CoreMatchers.not(nullValue())))
                .when()
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

    @AfterEach
    void tearDown() {
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
    void partialUpdateBookingLastAndFirstnameChangePositiveTest() {
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
    void partialUpdateBookingFirstnameChangePositiveTest() {
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
    void partialUpdateBookingLastnameChangePositiveTest() {
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
    void partialUpdateBookingTotalpriceChangePositiveTest() {
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
    void partialUpdateBookingDepositpaidChangePositiveTest() {
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
    void partialUpdateBookingAllBookingdatesChangePositiveTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body(requestAccount.withBookingdates(requestBookingdates.withCheckin("2018-01-15").withCheckout("2019-01-15")))
                .when()
                .patch("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("bookingdates.checkin", equalTo("2018-01-15"))
                .body("bookingdates.checkout", equalTo("2019-01-15"));
    }

    @Test
    void partialUpdateBookingOnlyCheckinChangePositiveTest() {
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
    void partialUpdateBookingOnlyCheckoutChangePositiveTest() {
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

