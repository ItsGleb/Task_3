package ru.education_services.stellarburgers.page_object_model;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import ru.education_services.stellarburgers.general_interface.GeneralMethods;

import java.util.List;
import java.util.Locale;

public class MainPage {
    private WebDriver driver;

    // Кнопка "Войти в аккаунт"
    private By logInToYourAccount = By.xpath(("//main//button[text()='Войти в аккаунт']"));
    // Заголовок Собери Бургер
    private By makeBurgerHeader = By.xpath("//main/section[@class='BurgerIngredients_ingredients__1N8v2']");
    // Кнопка "Личный кабинет" в хедере страницы
    private By personalAccount = By.xpath("//header//p[text()='Личный Кабинет']");
    // Кнопка раздела "Булки"
    private By bunButton = By.xpath("//main//span[text()='Булки']");
    // Кнопка раздела "Соусы"
    private By saucesButton = By.xpath("//main//span[text()='Соусы']");
    // Кнопка раздела "Начинки"
    private By fillingButton = By.xpath("//main//span[text()='Начинки']");
    // Блок ингредиентов
    private By ingredientsBlock = By.xpath("//main/section//ul[@class='BurgerIngredients_ingredients__list__2A-mT']");

    public MainPage(WebDriver driver) {
        this.driver = driver;
    }

    @Step("Ждем загрузки заголовка 'Собери Бургер'")
    public boolean isMakeBurgerHeaderDisplyed() {
        GeneralMethods.waitingElement(makeBurgerHeader, 10, driver);
        return driver.findElement(makeBurgerHeader).isDisplayed();
    }

    @Step("Нажатие на кнопку 'Войти в аккаунт' на главной")
    public void clickOnLoginInToYourAccountButton() {
        driver.findElement(logInToYourAccount).click();
    }

    @Step("Нажатие на кнопку 'Личный кабинет'на главной")
    public void clickOnPersonalAccountButton() {
        driver.findElement(personalAccount).click();
    }

    @Step("Нажатие на кнопку 'Булки' на главной")
    public void clickOnBunButton() {
        GeneralMethods.waitingElement(bunButton, 3, driver);
        driver.findElement(bunButton).click();
    }

    @Step("Нажатие на кнопку 'Соусы' на главной")
    public void clickOnSaucesButton() {
        GeneralMethods.waitingElement(saucesButton, 3, driver);
        driver.findElement(saucesButton).click();
    }

    @Step("Нажатие на кнопку 'Начинки' на главной")
    public void clickOnFillingButton() {
        GeneralMethods.waitingElement(fillingButton, 3, driver);
        driver.findElement(fillingButton).click();
    }

    @Step("Осуществлен ли переход к разделу Булки/Соусы/Начинка")
    public boolean isOneOfTabBunSaucesFillingDisplayed(String tab) {
        tab = tab.toLowerCase();
        // Получаем весь список веб элементов
        List<WebElement> ingredients = driver.findElements(ingredientsBlock);
        WebElement bunList = ingredients.get(0);
        WebElement saucesList = ingredients.get(1);
        WebElement fillingList = ingredients.get(2);
        boolean result = false;
        switch (tab) {
            case "булки":
                clickOnBunButton();
                result = bunList.isDisplayed();
                break;
            case "соусы":
                clickOnSaucesButton();
                result = saucesList.isDisplayed();
                break;
            case "начинка":
                clickOnFillingButton();
                result = fillingList.isDisplayed();
                break;
            default:
                throw new IllegalArgumentException("Неизвестная кнопка: " + tab);
        }
        return result;
    }

}
