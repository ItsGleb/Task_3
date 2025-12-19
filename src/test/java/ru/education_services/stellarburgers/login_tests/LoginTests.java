package ru.education_services.stellarburgers.login_tests;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.bonigarcia.wdm.WebDriverManager;

import jdk.jfr.Description;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v143.network.Network;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


import ru.education_services.stellarburgers.driver_factory.DriverFactory;
import ru.education_services.stellarburgers.general_interface.GeneralMethods;
import ru.education_services.stellarburgers.page_object_model.AuthorizationPage;
import ru.education_services.stellarburgers.page_object_model.ForgotPasswordPage;
import ru.education_services.stellarburgers.page_object_model.MainPage;
import ru.education_services.stellarburgers.page_object_model.RegistrationPage;

import java.time.Duration;


import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.education_services.stellarburgers.constants.Constants.*;

public class LoginTests {
    private WebDriver driver;
    private String accessToken;

    private By byElement;
    private DriverFactory driverFactory;


    @BeforeEach
    public void setup() {
        // Создаем экземпляр фабрики
        driverFactory = new DriverFactory();

        // Инициализируем драйвер через фабрику
        driverFactory.initDriver();
        driver = driverFactory.getDriver();

        GeneralMethods.registrationRequest("test06-email@yandex.ru", "password", "Test");
    }

    @Test
    @DisplayName("Проверка входа по кнопке 'Личный кабинет'")
    @Description("Вход по кнопке 'Личный кабинет' на главной")
    public void personalAccountUsingButtonOnMainPageTest() throws Exception {
        // Открыли браузер
        driver.get(MAIN_PAGE);
        // Подождем и проверим что страница загрузилась
        byElement = By.xpath("//main/section[@class='BurgerIngredients_ingredients__1N8v2']");
        WebElement uiElement = driver.findElement(byElement);
        new WebDriverWait(driver, Duration.ofSeconds(3))
                .until(ExpectedConditions.visibilityOf(uiElement));
        if (!driver.findElement(byElement).isDisplayed()) {
            throw new Exception("Страница регистрации не загрузилась");
        }
        MainPage objMainPage = new MainPage(driver);
        objMainPage.clickOnPersonalAccountButton();
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
        AuthorizationPage objAuthorizationPage = new AuthorizationPage(driver);
        // Заполняем форму логин и заходим в аккаунт
        objAuthorizationPage.fillOutTheFormAndClickOnEnter(GeneralMethods.getRegistrationMapData("email"),
                GeneralMethods.getRegistrationMapData("password"));
        // Получаем ответ
        int timeout = 40;
        String authReply = q.poll(timeout, TimeUnit.SECONDS);
        assertNotNull(authReply, "Ответ не получен. Текущее значение тайм-аута для получения ответа = " + timeout);
        // Парсим строку в json объект
        JsonObject jsonObject = JsonParser.parseString(authReply).getAsJsonObject();
        // Достаем значение токена
        accessToken = jsonObject.get("accessToken").getAsString();
        // Закрываем слушалку ответов
        devTools.clearListeners();
        // Проверяем что получили токен
        assertTrue(accessToken != null, "Токен не был получен. Авторизация не прошла");
        // Проверяем что отобразился заголовок главной страницы
        assertTrue(objMainPage.isMakeBurgerHeaderDisplyed(), "Переход на главную страницу не работает");
    }

    @ParameterizedTest(name = "Проверка авторизации [{index}]: {0}")
    // Делаю это для того, чтобы не плодить 3 одинаковых теста у которых меняется точка входа только
    @MethodSource("dataForTheForm")
    public void loginTest(String pageName, String URL, String element) throws Exception {
        GeneralMethods.openPage(URL, element, driver);
        switch (URL) {
            case REGISTRATION_PAGE:
                System.out.println("Запуск теста " + pageName);
                RegistrationPage objRegistrationPage = new RegistrationPage(driver);
                // Переходим на форму логина
                objRegistrationPage.clickOnTheEnterButton();
                break;
            case MAIN_PAGE:
                System.out.println("Запуск теста " + pageName);
                MainPage objMainPage = new MainPage(driver);
                // Переходим на форму логина
                objMainPage.clickOnLoginInToYourAccountButton();
                break;
            case FORGOT_PASSWORD_PAGE:
                System.out.println("Запуск теста " + pageName);
                ForgotPasswordPage objForgotPasswordPage = new ForgotPasswordPage(driver);
                // Переходим на форму логина
                objForgotPasswordPage.clickOnEnterButton();
                break;
            default:
                throw new Exception("Тест не запустился");
        }
        // Проверяем что авторизация отработает
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
        AuthorizationPage objAuthorizationPage = new AuthorizationPage(driver);
        // Заполняем форму логин и заходим в аккаунт
        objAuthorizationPage.fillOutTheFormAndClickOnEnter(GeneralMethods.getRegistrationMapData("email"),
                GeneralMethods.getRegistrationMapData("password"));
        // Получаем ответ
        int timeout = 40;
        String authReply = q.poll(timeout, TimeUnit.SECONDS);
        assertNotNull(authReply,"Ответ не получен. Текущее значение тайм-аута для получения ответа = "+timeout);
        // Парсим строку в json объект
        JsonObject jsonObject = JsonParser.parseString(authReply).getAsJsonObject();
        // Достаем значение токена
        accessToken = jsonObject.get("accessToken").getAsString();
        // Закрываем слушалку ответов
        devTools.clearListeners();
        // Проверяем что получили токен
        assertTrue(accessToken != null, "Токен не был получен. Авторизация не прошла");
        // Проверяем что отобразился заголовок главной страницы
        MainPage objMainPage = new MainPage(driver);
        assertTrue(objMainPage.isMakeBurgerHeaderDisplyed(), "Переход на главную страницу не работает");
    }

    private static Stream<Arguments> dataForTheForm() {
        String registration = "//h2[text()='Регистрация']";
        String burgerHeader = "//main/section[@class='BurgerIngredients_ingredients__1N8v2']";
        String forgotPasswordHeader = "//main//h2";
        return Stream.of(
                        Arguments.of("с страницы 'регистрации'", REGISTRATION_PAGE, registration),
                        Arguments.of("с 'Главной' страницы ", MAIN_PAGE, burgerHeader),
                        Arguments.of("с страницы 'Забыли пароль'", FORGOT_PASSWORD_PAGE, forgotPasswordHeader));
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
