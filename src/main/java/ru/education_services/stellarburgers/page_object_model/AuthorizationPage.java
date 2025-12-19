package ru.education_services.stellarburgers.page_object_model;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import ru.education_services.stellarburgers.general_interface.GeneralMethods;

public class AuthorizationPage {

    private WebDriver driver;
    // Заголовок формы авторизации
    private By authorizationHeader = By.xpath("//div[@class='Auth_login__3hAey']/h2");
    // Поле ввода email
    private By inputEmailField = By.xpath("//label[text()='Email']/following-sibling::input");
    // Поле ввода пароля
    private By inputPasswordField = By.xpath("//label[text()='Пароль']/following-sibling::input");
    // Кнопка войти
    private By enterButton = By.xpath("//button[text()='Войти']");

    public AuthorizationPage(WebDriver driver) {
        this.driver = driver;
    }

    @Step("Проверяем загрузился ли заголовок страницы")
    public boolean isAuthorizationHeaderDisplayed() {
        GeneralMethods.waitingElement(authorizationHeader, 3, driver);
        boolean isDisplayed = driver.findElement(authorizationHeader).isDisplayed();
        return isDisplayed;
    }
    @Step("Клик и ввод валидного адреса электронной почты пользователя")
    public void clickAndEnteringUserValidEmail(String email) {
        if (email.contains("@") && email.contains(".")) {
            driver.findElement(inputEmailField).sendKeys(email);
        } else throw new IllegalArgumentException("Переданный в метод email некорректный");
    }

    @Step("Клик и ввод пароля пользователя")
    public void clickAndEnteringUserPassword(String password) {
        if (password != null || password != "") {
            driver.findElement(inputPasswordField).sendKeys(password);
        } else throw new IllegalArgumentException("Переданный в метод password некорректный");
    }
    @Step("Нажатие на кнопку 'Войти'")
    public void clickOnTheRegisterButton() {
        driver.findElement(enterButton).click();
    }
    @Step("Шаг: Заполнить форму и нажать 'Войти'")
    public void fillOutTheFormAndClickOnEnter(String email, String password) {
        clickAndEnteringUserValidEmail(email);
        clickAndEnteringUserPassword(password);
        clickOnTheRegisterButton();
    }
}
