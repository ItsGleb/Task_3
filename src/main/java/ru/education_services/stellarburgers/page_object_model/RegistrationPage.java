package ru.education_services.stellarburgers.page_object_model;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import ru.education_services.stellarburgers.general_interface.GeneralMethods;


public class RegistrationPage {

    private WebDriver driver;

    // Поле ввода имени
    // following-sibling::input - вот эта штука выбирает соседние элементы с нужным тегом, если поставить ::* то выберет все элементы
    private By inputNameField = By.xpath("//label[text()='Имя']/following-sibling::input");
    // Поле ввода email
    private By inputEmailField = By.xpath("//label[text()='Email']/following-sibling::input");
    // Поле ввода пароля
    private By inputPasswordField = By.xpath("//label[text()='Пароль']/following-sibling::input");
    // Кнопка "Зарегистрироваться"
    private By registerButton = By.xpath("//button[text()='Зарегистрироваться']");
    // Сообщение о некорректном пароле
    private By validationErrorText = By.xpath("//p[text()='Некорректный пароль']");
    // Кнопка "Войти"
    private By enterButton = By.xpath("//p[text()='Уже зарегистрированы?']/a");

    public RegistrationPage(WebDriver driver) {
        this.driver = driver;
    }

    @Step("Клик и ввод имени пользователя")
    public void clickAndEnteringUserName(String name) {
        if (name != null || name != "") {
            driver.findElement(inputNameField).sendKeys(name);
        } else throw new IllegalArgumentException("Переданный в метод name некорректный");
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

    @Step("Нажатие на кнопку 'Зарегистрироваться'")
    public void clickOnTheRegisterButton() {
        driver.findElement(registerButton).click();
    }

    @Step("Проверяем появилось ли валидационное сообщение")
    public boolean isValidationErrorTextDisplayed() {
        GeneralMethods.waitingElement(validationErrorText, 3, driver);
        return driver.findElement(validationErrorText).isDisplayed();
    }

    @Step("Получаем значение текстовки сообщения об ошибке")
    public String getPasswordValidationErrorText() {
        String errorText = driver.findElement(validationErrorText).getText();
        return errorText;
    }
    @Step("Клик на кнопку 'Войти'")
    public void clickOnTheEnterButton(){
        driver.findElement(enterButton).click();
    }

    @Step("Шаг: Заполнить форму и нажать 'Зарегистрироваться'")
    public void fillOutTheFormAndClickOnRegister(String name, String email, String password) {
        clickAndEnteringUserName(name);
        clickAndEnteringUserValidEmail(email);
        clickAndEnteringUserPassword(password);
        clickOnTheRegisterButton();
    }
}
