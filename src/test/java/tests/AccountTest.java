package tests;

import io.restassured.response.Response;
import models.AuthData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccountTest extends TestBase {

    @Test
    @DisplayName("Тест на создание нового пользователя с невалидным паролем")
    void createNewUserWithBadPass() {
        AuthData user = new AuthData();

        step("Задаем тестовый логин и тестовый невалидный пароль", () -> {
            user.setUserName(NEW_USER);
            user.setPassword(BAD_PASS);
        });

        Response createResponse = step("Регистрация пользователя", () -> createUser(user, 400));

        step("Проверка результата", () -> {
            assertEquals("1300", createResponse.path("code"));
            String message = createResponse.path("message");
            assertTrue(message.contains("Passwords must have at least one non alphanumeric character"));
        });
    }

    @Test
    @DisplayName("Тест на создание нового пользователя")
    void createNewUserSuccess() {
        AuthData user = new AuthData();

        step("Задаем тестовый логин и тестовый пароль", () -> {
            user.setUserName(createTestUserName());
            user.setPassword(TEST_PASS);
        });

        Response createResponse = step("Регистрация пользователя", () -> createUser(user, 201));

        step("Проверка результата", () -> {
            assertNotNull(createResponse.path("userID"));
            assertEquals(user.getUserName(), createResponse.path("username"));
            List<String> books = createResponse.path("books");
            assertEquals(0, books.size());
        });
    }

    @Test
    @DisplayName("Тест на повторное создание пользователя")
    void createDuplicateUser() {
        AuthData user = new AuthData();

        step("Задаем тестовый логин и тестовый пароль", () -> {
            user.setUserName(createTestUserName());
            user.setPassword(TEST_PASS);
        });

        step("Регистрация пользователя", () -> createUser(user, 201));

        Response createResponse = step("Регистрация пользователя", () -> createUser(user, 406));

        step("Проверка результата", () -> {
            assertEquals("1204", createResponse.path("code"));
            assertEquals("User exists!", createResponse.path("message"));
        });
    }

    @Test
    @DisplayName("Тест на генерацию токена для созданного пользователя")
    void generateToken() {
        AuthData user = new AuthData();

        step("Задаем тестовый логин и тестовый пароль", () -> {
            user.setUserName(createTestUserName());
            user.setPassword(TEST_PASS);
        });

        step("Регистрация пользователя", () -> createUser(user, 201));

        Response generateToken = step("Запрос генерацию токена", () -> generateToken(user));

        step("Проверка результата", () -> {
            assertNotNull(generateToken.path("token"));
            assertEquals("Success", generateToken.path("status"));
            assertEquals("User authorized successfully.", generateToken.path("result"));
        });
    }

    @Test
    @DisplayName("Тест на прохождение авторизации созданным пользователем")
    void loginUser() {
        AuthData user = new AuthData();

        step("Задаем тестовый логин и тестовый пароль", () -> {
            user.setUserName(createTestUserName());
            user.setPassword(TEST_PASS);
        });

        step("Регистрация пользователя", () -> createUser(user, 201));

        step("Запрос генерацию токена", () -> generateToken(user));

        Response loginUserResponse = step("Запрос на авторизацию пользователя", () -> loginUser(user));

        step("Проверка результата", () -> {
            assertNotNull(loginUserResponse.path("userId"));
            assertNotNull(loginUserResponse.path("token"));
            assertEquals(user.getUserName(), loginUserResponse.path("username"));
            assertEquals(user.getPassword(), loginUserResponse.path("password"));
        });
    }

    @Test
    @DisplayName("Тест на получение информации о созданном пользователе")
    void getUserInfo() {
        AuthData user = new AuthData();

        step("Задаем тестовый логин и тестовый пароль", () -> {
            user.setUserName(createTestUserName());
            user.setPassword(TEST_PASS);
        });

        step("Регистрация пользователя", () -> createUser(user, 201));

        step("Запрос генерацию токена", () -> generateToken(user));

        Response loginUserResponse = step("Запрос на авторизацию пользователя", () -> loginUser(user));

        String userId = loginUserResponse.path("userId");
        String token = loginUserResponse.path("token");

        Response getUserInfo = step("Запрос на получение информации о пользователе", () -> getUserInfo(token, userId));

        step("Проверка результата", () -> {
            assertEquals(userId, getUserInfo.path("userId"));
            assertEquals(user.getUserName(), getUserInfo.path("username"));
        });
    }

    @Test
    @DisplayName("Тест на удаление пользователя")
    void deleteUser() {
        AuthData user = new AuthData();

        step("Задаем тестовый логин и тестовый пароль", () -> {
            user.setUserName(createTestUserName());
            user.setPassword(TEST_PASS);
        });

        step("Регистрация пользователя", () -> createUser(user, 201));

        step("Запрос генерацию токена", () -> generateToken(user));

        Response loginUserResponse = step("Запрос на авторизацию пользователя", () -> loginUser(user));

        String userId = loginUserResponse.path("userId");
        String token = loginUserResponse.path("token");

        step("Запрос на удаление пользователя", () -> deleteUser(token, userId, 204));
    }

    @Test
    @DisplayName("Тест на повторное удаление пользователя")
    void deleteUserAgain() {
        AuthData user = new AuthData();

        step("Задаем тестовый логин и тестовый пароль", () -> {
            user.setUserName(createTestUserName());
            user.setPassword(TEST_PASS);
        });

        step("Регистрация пользователя", () -> createUser(user, 201));

        step("Запрос генерацию токена", () -> generateToken(user));

        Response loginUserResponse = step("Запрос на авторизацию пользователя", () -> loginUser(user));

        String userId = loginUserResponse.path("userId");
        String token = loginUserResponse.path("token");

        step("Запрос на удаление пользователя", () -> deleteUser(token, userId, 204));

        Response deleteResponse = step("Повторный запрос на удаление пользователя", () -> deleteUser(token, userId, 200));

        step("Проверка результата", () -> {
            assertEquals("1207", deleteResponse.path("code"));
            assertEquals("User Id not correct!", deleteResponse.path("message"));
        });
    }
}
