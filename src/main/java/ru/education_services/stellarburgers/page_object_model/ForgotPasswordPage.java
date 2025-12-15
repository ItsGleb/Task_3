package ru.education_services.stellarburgers.page_object_model;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import ru.education_services.stellarburgers.general_interface.GeneralMethods;

public class ForgotPasswordPage {
    WebDriver driver;
    // Кнопка "Войти"
    private By enterButton = By.xpath("//main//a");

    public ForgotPasswordPage(WebDriver driver) {
        this.driver = driver;
    }

    @Step("Клик на кнопку 'Войти'")
    public void clickOnEnterButton(){
        GeneralMethods.waitingElement(enterButton,3,driver);
        driver.findElement(enterButton).click();
    }
}
