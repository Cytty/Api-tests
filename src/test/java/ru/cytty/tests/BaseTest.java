package ru.cytty.tests;

import com.github.javafaker.Faker;
import io.qameta.allure.Story;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import lombok.ToString;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.cytty.dao.BookingdatesRequest;
import ru.cytty.dao.CreateAccountRequest;
import ru.cytty.dao.CreateTokenRequest;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@ToString

public abstract class BaseTest {
    final static Logger logger = LoggerFactory.getLogger(BaseTest.class);
    protected static final String PROPERTIES_FILE_PATH = "src/test/resources/application.properties";
    protected static CreateTokenRequest request;
    protected static Properties properties = new Properties();
    protected static BookingdatesRequest requestBookingdates;
    static CreateAccountRequest requestAccount;
    static SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
    static Faker faker = new Faker();
    static String token;
    String id;

    @BeforeAll
    static void beforeAll() throws IOException {
        logger.info("Preparing authorization data");
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        logger.info("Connecting the filter for the Allure report");
        RestAssured.filters(new AllureRestAssured());
        logger.info("Connecting a properties");
        properties.load(new FileInputStream(PROPERTIES_FILE_PATH));
        RestAssured.baseURI = properties.getProperty("base.url");
        request = CreateTokenRequest.builder()
                .username(properties.getProperty("username"))
                .password(properties.getProperty("password"))
                .build();
        logger.info(properties.getProperty("username"));
        logger.info(properties.getProperty("password"));
        logger.info(request.toString()); //не срабатывает
        logger.info("request.toString may not work");
        requestBookingdates = BookingdatesRequest.builder()
                .checkin(formater.format(faker.date().birthday().getDate()))
                .checkout(formater.format(faker.date().birthday().getDate()))
                .build();
        logger.info(requestBookingdates.toString()); //не срабатывает
        logger.info("requestBookingdates.toString may not work");
        requestAccount = CreateAccountRequest.builder()
                .firstname(faker.name().fullName())
                .lastname(faker.name().lastName())
                .totalprice(faker.hashCode())
                .depositpaid(true)                          //оставила специально, чтобы случайное значение не совпало с заменяемым
                .bookingdates(requestBookingdates)
                .additionalneeds(faker.chuckNorris().fact())
                .build();
        logger.info(requestAccount.toString()); //не срабатывает
        logger.info("requestAccount.toString may not work");
        token = given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .body(request)
                .expect()
                .statusCode(200)
                .body("token", is(not(nullValue())))
                .when()
                .post("/auth")
                .prettyPeek()
                .body()
                .jsonPath()
                .get("token")
                .toString();
        logger.info("token = " + token);
    }
    @Story("Create a booking")
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
        logger.info("Booking number " + id + " created");
    }
}
