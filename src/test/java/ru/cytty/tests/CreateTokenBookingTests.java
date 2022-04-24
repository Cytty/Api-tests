package ru.cytty.tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class CreateTokenBookingTests {

    @BeforeAll
    static void beforeAll() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void createTokenPositiveTest() {
        given() //предусловия, подготовка
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .header("Content-Type", "application/json")
                .body("{\n"
                        + "     \"username\" : \"admin\",\n"
                        + "     \"password\" : \"password123\"\n"
                        + "}")
                .when() // шаг(и)
                .post("https://restful-booker.herokuapp.com/auth")
                .prettyPeek()
                .then() //проверки
                .statusCode(200);
    }

    @Test
    void createTokenWithAWrongPasswordNegativeTest() {
        given() //предусловия, подготовка
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .header("Content-Type", "application/json")
                .body("{\n"
                        + "     \"username\" : \"admin\",\n"
                        + "     \"password\" : \"password\"\n"
                        + "}")
                .when() // шаг(и)
                .post("https://restful-booker.herokuapp.com/auth")
                .prettyPeek()
                .then() //проверки
                .statusCode(200)
                .body("reason", equalTo("Bad credentials"));
    }

    @Test
    void createTokenWithAWrongUsernameAndPasswordNegativeTest() {
        Response response = given() //предусловия, подготовка
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .header("Content-Type", "application/json")
                .body("{\n"
                        + "     \"username\" : \"admin123\",\n"
                        + "     \"password\" : \"password\"\n"
                        + "}")
                .when() // шаг(и)
                .post("https://restful-booker.herokuapp.com/auth")
                .prettyPeek();
        assertThat(response.statusCode(), equalTo(200));
        assertThat(response.body().jsonPath().get("reason"), containsStringIgnoringCase("Bad credentials"));
    }

    @Test
    void createTokenWithAWrongUsernameAndPasswordNegative2Test() {
        given() //предусловия, подготовка
                .log()                                                 // ЛОГИРУЕМ
                .method()                                                 // -метод
                .log()                                                 // ЛОГИРУЕМ
                .uri()                                                    // -uri
                .log()                                                 // ЛОГИРУЕМ
                .body()                                                    // -тело
                .header("Content-Type", "application/json")      // ПОДГОТАВЛИВАЕМ ЗАГОЛОВОК
                .body("{\n"                                            // ПОДГОТАВЛИВАЕМ ТЕЛО
                        + "     \"username\" : \"admin\",\n"
                        + "     \"password\" : \"password123\"\n"
                        + "}")
                .expect()                                              // ПРОВЕРЯЕМ
                .statusCode(200)                                            // -статус код
                .body("token", is(CoreMatchers.not(nullValue())))       // -тело
                .when()                                                // ЧТО ДЕЛАЕМ (шаги)
                .post("https://restful-booker.herokuapp.com/auth")       // -пост запрос
                .prettyPeek();                                         //ЛОГИРУЕМ ОТВЕТ

    }
}
