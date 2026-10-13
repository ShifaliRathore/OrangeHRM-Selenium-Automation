package orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

public class TimePage {

    WebDriver driver;

    public TimePage(WebDriver driver) {
        this.driver = driver;
    }

    By timeHeading = By.xpath("//h6[text()='Time']");

    public boolean isTimePageDisplayed() {
        WebDriverWait wait =
                new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            return wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            timeHeading
                    )
            ).isDisplayed();

        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }
}