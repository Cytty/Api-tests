
package ru.cytty.tests;

import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.cytty.dao.CreateTokenRequest;
import ru.cytty.dao.CreateTokenResponse;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
@Severity(SeverityLevel.BLOCKER)
@Story("create a booking")
@Feature("Tests to get a token")

public class CreateTokenBookingTests {
    private static final String PROPERTIES_FILE_PATH = "src/test/resources/application.properties";
    private static CreateTokenRequest request;
    private static CreateTokenResponse response;
    static Properties properties = new Properties();


    @BeforeAll
    static void beforeAll() throws IOException {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        //RestAssured.filters(new AllureRestAssured());
        properties.load(new FileInputStream(PROPERTIES_FILE_PATH));
        RestAssured.baseURI = properties.getProperty("base.url");
        request = CreateTokenRequest.builder()
                .username(properties.getProperty("username"))
                .password(properties.getProperty("password"))
                .build();
    }

    @Test
    //@Description("Creating a token with correct authorisation")
   @Step("Creating a token with correct authorisation")
    void createTokenPositiveTest() {
        response = given()
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .header("Content-Type", "application/json")
                .body(request)
                .expect()
                .statusCode(200)
                .when() // шаг(и)
                .post("/auth")
                .prettyPeek()
                .then()
                .extract()
                .as(CreateTokenResponse.class);
        assertThat(response.getToken().length(), equalTo(15));
    }

    @Test
   // @Step("Creating a token with an incorrect password")
    @Description("Creating a token with an incorrect password")
    void createTokenWithAWrongPasswordNegativeTest() {
        given() //предусловия, подготовка
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .header("Content-Type", "application/json")
                .body(request.withPassword("password"))
                .when()
                .post("/auth")
                .prettyPeek()
                .then() //проверки
                .statusCode(200)
                .body("reason", equalTo("Bad credentials"));
    }

    @Test
    @Step("Creating a token with an incorrect username")
    void createTokenWithAWrongUsernameAndPasswordNegativeTest() {
        Response response = given() //предусловия, подготовка
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .header("Content-Type", "application/json")
                .body(request.withUsername(properties.getProperty("username") + "123"))
                .when() // шаг(и)
                .post("/auth")
                .prettyPeek();
        assertThat(response.statusCode(), equalTo(200));
        assertThat(response.body().jsonPath().get("reason"), containsStringIgnoringCase("Bad credentials"));
    }


    //оставила для памяти с комментариями
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
                .body(request)
                .expect()                                              // ПРОВЕРЯЕМ
                .statusCode(200)                                            // -статус код
                .body("token", CoreMatchers.is(CoreMatchers.not(nullValue())))       // -тело
                .when()                                                // ЧТО ДЕЛАЕМ (шаги)
                .post("/auth")                                          // -пост запрос
                .prettyPeek();                                         //ЛОГИРУЕМ ОТВЕТ

    }
}
