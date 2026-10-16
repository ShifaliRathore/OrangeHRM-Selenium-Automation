package orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * LeaveListPage
 *
 * Page object for: /leave/viewLeaveList (Admin view)
 * Accessible only to Admin or HR Manager. Subject to session rotation (DEF-LM-001).
 * Note: page heading uses h5, unlike other Leave pages which use h6.
 */
public class LeaveListPage {

    WebDriver driver;
    WebDriverWait wait;

    public LeaveListPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // Locators
    By leaveListHeading    = By.xpath("//h5[normalize-space()='Leave List']");
    By employeeNameInput   = By.xpath("//input[@placeholder='Type for hints...']");
    By leaveStatusDropdown = By.xpath("(//div[contains(@class,'oxd-select-text')])[1]");
    By leaveTypeDropdown   = By.xpath("(//div[contains(@class,'oxd-select-text')])[2]");
    By fromDateInput       = By.xpath("(//input[@placeholder='yyyy-dd-mm'])[1]");
    By toDateInput         = By.xpath("(//input[@placeholder='yyyy-dd-mm'])[2]");
    By searchButton        = By.xpath("//button[normalize-space()='Search']");
    By resetButton         = By.xpath("//button[normalize-space()='Reset']");
    By resultRows          = By.xpath("//div[@class='oxd-table-body']/div");
    By noRecordsMessage    = By.xpath("//*[contains(text(),'No Records Found')]");

    // Page verification
    public boolean isLeaveListPageDisplayed() {
        try {
            return wait.until(ExpectedConditions
                    .visibilityOfElementLocated(leaveListHeading))
                    .isDisplayed();
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    // UI elements verification
    public boolean areFilterElementsDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(leaveListHeading));
            return driver.findElement(employeeNameInput).isDisplayed()
                    && driver.findElement(leaveStatusDropdown).isDisplayed()
                    && driver.findElement(leaveTypeDropdown).isDisplayed()
                    && driver.findElement(fromDateInput).isDisplayed()
                    && driver.findElement(toDateInput).isDisplayed()
                    && driver.findElement(searchButton).isDisplayed()
                    && driver.findElement(resetButton).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // Filter actions

    /**
     * Types a valid employee name and selects the first autocomplete suggestion.
     * Use enterInvalidEmployeeName() for names that will not match any employee.
     *
     * @param employeeName partial or full name to type
     */
    public void enterEmployeeName(String employeeName) {
        WebElement field = wait.until(
                ExpectedConditions.elementToBeClickable(employeeNameInput));
        field.clear();
        field.sendKeys(employeeName);
        By suggestion = By.xpath("//div[@role='listbox']//span");
        wait.until(ExpectedConditions.visibilityOfElementLocated(suggestion));
        driver.findElement(suggestion).click();
    }

    /**
     * Types a name without waiting for autocomplete — for invalid name searches.
     *
     * @param invalidName a string that produces no autocomplete hits
     */
    public void enterInvalidEmployeeName(String invalidName) {
        WebElement field = wait.until(
                ExpectedConditions.elementToBeClickable(employeeNameInput));
        field.clear();
        field.sendKeys(invalidName);
    }

    /**
     * @param status exact visible text e.g. "Pending Approval"
     */
    public void selectLeaveStatus(String status) {
        wait.until(ExpectedConditions.elementToBeClickable(leaveStatusDropdown)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@role='listbox']")));
        driver.findElement(
                By.xpath("//span[normalize-space()='" + status + "']")).click();
    }

    /**
     * @param leaveType exact visible text e.g. "CAN - Bereavement"
     */
    public void selectLeaveType(String leaveType) {
        wait.until(ExpectedConditions.elementToBeClickable(leaveTypeDropdown)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@role='listbox']")));
        driver.findElement(
                By.xpath("//span[normalize-space()='" + leaveType + "']")).click();
    }

    public void clickSearch() {
        driver.findElement(searchButton).click();
    }

    public void clickReset() {
        driver.findElement(resetButton).click();
    }

    // Result table verification
    public boolean isRecordDisplayed() {
        try {
            List<WebElement> rows = wait.until(
                    ExpectedConditions.visibilityOfAllElementsLocatedBy(resultRows));
            return rows.size() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public int getResultCount() {
        return driver.findElements(resultRows).size();
    }

    public boolean isNoRecordsMessageDisplayed() {
        try {
            return driver.findElement(noRecordsMessage).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getFirstRowText() {
        List<WebElement> rows = driver.findElements(resultRows);
        return rows.size() > 0 ? rows.get(0).getText() : "";
    }

    public boolean doesFirstRecordContain(String expectedValue) {
        return getFirstRowText().toLowerCase().contains(expectedValue.toLowerCase());
    }

    public boolean areFiltersReset() {
        try {
            WebElement nameField = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(employeeNameInput));
            String nameValue = nameField.getAttribute("value");
            String statusText = driver.findElement(leaveStatusDropdown).getText().trim();
            return (nameValue == null || nameValue.isEmpty())
                    && statusText.contains("Select");
        } catch (Exception e) {
            return false;
        }
    }
}
