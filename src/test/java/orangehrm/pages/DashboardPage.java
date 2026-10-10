package orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
public class DashboardPage {

    WebDriver driver;

    public DashboardPage(WebDriver driver) {
        this.driver = driver;
    }

    By dashboardHeading =
            By.xpath("//h6[text()='Dashboard']");

    // Add these locators
    By profileMenu =
            By.className("oxd-userdropdown-tab");

    By logout =
            By.linkText("Logout");

    public boolean isDashboardDisplayed() {

        return driver.findElement(dashboardHeading)
                .isDisplayed();
    }

    public void logout() throws InterruptedException {

        driver.findElement(profileMenu).click();

        Thread.sleep(2000);

        driver.findElement(logout).click();
    }
}