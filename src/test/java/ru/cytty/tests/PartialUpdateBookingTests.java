package ru.cytty.tests;

import io.restassured.RestAssured;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

public class PartialUpdateBookingTests {
    static String token;
    String id;

    @BeforeAll
    static void beforeAll() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        token = given() //предусловия, подготовка
                .log()
                .all()
                .header("Content-Type", "application/json")                   // ПОДГОТАВЛИВАЕМ ЗАГОЛОВОК
                .body("{\n"                                                         // ПОДГОТАВЛИВАЕМ ТЕЛО
                        + "     \"username\" : \"admin\",\n"
                        + "     \"password\" : \"password123\"\n"
                        + "}")
                .expect()                                   // ПРОВЕРЯЕМ
                .statusCode(200)                            // -статус код
                .body("token", is(CoreMatchers.not(nullValue())))   // -тело
                .when() // шаг(и)                           // ЧТО ДЕЛАЕМ
                .post("https://restful-booker.herokuapp.com/auth")  // -пост запрос
                .prettyPeek()                                 //ЛОГИРУЕМ ОТВЕТ
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
                .body("{\n"
                        + "    \"firstname\" : \"Jim\",\n"
                        + "    \"lastname\" : \"Brown\",\n"
                        + "    \"totalprice\" : 111,\n"
                        + "    \"depositpaid\" : true,\n"
                        + "    \"bookingdates\" : {\n"
                        + "        \"checkin\" : \"2018-01-01\",\n"
                        + "        \"checkout\" : \"2019-01-01\"\n"
                        + "    },\n"
                        + "    \"additionalneeds\" : \"Breakfast\"\n"
                        + "}")
                .expect()
                .statusCode(200)
                .when()
                .post("https://restful-booker.herokuapp.com/booking")
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
                .delete("https://restful-booker.herokuapp.com/booking/" + id)
                .prettyPeek()
                .then().statusCode(201);
    }

    @Test
    void PartialUpdateBookingLastAndFirstnameChangePositiveTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body("{\n"
                        + "    \"firstname\" : \"Bob\",\n"
                        + "    \"lastname\" : \"Gray\"\n"
                        + "}")
                .when()
                .patch("https://restful-booker.herokuapp.com/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("firstname", equalTo("Bob"))
                .body("lastname", equalTo("Gray"));

    }

    @Test
    void PartialUpdateBookingFirstnameChangePositiveTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body("{\n"
                        + "    \"firstname\" : \"Bob\"\n"
                        + "}")
                .when()
                .patch("https://restful-booker.herokuapp.com/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("firstname", equalTo("Bob"));
    }

    @Test
    void PartialUpdateBookingLastnameChangePositiveTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body("{\n"
                        + "     \"lastname\" : \"Gray\"\n"
                        + "}")
                .when()
                .patch("https://restful-booker.herokuapp.com/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("lastname", equalTo("Gray"));
    }

    @Test
    void PartialUpdateBookingTotalpriceChangePositiveTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body("{\n"
                        + "     \"totalprice\" : 2000\n"
                        + "}")
                .when()
                .patch("https://restful-booker.herokuapp.com/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("totalprice", equalTo(2000));
    }

    @Test
    void PartialUpdateBookingDepositpaidChangePositiveTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body("{\n"
                        + "    \"depositpaid\" : false\n"
                        + "}")
                .when()
                .patch("https://restful-booker.herokuapp.com/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("depositpaid", equalTo(false));
    }

    @Test
    void PartialUpdateBookingAllBookingdatesChangePositiveTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body("{\n"
                        + "\"bookingdates\" : {\n"
                        + "        \"checkin\" : \"2018-01-15\",\n"
                        + "        \"checkout\" : \"2019-01-15\"\n"
                        + "    }\n"
                        + "}")
                .when()
                .patch("https://restful-booker.herokuapp.com/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("bookingdates.checkin", equalTo("2018-01-15"))
                .body("bookingdates.checkout", equalTo("2019-01-15"));
    }

    @Test
    void PartialUpdateBookingOnlyCheckinChangePositiveTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body("{\n"
                        + "\"bookingdates\" : {\n"
                        + "        \"checkin\" : \"2018-01-15\"\n"
                        + "    }\n"
                        + "}")
                .when()
                .patch("https://restful-booker.herokuapp.com/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("bookingdates.checkin", equalTo("2018-01-15"));
    }

    @Test
    void PartialUpdateBookingOnlyCheckoutChangePositiveTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body("{\n"
                        + "\"bookingdates\" : {\n"
                        + "        \"checkout\" : \"2019-01-15\"\n"
                        + "    }\n"
                        + "}")
                .when()
                .patch("https://restful-booker.herokuapp.com/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("bookingdates.checkout", equalTo("2019-01-15"));
    }
}

