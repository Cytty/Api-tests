package ru.cytty.tests;

import com.github.javafaker.Faker;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.*;
import ru.cytty.dao.BookingdatesRequest;
import ru.cytty.dao.CreateAccountRequest;
import ru.cytty.dao.CreateTokenRequest;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
@Severity(SeverityLevel.BLOCKER)
@Story("change positions on a booking")
@Feature("Tests for changes to the booking")

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
                .depositpaid(true)                          //оставила специально, чтобы случайное значение не совпало с заменяемым
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
    @Step("Changing the bookingdates on the booking")
    void PartialUpdateBookingAllBookingdatesChangePositiveTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body(requestAccount.withBookingdates(requestBookingdates.withCheckin("2018-01-15").withCheckout("2018-01-15")))
                         // повозилась с генерацией нового значения и новыми переменными, но не справилась с приведением к единому формату.
                         // форматер отказывался работать с ГетДейт, а туСтринг не работал с форматером. Оставила так.
                .when()
                .patch("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("bookingdates.checkin", equalTo("2018-01-15"))
                .body("bookingdates.checkout", equalTo("2018-01-15"));
    }

    @Test
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

