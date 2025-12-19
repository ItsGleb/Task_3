package ru.education_services.stellarburgers.registration_page_tests;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Step;
import jdk.jfr.Description;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v143.network.Network;
import org.openqa.selenium.devtools.v143.network.model.Response;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.education_services.stellarburgers.driver_factory.DriverFactory;
import ru.education_services.stellarburgers.general_interface.GeneralMethods;
import ru.education_services.stellarburgers.page_object_model.AuthorizationPage;
import ru.education_services.stellarburgers.page_object_model.RegistrationPage;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.education_services.stellarburgers.constants.Constants.REGISTRATION_PAGE;

public class RegistrationPageTests {
    private WebDriver driver;
    private String accessToken;
    private DriverFactory driverFactory;
    @BeforeEach
    public void setup() throws Exception {
        // Создаем экземпляр фабрики
        driverFactory = new DriverFactory();

        // Инициализируем драйвер через фабрику
        driverFactory.initDriver();
        driver = driverFactory.getDriver();
        //WebDriverManager.chromedriver().setup();
        //driver = new ChromeDriver();
        driver.get(REGISTRATION_PAGE); // Открыли браузер
        // Подождем и проверим что страница загрузилась
        WebElement uiElement = driver.findElement(By.xpath("//h2[text()='Регистрация']"));
        new WebDriverWait(driver, Duration.ofSeconds(3))
                .until(ExpectedConditions.visibilityOf(uiElement));
        if (!driver.findElement(By.xpath("//h2[text()='Регистрация']")).isDisplayed()) {
            throw new Exception("Страница регистрации не загрузилась");
        }

    }

    @Test
    @DisplayName("Проверка ошибки валидации поля ввода пароля")
    @Description("Длина пароля >= 6 символам")
    public void validationErrorWhenPasswordLessSixCharactersTest() {
        RegistrationPage objRegistrationPage = new RegistrationPage(driver);
        String name = "Test";
        String email = "test6-email@yandex.ru";
        String password = "passw";
        String expectedValidationErrorText = "Некорректный пароль";
        objRegistrationPage.fillOutTheFormAndClickOnRegister(name, email, password);
        assertTrue(objRegistrationPage.isValidationErrorTextDisplayed(), "Сообщение об ошибки " +
                "не отобразилось");
        assertEquals(expectedValidationErrorText, objRegistrationPage.getPasswordValidationErrorText(),
                "Сообщение об ошибке неправильное");
    }

    @Test
    @DisplayName("Проверка успешной регистрации")
    @Description("Заполнение формы регистрации валидными данными и дальнейшая регистрация нового пользователя")
    public void successfulRegistrationTest() throws InterruptedException {
        RegistrationPage objRegistrationPage = new RegistrationPage(driver);
        String name = "Test";
        String email = "test06-email@yandex.ru";
        String password = "password";
        // Это нам нужно, для того чтобы получить access token из ответа на запрос регистрации
        DevTools devTools = ((ChromeDriver) driver).getDevTools();
        devTools.createSession();
        devTools.send(Network.enable(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()));
        BlockingQueue<String> q = new LinkedBlockingQueue<>();
        devTools.addListener(Network.responseReceived(), r -> {
            if (r.getResponse().getUrl().contains("auth")) {
                Network.GetResponseBodyResponse response = devTools.send(Network.getResponseBody(r.getRequestId()));
                q.add(response.getBody());
            }
        });
        // Регистрируемся
        objRegistrationPage.fillOutTheFormAndClickOnRegister(name, email, password);
        AuthorizationPage objAuthorizationPage = new AuthorizationPage(driver);
        // Получаем ответ
        String authReply = q.poll(10, TimeUnit.SECONDS);

        // Парсим строку в json объект
        JsonObject jsonObject = JsonParser.parseString(authReply).getAsJsonObject();
        // Достаем значение токена
        accessToken = jsonObject.get("accessToken").getAsString();
        // Закрываем слушалку ответов
        devTools.clearListeners();
        // Проверяем что перешли на страничку логина
        assertTrue(objAuthorizationPage.isAuthorizationHeaderDisplayed(), "Отсутствует переход на страницу" +
                "логина");
        // Проверяем что получили токен
        assertTrue(accessToken != null, "Токен не был получен. Регистрация не прошла");
    }


    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        if (accessToken != null) {
            GeneralMethods.deleteUser(accessToken);
        }

    }
}

