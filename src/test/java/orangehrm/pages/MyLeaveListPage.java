package orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * MyLeaveListPage
 *
 * Page object for: /leave/viewMyLeaveList
 * Shows the personal leave history of the currently logged-in user.
 * Subject to session rotation (DEF-LM-001).
 */
public class MyLeaveListPage {

    WebDriver driver;
    WebDriverWait wait;

    public MyLeaveListPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // Locators
    By myLeaveListHeading  = By.xpath("//h6[normalize-space()='My Leave List']");
    By fromDateInput       = By.xpath("(//input[@placeholder='yyyy-dd-mm'])[1]");
    By toDateInput         = By.xpath("(//input[@placeholder='yyyy-dd-mm'])[2]");
    By leaveStatusDropdown = By.xpath("(//div[contains(@class,'oxd-select-text')])[1]");
    By leaveTypeDropdown   = By.xpath("(//div[contains(@class,'oxd-select-text')])[2]");
    By searchButton        = By.xpath("//button[normalize-space()='Search']");
    By resetButton         = By.xpath("//button[normalize-space()='Reset']");
    By resultRows          = By.xpath("//div[@class='oxd-table-body']/div");
    By noRecordsMessage    = By.xpath("//*[contains(text(),'No Records Found')]");
    By statusCellsInResults = By.xpath(
            "//div[@class='oxd-table-body']//div[@role='row']//div[@role='cell'][4]//div");

    // Page verification
    public boolean isMyLeaveListPageDisplayed() {
        try {
            return wait.until(ExpectedConditions
                    .visibilityOfElementLocated(myLeaveListHeading))
                    .isDisplayed();
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    // UI elements verification
    public boolean areFilterElementsDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(myLeaveListHeading));
            return driver.findElement(fromDateInput).isDisplayed()
                    && driver.findElement(toDateInput).isDisplayed()
                    && driver.findElement(leaveStatusDropdown).isDisplayed()
                    && driver.findElement(leaveTypeDropdown).isDisplayed()
                    && driver.findElement(searchButton).isDisplayed()
                    && driver.findElement(resetButton).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // Filter actions

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

    public boolean doAllRecordsMatchStatus(String expectedStatus) {
        try {
            List<WebElement> cells = wait.until(
                    ExpectedConditions.visibilityOfAllElementsLocatedBy(statusCellsInResults));
            if (cells.isEmpty()) return false;
            for (WebElement cell : cells) {
                if (!cell.getText().trim().equalsIgnoreCase(expectedStatus)) return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean areFiltersReset() {
        try {
            WebElement dropdown = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(leaveStatusDropdown));
            return dropdown.getText().trim().contains("Select");
        } catch (Exception e) {
            return false;
        }
    }
}
