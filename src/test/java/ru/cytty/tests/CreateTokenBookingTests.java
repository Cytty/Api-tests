package ru.cytty.tests;

import io.qameta.allure.*;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.ToString;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.cytty.dao.CreateTokenRequest;
import ru.cytty.dao.CreateTokenResponse;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsStringIgnoringCase;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@Severity(SeverityLevel.BLOCKER)
@Story("create a booking")
@Feature("Tests to get a token")
@ToString

public class CreateTokenBookingTests {
    final static Logger logger = LoggerFactory.getLogger(CreateTokenBookingTests.class);
    private static final String PROPERTIES_FILE_PATH = "src/test/resources/application.properties";
    private static CreateTokenRequest request;
    private static CreateTokenResponse response;
    static Properties properties = new Properties();


    @BeforeAll
     static void beforeSuit() throws IOException {
        logger.info("Preparing authorization data");
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        logger.info("Connecting the filter for the Allure report");
        RestAssured.filters(new AllureRestAssured());
        logger.info("Connecting a properties");
        properties.load(new FileInputStream(PROPERTIES_FILE_PATH));
        baseURI = properties.getProperty("base.url");
        request = CreateTokenRequest.builder()
                .username(properties.getProperty("username"))
                .password(properties.getProperty("password"))
                .build();
        logger.info(properties.getProperty("username"));
        logger.info(properties.getProperty("password"));
        //logger.info(request.toString()); не срабатывает
        logger.info("Start of token tests");
    }

    @AfterAll
    static void afterSuit(){
        logger.info("End of token tests");
    }

    @Test
    @io.qameta.allure.Muted
    @Step("Creating a token with correct authorisation")
    void createTokenPositiveTest() {
        logger.info("Start of tests 'Creating a token with correct authorisation'");
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
        logger.info("End of tests 'Creating a token with correct authorisation'");
    }

    @Test
    @io.qameta.allure.Muted
    @Step("Creating a token with an incorrect password")
    void createTokenWithAWrongPasswordNegativeTest() {
        logger.info("Start of tests 'Creating a token with an incorrect password'");
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
        logger.info("End of tests 'Creating a token with an incorrect password'");
    }

    @Test
    @io.qameta.allure.Muted
    @Step("Creating a token with an incorrect username")
    void createTokenWithAWrongUsernameAndPasswordNegativeTest() {
        logger.info("Start of tests 'Creating a token with an incorrect username'");
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
        logger.info("End of tests 'Creating a token with an incorrect username'");
    }
}
