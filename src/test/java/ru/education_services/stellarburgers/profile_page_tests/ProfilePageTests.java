package ru.education_services.stellarburgers.profile_page_tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import jdk.jfr.Description;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import ru.education_services.stellarburgers.driver_factory.DriverFactory;
import ru.education_services.stellarburgers.general_interface.GeneralMethods;
import ru.education_services.stellarburgers.page_object_model.AuthorizationPage;
import ru.education_services.stellarburgers.page_object_model.MainPage;
import ru.education_services.stellarburgers.page_object_model.ProfilePage;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.education_services.stellarburgers.constants.Constants.*;

public class ProfilePageTests {
    private WebDriver driver;
    private String accessToken;
    MainPage objMainPage;
    private By byElement;
    private DriverFactory driverFactory;
    @BeforeEach
    public void setup() {
        // Создаем экземпляр фабрики
        driverFactory = new DriverFactory();

        // Инициализируем драйвер через фабрику
        driverFactory.initDriver();
        driver = driverFactory.getDriver();
        //WebDriverManager.chromedriver().setup();
        //driver = new ChromeDriver();
        GeneralMethods.registrationRequest("test06-email@yandex.ru", "password", "Test");
        driver.get(LOGIN_PAGE);
        AuthorizationPage objAuthorizationPage = new AuthorizationPage(driver);
        // Заполняем форму логин и заходим в аккаунт
        objAuthorizationPage.fillOutTheFormAndClickOnEnter(GeneralMethods.getRegistrationMapData("email"),
                GeneralMethods.getRegistrationMapData("password"));
        // Пытался перейти по прямому адресу в профиль, но ссылка нерабочая
        objMainPage = new MainPage(driver);
        objMainPage.clickOnPersonalAccountButton();
        byElement = By.xpath("//div[@class='Account_contentBox__2CPm3']/div");
        GeneralMethods.waitingElement(byElement, 10, driver);
        accessToken = (String) ((JavascriptExecutor) driver).executeScript(
                "return localStorage.getItem(arguments[0]);", "accessToken");
    }

    @Test
    @DisplayName("Проверка работы логотипа стеллар бургер из личного кабинета")
    @Description("Логотип стеллар бургер должен навигировать на главную страницу")
    public void routeToMainPageFromProfileTest() {
        ProfilePage objProfilePage = new ProfilePage(driver);
        objProfilePage.clickOnStellarBurgersLogo();
        assertTrue(objMainPage.isMakeBurgerHeaderDisplyed(), "Переход на главную не отработал");
    }

    @Test
    @DisplayName("Проверка работы кнопки 'Конструктор' из личного кабинета")
    @Description("По клику на кнопку конструктор должен быть переход на главную страницу")
    public void routeToMainPageFromProfileUsingConstructorButtonTest() {
        ProfilePage objProfilePage = new ProfilePage(driver);
        objProfilePage.clickOnTheConstructorButton();
        assertTrue(objMainPage.isMakeBurgerHeaderDisplyed(), "Переход на главную не отработал");
    }

    @Test
    @DisplayName("Проверка работы кнопки 'Выход' из личного кабинета")
    @Description("По клику на кнопку 'Выход' должен быть переход на страницу авторизации")
    public void logoutTest() throws InterruptedException {
        ProfilePage objProfilePage = new ProfilePage(driver);
        objProfilePage.clickOnTheExitButton();
        AuthorizationPage objAuthorizationPage = new AuthorizationPage(driver);
        assertTrue(objAuthorizationPage.isAuthorizationHeaderDisplayed(), "Нет перехода на страницу авторизации");
        String token = (String) ((JavascriptExecutor) driver).executeScript(
                "return localStorage.getItem(arguments[0]);", "accessToken");
        assertTrue(token == null, "Токен присутствует в localStorage");

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
