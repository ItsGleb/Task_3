package ru.education_services.stellarburgers.general_interface;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;


public interface GeneralMethods {
    Map<String, String> registrationMap = new HashMap<>();
    @Step("Ожидание загрузки элемента")
    static void waitingElement(By element, int waitingTime, WebDriver driver) {
        // Сначала ждем
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(waitingTime));
        // Проверяем появился элемент или нет
        wait.until(ExpectedConditions.visibilityOfElementLocated(element));

    }

    static void openPage(String URL, String elementXpath, WebDriver driver) throws Exception {
        // Открыли браузер
        driver.get(URL);
        // Подождем и проверим что страница загрузилась
        By byElement = By.xpath(elementXpath);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        wait.until(ExpectedConditions.visibilityOfElementLocated(byElement));

        if (!driver.findElement(byElement).isDisplayed()) {
            throw new Exception("Страница регистрации загрузилась");
        }
    }
    static void registrationRequest(String email, String password, String name){
        registrationMap.put("email", email);
        registrationMap.put("password", password);
        registrationMap.put("name", name);
        given()
                .header("Content-Type", "application/json")
                .body(registrationMap)
                .when()
                .post("https://stellarburgers.education-services.ru/api/auth/register");
    }
    static String getRegistrationMapData(String data){
        String mapData = registrationMap.get(data);
        return mapData;
    }
    static void deleteUser(String accessToken){
        given()
                .header("Authorization", accessToken)
                .header("Content-Type", "application/json")
                .when()
                .delete("https://stellarburgers.education-services.ru/api/auth/user");
    }
}
