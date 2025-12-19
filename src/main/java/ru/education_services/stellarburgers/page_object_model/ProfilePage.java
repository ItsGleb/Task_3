package ru.education_services.stellarburgers.page_object_model;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import ru.education_services.stellarburgers.general_interface.GeneralMethods;

public class ProfilePage {
    WebDriver driver;

    // Текстовка профиля
    private By profileTexting = By.xpath("//main//p");
    // Блок персональных данных пользователя
    private By personalDataBlock = By.xpath("//div[@class='Account_contentBox__2CPm3']/div");
    // Логотип бургера
    private By stellarBurgersLogo = By.xpath("//nav/div");
    // Кнопка "Конструктор"
    private By constructorButton = By.xpath("//nav//p[text()='Конструктор']");
    // Кнопка "Выход"
    private By exitButton = By.xpath("//main//button[text()='Выход']");

    public ProfilePage(WebDriver driver) {
        this.driver = driver;
    }

    @Step("Проверка загрузки блока персональных данных пользователя")
    public boolean isProfileLoad() {
        GeneralMethods.waitingElement(personalDataBlock, 10, driver);
        return driver.findElement(personalDataBlock).isDisplayed();
    }
    @Step("Клик на логотип стеллар бургерс")
    public void clickOnStellarBurgersLogo(){
        GeneralMethods.waitingElement(stellarBurgersLogo,3,driver);
        driver.findElement(stellarBurgersLogo).click();
    }
    @Step("Клик на конструктор")
    public void clickOnTheConstructorButton(){
        GeneralMethods.waitingElement(constructorButton,3,driver);
        driver.findElement(constructorButton).click();
    }
    @Step("Клик на 'Выход'")
    public void clickOnTheExitButton(){
        GeneralMethods.waitingElement(exitButton,3,driver);
        driver.findElement(exitButton).click();
    }
}
