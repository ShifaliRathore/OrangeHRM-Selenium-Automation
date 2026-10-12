package orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Page Object for the OrangeHRM "Admin > User Management > Users" page.
 */
public class AdminPage {

    public static final String BASE = "https://opensource-demo.orangehrmlive.com/web/index.php";

    // Column positions inside a table row (0 = checkbox)
    public static final int COL_USERNAME = 1;
    public static final int COL_ROLE = 2;
    public static final int COL_EMPLOYEE = 3;
    public static final int COL_STATUS = 4;

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ---------- Locators ----------
    private static final By LOGIN_USER = By.name("username");
    private static final By LOGIN_PASS = By.name("password");
    private static final By LOGIN_BTN = By.cssSelector("button[type='submit']");

    private static final By ADMIN_MENU = By.xpath("//a[contains(@href,'viewAdminModule')]");
    private static final By BREADCRUMB = By.cssSelector(".oxd-topbar-header-breadcrumb");

    private static final By LOADER = By.cssSelector(".oxd-loading-spinner, .oxd-form-loader");
    private static final By RECORDS_FOUND = By.xpath("//span[contains(.,'Found')]");
    private static final By TABLE_ROWS = By.cssSelector(".oxd-table-body .oxd-table-card");

    private static final By SEARCH_BTN = By.xpath("//button[normalize-space()='Search']");
    private static final By RESET_BTN = By.xpath("//button[normalize-space()='Reset']");
    private static final By ADD_BTN = By.xpath("//button[normalize-space()='Add']");
    private static final By SAVE_BTN = By.xpath("//button[normalize-space()='Save']");
    private static final By CONFIRM_DELETE_BTN = By.xpath("//button[normalize-space()='Yes, Delete']");

    private static final By EMPLOYEE_HINT_INPUT = By.xpath("//input[@placeholder='Type for hints...']");
    private static final By AUTOCOMPLETE_OPTIONS = By.cssSelector("div[role='listbox'] .oxd-autocomplete-option");

    private static final By TOAST = By.cssSelector(".oxd-toast");
    private static final By FIELD_ERRORS = By.cssSelector(".oxd-input-field-error-message");
    private static final By FIRST_ROW_DELETE_ICON =
            By.xpath("(//div[contains(@class,'oxd-table-body')]//i[contains(@class,'bi-trash')])[1]/parent::button");

    public AdminPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        this.wait.ignoring(StaleElementReferenceException.class);
    }

    // ---------- Dynamic locators ----------
    private By inputByLabel(String label) {
        return By.xpath("//label[normalize-space()='" + label
                + "']/ancestor::div[contains(concat(' ', normalize-space(@class), ' '), ' oxd-input-group ')][1]//input");
    }

    private By dropdownByLabel(String label) {
        return By.xpath("//label[normalize-space()='" + label
                + "']/ancestor::div[contains(concat(' ', normalize-space(@class), ' '), ' oxd-input-group ')][1]//div[contains(@class,'oxd-select-text-input')]");
    }

    private By dropdownOption(String option) {
        return By.xpath("//div[@role='listbox']//span[normalize-space()='" + option + "']");
    }

    // ---------- Navigation ----------
    public void login(String username, String password) {
        driver.get(BASE + "/auth/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(LOGIN_USER)).sendKeys(username);
        driver.findElement(LOGIN_PASS).sendKeys(password);
        driver.findElement(LOGIN_BTN).click();
        wait.until(ExpectedConditions.urlContains("/dashboard"));
    }

    public void openDashboard() {
        driver.get(BASE + "/dashboard/index");
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(ADMIN_MENU));
    }

    /** Click the "Admin" item in the left side menu. */
    public void openAdminModule() {
        wait.until(ExpectedConditions.elementToBeClickable(ADMIN_MENU)).click();
        wait.until(ExpectedConditions.urlContains("viewSystemUsers"));
        waitForUsersList();
    }

    /** Go straight to the System Users page (used to reset state between tests). */
    public void goToUsersPage() {
        driver.get(BASE + "/admin/viewSystemUsers");
        wait.until(ExpectedConditions.urlContains("viewSystemUsers"));
        waitForUsersList();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getModuleHeading() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(BREADCRUMB)).getText();
    }

    // ---------- Waits ----------
    private void waitForLoader() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(2))
                    .until(ExpectedConditions.visibilityOfElementLocated(LOADER));
        } catch (TimeoutException ignored) {
            // loader was too quick to catch - that's fine
        }
        wait.until(ExpectedConditions.invisibilityOfElementLocated(LOADER));
    }

    private void waitForUsersList() {
        waitForLoader();
        wait.until(ExpectedConditions.visibilityOfElementLocated(RECORDS_FOUND));
    }

    // ---------- Generic helpers ----------
    private void type(By locator, String text) {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
        el.clear();
        el.sendKeys(text);
    }

    private void selectDropdown(String label, String option) {
        wait.until(ExpectedConditions.elementToBeClickable(dropdownByLabel(label))).click();
        wait.until(ExpectedConditions.elementToBeClickable(dropdownOption(option))).click();
    }

    // ---------- Search section ----------
    public void enterUsername(String username) {
        type(inputByLabel("Username"), username);
    }

    public String getUsernameFieldValue() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(inputByLabel("Username")))
                .getDomProperty("value");
    }

    public void selectUserRole(String role) {
        selectDropdown("User Role", role);
    }

    public void selectStatus(String status) {
        selectDropdown("Status", status);
    }

    public void clickSearch() {
        wait.until(ExpectedConditions.elementToBeClickable(SEARCH_BTN)).click();
        waitForUsersList();
    }

    public void clickReset() {
        wait.until(ExpectedConditions.elementToBeClickable(RESET_BTN)).click();
        waitForUsersList();
    }

    public void searchByUsername(String username) {
        enterUsername(username);
        clickSearch();
    }

    // ---------- Results ----------
    public int getRowCount() {
        return driver.findElements(TABLE_ROWS).size();
    }

    /** Returns the text of one column for every visible row. */
    public List<String> getColumnValues(int columnIndex) {
        List<String> values = new ArrayList<>();
        for (WebElement row : driver.findElements(TABLE_ROWS)) {
            List<WebElement> cells = row.findElements(By.cssSelector(".oxd-table-cell"));
            values.add(cells.get(columnIndex).getText().trim());
        }
        return values;
    }

    public String getRecordsFoundText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(RECORDS_FOUND)).getText();
    }

    // ---------- Add user ----------
    public void openAddUserForm() {
        wait.until(ExpectedConditions.elementToBeClickable(ADD_BTN)).click();
        wait.until(ExpectedConditions.urlContains("saveSystemUser"));
        waitForLoader();
        wait.until(ExpectedConditions.visibilityOfElementLocated(dropdownByLabel("User Role")));
    }

    private void selectEmployee(String hint) {
        type(EMPLOYEE_HINT_INPUT, hint);
        wait.until(d -> {
            List<WebElement> options = d.findElements(AUTOCOMPLETE_OPTIONS);
            return !options.isEmpty() && !options.get(0).getText().contains("Searching");
        });
        List<WebElement> options = driver.findElements(AUTOCOMPLETE_OPTIONS);
        if (options.get(0).getText().contains("No Records")) {
            throw new IllegalStateException("No employee found for hint: " + hint);
        }
        options.get(0).click();
    }

    public void clickSave() {
        wait.until(ExpectedConditions.elementToBeClickable(SAVE_BTN)).click();
    }

    public void addUser(String role, String employeeHint, String status, String username, String password) {
        openAddUserForm();
        selectDropdown("User Role", role);
        selectEmployee(employeeHint);
        selectDropdown("Status", status);
        type(inputByLabel("Username"), username);
        type(inputByLabel("Password"), password);
        type(inputByLabel("Confirm Password"), password);
        clickSave();
    }

    /** Waits for the green/red toast and returns its text. */
    public String getToastMessage() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(TOAST)).getText();
    }

    /** Waits until at least one validation message is shown and returns all of them. */
    public List<String> getValidationErrors() {
        wait.until(d -> !d.findElements(FIELD_ERRORS).isEmpty());
        List<String> errors = new ArrayList<>();
        for (WebElement e : driver.findElements(FIELD_ERRORS)) {
            errors.add(e.getText().trim());
        }
        return errors;
    }

    // ---------- Delete user ----------
    /** Searches for the username, deletes the first match and returns the toast text. */
    public String deleteUser(String username) {
        searchByUsername(username);
        wait.until(ExpectedConditions.elementToBeClickable(FIRST_ROW_DELETE_ICON)).click();
        wait.until(ExpectedConditions.elementToBeClickable(CONFIRM_DELETE_BTN)).click();
        String toast = getToastMessage();
        waitForLoader();
        return toast;
    }
}