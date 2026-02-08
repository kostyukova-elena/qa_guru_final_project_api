package tests;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import models.AuthData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static spec.Specification.createResponseSpec;
import static spec.Specification.requestSpec;

public class TestBase {

    protected static final String USER_URI = "/Account/v1/User";
    protected static final String AUTHORIZED_URI = "/Account/v1/Login";
    protected static final String GENERATE_TOKEN_URI = "/Account/v1/GenerateToken";
    protected static final String UUID_PARAM = "/{UUID}";
    protected static final String NEW_USER = "new_user";
    protected static final String RANDOM_USER = "test_user_";
    protected static final String BAD_PASS = "bad_pass";
    protected static final String TEST_PASS = "!123Abc!";

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://demoqa.com";
    }

    @BeforeEach
    void setupAllure() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    protected String createTestUserName() {
        Random random = new Random();
        int randomNumber = random.nextInt(90000) + 10000;

        return RANDOM_USER + randomNumber;
    }

    protected Response createUser(AuthData user, int statusCode) {
        return given(requestSpec)
                .body(user)
                .when()
                .post(USER_URI)
                .then()
                .log().status()
                .log().body()
                .spec(createResponseSpec(statusCode))
                .extract().response();
    }

    protected Response generateToken(AuthData user) {
        return given(requestSpec)
                .body(user)
                .when()
                .post(GENERATE_TOKEN_URI)
                .then()
                .log().status()
                .log().body()
                .spec(createResponseSpec(200))
                .extract().response();
    }

    protected Response loginUser(AuthData user) {
        return given(requestSpec)
                .body(user)
                .when()
                .post(AUTHORIZED_URI)
                .then()
                .log().status()
                .log().body()
                .spec(createResponseSpec(200))
                .extract().response();
    }

    protected Response getUserInfo(String token, String userId) {
        return given(requestSpec)
                .header("Authorization", "Bearer " + token)
                .log().headers()
                .pathParam("UUID", userId)
                .when()
                .get(USER_URI + UUID_PARAM)
                .then()
                .log().status()
                .log().body()
                .spec(createResponseSpec(200))
                .extract().response();
    }

    protected Response deleteUser(String token, String userId, int statusCode) {
        return given(requestSpec)
                .header("Authorization", "Bearer " + token)
                .pathParam("UUID", userId)
                .when()
                .delete(USER_URI + UUID_PARAM)
                .then()
                .log().status()
                .log().body()
                .spec(createResponseSpec(statusCode))
                .extract().response();
    }

}
