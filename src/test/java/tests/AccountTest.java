package tests;

import io.restassured.response.Response;
import models.AuthData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;
import java.util.Random;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static spec.Specification.createResponseSpec;
import static spec.Specification.requestSpec;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AccountTest extends TestBase {

    private static final String NEW_USER = "new_user";
    private static final String RANDOM_USER = "test_user_";
    private static final String BAD_PASS = "bad_pass";
    private static final String TEST_PASS = "!123Abc!";

    private static final AuthData createUser = new AuthData();
    private Response authResponse;

    @Test
    @DisplayName("1. Тест на создание нового пользователя с невалидным паролем")
    @Order(1)
    void createNewUserWithBadPass() {
        step("Задаем тестовый логин и тестовый невалидный пароль", () -> {
            createUser.setUserName(NEW_USER);
            createUser.setPassword(BAD_PASS);
        });

        Response createResponse = step("Регистрация пользователя", () -> given(requestSpec)
                .body(createUser)
                .when()
                .post(USER_URI)
                .then()
                .log().status()
                .log().body()
                .spec(createResponseSpec(400))
                .extract().response());

        step("Проверка результата", () -> {
            assertEquals("1300", createResponse.path("code"));
            String message = createResponse.path("message");
            assertTrue(message.contains("Passwords must have at least one non alphanumeric character"));
        });
    }

    @Test
    @DisplayName("2. Тест на создание нового пользователя")
    @Order(2)
    void createNewUserSuccess() {
        step("Задаем тестовый логин и тестовый пароль", () -> {
            Random random = new Random();
            int randomNumber = random.nextInt(90000) + 10000;

            createUser.setUserName(RANDOM_USER + randomNumber);
            createUser.setPassword(TEST_PASS);
        });

        Response createResponse = step("Запрос на регистрацию пользователя", () -> given(requestSpec)
                .body(createUser)
                .when()
                .post(USER_URI)
                .then()
                .log().status()
                .log().body()
                .spec(createResponseSpec(201))
                .extract().response());

        step("Проверка результата", () -> {
            assertNotNull(createResponse.path("userID"));
            assertEquals(createUser.getUserName(), createResponse.path("username"));
            List<String> books = createResponse.path("books");
            assertEquals(0, books.size());
        });
    }

    @Test
    @DisplayName("3. Тест на повторное создание пользователя")
    @Order(3)
    void createDuplicateUser() {
        Response createResponse = step("Запрос на повторную регистрацию пользователя", () -> given(requestSpec)
                .body(createUser)
                .when()
                .post(USER_URI)
                .then()
                .log().status()
                .log().body()
                .spec(createResponseSpec(406))
                .extract().response());

        step("Проверка результата", () -> {
            assertEquals("1204", createResponse.path("code"));
            assertEquals("User exists!", createResponse.path("message"));
        });
    }

    @Test
    @DisplayName("4. Тест на генерацию токена для созданного")
    @Order(4)
    void generateToken() {
        Response generateToken = step("Запрос генерацию токена", () -> given(requestSpec)
                .body(createUser)
                .when()
                .post(GENERATE_TOKEN_URI)
                .then()
                .log().status()
                .log().body()
                .spec(createResponseSpec(200))
                .extract().response());

        step("Проверка результата", () -> {
            assertNotNull(generateToken.path("token"));
            createUser.setToken(generateToken.path("token"));
            assertEquals("Success", generateToken.path("status"));
            assertEquals("User authorized successfully.", generateToken.path("result"));
        });
    }

    @Test
    @DisplayName("5. Тест на прохождение авторизации созданным пользователем")
    @Order(5)
    void loginUser() {
        authResponse = step("Запрос на авторизацию пользователя", () -> given(requestSpec)
                .body(createUser)
                .when()
                .post(AUTHORIZED_URI)
                .then()
                .log().status()
                .log().body()
                .spec(createResponseSpec(200))
                .extract().response());

        step("Проверка результата", () -> {
            assertNotNull(authResponse.path("userId"));
            createUser.setUserID(authResponse.path("userId"));
            assertNotNull(authResponse.path("token"));
            assertEquals(createUser.getUserName(), authResponse.path("username"));
            assertEquals(createUser.getPassword(), authResponse.path("password"));
        });
    }

    @Test
    @DisplayName("6. Тест на получение информации о созданном пользователе")
    @Order(6)
    void getUserInfo() {
        Response getUserInfo = step("Запрос на получение информации о пользователе", () -> given(requestSpec)
                .header("Authorization", "Bearer " + createUser.getToken())
                .log().headers()
                .pathParam("UUID", createUser.getUserID())
                .when()
                .get(USER_URI + UUID_PARAM)
                .then()
                .log().status()
                .log().body()
                .spec(createResponseSpec(200))
                .extract().response());

        step("Проверка результата", () -> {
            assertEquals(createUser.getUserID(), getUserInfo.path("userId"));
            assertEquals(createUser.getUserName(), getUserInfo.path("username"));
        });
    }

    @Test
    @DisplayName("7. Тест на удаление пользователя")
    @Order(7)
    void deleteUser() {
        Response deleteResponse = step("Запрос на удаление пользователя", () -> given(requestSpec)
                .header("Authorization", "Bearer " + createUser.getToken())
                .pathParam("UUID", createUser.getUserID())
                .when()
                .delete(USER_URI + UUID_PARAM)
                .then()
                .log().status()
                .log().body()
                .spec(createResponseSpec(204))
                .extract().response());
    }

    @Test
    @DisplayName("8. Тест на повторное удаление пользователя")
    @Order(8)
    void deleteUserAgain() {
        Response deleteResponse = step("Запрос на удаление пользователя", () -> given(requestSpec)
                .header("Authorization", "Bearer " + createUser.getToken())
                .pathParam("UUID", createUser.getUserID())
                .when()
                .delete(USER_URI + UUID_PARAM)
                .then()
                .log().status()
                .log().body()
                .spec(createResponseSpec(200))
                .extract().response());

        step("Проверка результата", () -> {
            assertEquals("1207", deleteResponse.path("code"));
            assertEquals("User Id not correct!", deleteResponse.path("message"));
        });
    }
}
