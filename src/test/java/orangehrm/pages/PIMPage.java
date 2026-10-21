
package orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class PIMPage {

    private WebDriver driver;
    private WebDriverWait wait;

    public PIMPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    private By pimMenu = By.xpath("//span[text()='PIM']");
    private By pimHeading = By.xpath("//h6[text()='PIM']");
    private By employeeListHeading = By.xpath("//h5[text()='Employee Information']");
    private By employeeNameSearch = By.xpath("//label[text()='Employee Name']/../..//input");
    private By searchButton = By.xpath("//button[@type='submit']");
    private By resetButton = By.xpath("//button[normalize-space()='Reset']");

    public void openPIM() {
        wait.until(ExpectedConditions.elementToBeClickable(pimMenu)).click();
    }

    public boolean isPIMHeadingDisplayed() {
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(pimHeading)
        ).isDisplayed();
    }

    public boolean isEmployeeListDisplayed() {
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(employeeListHeading)
        ).isDisplayed();
    }

    public void searchEmployee(String name) {
        WebElement input = wait.until(
                ExpectedConditions.visibilityOfElementLocated(employeeNameSearch)
        );

        input.clear();
        input.sendKeys(name);

        wait.until(ExpectedConditions.elementToBeClickable(searchButton)).click();
    }

    public void resetSearch() {
        wait.until(ExpectedConditions.elementToBeClickable(resetButton)).click();
    }

    // Test 3: Add Employee page opens
    public void clickAddEmployee() {
        By addButton = By.xpath("//button[normalize-space()='Add']");
        wait.until(ExpectedConditions.elementToBeClickable(addButton)).click();
    }

    public boolean isAddEmployeeHeadingDisplayed() {
        By addEmployeeHeading = By.xpath("//h6[text()='Add Employee']");
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(addEmployeeHeading)
        ).isDisplayed();
    }

    // Test 4: Employee Name search field is displayed
    public boolean isEmployeeNameSearchDisplayed() {
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(employeeNameSearch)
        ).isDisplayed();
    }

    // Test 5: Employee ID search field is displayed
    public boolean isEmployeeIdSearchDisplayed() {
        By employeeIdSearch = By.xpath(
                "//label[normalize-space()='Employee Id']/../..//input"
        );

        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(employeeIdSearch)
        ).isDisplayed();
    }

    // Test 6: No records found for a nonexistent employee
    public boolean isNoRecordsFoundDisplayed() {
        By noRecordsMessage = By.xpath(
                "//span[normalize-space()='No Records Found']"
        );

        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(noRecordsMessage)
        ).isDisplayed();
    }
}


