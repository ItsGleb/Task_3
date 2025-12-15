package ru.education_services.stellarburgers.main_page_test;

import io.github.bonigarcia.wdm.WebDriverManager;
import jdk.jfr.Description;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ru.education_services.stellarburgers.driver_factory.DriverFactory;
import ru.education_services.stellarburgers.general_interface.GeneralMethods;
import ru.education_services.stellarburgers.page_object_model.AuthorizationPage;
import ru.education_services.stellarburgers.page_object_model.MainPage;
import ru.education_services.stellarburgers.page_object_model.ProfilePage;

import java.time.Duration;

import java.util.Map;


import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.education_services.stellarburgers.constants.Constants.MAIN_PAGE;

public class MainPageTest {
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
        //WebDriverManager.chromedriver().setup();
        //driver = new ChromeDriver();
        GeneralMethods.registrationRequest("test06-email@yandex.ru", "password", "Test");
        // Открыли браузер
        driver.get(MAIN_PAGE);

    }

    @Test
    @DisplayName("Переход в личный кабинет")
    @Description("Проверка перехода в личный кабинет с главной страницы")
    public void personalAccountUsingButtonOnMainPageTest() throws Exception {

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
        AuthorizationPage objAuthorizationPage = new AuthorizationPage(driver);
        // Заполняем форму логин и заходим в аккаунт
        objAuthorizationPage.fillOutTheFormAndClickOnEnter(GeneralMethods.getRegistrationMapData("email"),
                GeneralMethods.getRegistrationMapData("password"));
        objMainPage.clickOnPersonalAccountButton();
        ProfilePage objProfilePage = new ProfilePage(driver);
        // Проверяем переход в личный кабинет
        assertTrue(objProfilePage.isProfileLoad(), "Отсутствует переход в личный кабинет");
        accessToken = (String) ((JavascriptExecutor) driver).executeScript(
                "return localStorage.getItem(arguments[0]);", "accessToken");
    }

    @ParameterizedTest(name = "Проверка перехода по табе [{index}]: {0}")
    @DisplayName("Проверка переходов по табам")
    @Description("Нажимаем на вкладки в таком порядке Булки ---> Соусы ---> Начинка")
    @CsvSource({"Булки", "Соусы", "Начинка"})
    public void routeTabsTest(String tab) {
        MainPage objMainPage = new MainPage(driver);
        if (tab.equals("Булки")) {
            // Чтобы элемент стал кликабельным
            objMainPage.clickOnFillingButton();
        }
        assertTrue(objMainPage.isOneOfTabBunSaucesFillingDisplayed(tab), "Переход не отработал");
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
