package orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * ApplyLeavePage
 *
 * Page object for: /leave/applyLeave
 * Subject to session rotation (DEF-LM-001) — loginAsAdmin() in BaseTest
 * mitigates this at test start.
 */
public class ApplyLeavePage {

    WebDriver driver;
    WebDriverWait wait;

    public ApplyLeavePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // Locators
    By applyLeaveHeading       = By.xpath("//h6[normalize-space()='Apply Leave']");
    By leaveTypeDropdown       = By.xpath("(//div[contains(@class,'oxd-select-text')])[1]");
    By fromDateInput           = By.xpath("(//input[@placeholder='yyyy-dd-mm'])[1]");
    By toDateInput             = By.xpath("(//input[@placeholder='yyyy-dd-mm'])[2]");
    By commentsTextarea        = By.xpath("//textarea[@placeholder='Type here']");
    By applyButton             = By.xpath("//button[normalize-space()='Apply']");
    By successToast            = By.id("oxd-toaster_1");
    By requiredValidationMessages = By.xpath("//span[normalize-space()='Required']");
    By dateRangeError          = By.xpath("//span[contains(@class,'oxd-input-field-error-message')]");

    // Page verification
    public boolean isApplyLeavePageDisplayed() {
        try {
            return wait.until(ExpectedConditions
                    .visibilityOfElementLocated(applyLeaveHeading))
                    .isDisplayed();
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    // Form UI verification
    public boolean areFormElementsDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(applyLeaveHeading));
            return driver.findElement(leaveTypeDropdown).isDisplayed()
                    && driver.findElement(fromDateInput).isDisplayed()
                    && driver.findElement(toDateInput).isDisplayed()
                    && driver.findElement(commentsTextarea).isDisplayed()
                    && driver.findElement(applyButton).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // Form actions

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

    /**
     * @param date format "yyyy-dd-mm" matching the placeholder
     */
    public void enterFromDate(String date) {
        WebElement field = wait.until(
                ExpectedConditions.elementToBeClickable(fromDateInput));
        field.click();
        field.sendKeys(Keys.CONTROL + "a");
        field.sendKeys(date);
        field.sendKeys(Keys.TAB);
    }

    /**
     * @param date format "yyyy-dd-mm" matching the placeholder
     */
    public void enterToDate(String date) {
        WebElement field = wait.until(
                ExpectedConditions.elementToBeClickable(toDateInput));
        field.click();
        field.sendKeys(Keys.CONTROL + "a");
        field.sendKeys(date);
        field.sendKeys(Keys.TAB);
    }

    public void enterComments(String comments) {
        WebElement textarea = driver.findElement(commentsTextarea);
        textarea.clear();
        textarea.sendKeys(comments);
    }

    public void clickApply() {
        driver.findElement(applyButton).click();
    }

    // Result verification
    public boolean isSuccessMessageDisplayed() {
        try {
            return wait.until(ExpectedConditions
                    .visibilityOfElementLocated(successToast))
                    .isDisplayed();
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    public boolean isRequiredValidationDisplayed() {
        try {
            wait.until(ExpectedConditions
                    .visibilityOfElementLocated(requiredValidationMessages));
            return driver.findElements(requiredValidationMessages).size() > 0;
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    public boolean isDateRangeErrorDisplayed() {
        try {
            return wait.until(ExpectedConditions
                    .visibilityOfElementLocated(dateRangeError))
                    .isDisplayed();
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    /**
     * Fills and submits a complete leave application.
     *
     * @param leaveType exact leave type text
     * @param fromDate  "yyyy-dd-mm"
     * @param toDate    "yyyy-dd-mm"
     * @param comments  optional — pass null or empty to skip
     */
    public void applyForLeave(String leaveType, String fromDate,
                               String toDate, String comments) {
        selectLeaveType(leaveType);
        enterFromDate(fromDate);
        enterToDate(toDate);
        if (comments != null && !comments.isEmpty()) {
            enterComments(comments);
        }
        clickApply();
    }
}
