package orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

/** Page actions for the manually verified Recruitment workflow. */
public class RecruitmentPage {

    private static final String RECRUITMENT_URL =
            "https://opensource-demo.orangehrmlive.com/web/index.php/recruitment/";
    private static final String EMPLOYEE_LIST_URL =
            "https://opensource-demo.orangehrmlive.com/web/index.php/pim/viewEmployeeList";

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By candidatesTab = By.xpath("//a[normalize-space()='Candidates']");
    private final By vacanciesTab = By.xpath("//a[normalize-space()='Vacancies']");
    private final By addButton = By.xpath("//button[normalize-space()='Add']");
    private final By saveButton = By.xpath("//button[@type='submit']");
    private final By searchButton = By.xpath("//button[normalize-space()='Search']");
    private final By autocompleteOptions = By.cssSelector(
            ".oxd-autocomplete-dropdown .oxd-autocomplete-option");
    private final By selectOptions = By.cssSelector(".oxd-select-dropdown .oxd-select-option");
    private final By tableRows = By.cssSelector(".oxd-table-body .oxd-table-card");
    private final By noRecords = By.xpath("//*[contains(normalize-space(),'No Records Found')]");
    private final By deleteConfirmation = By.xpath("//button[normalize-space()='Yes, Delete']");
    private final By resumeInput = By.cssSelector("input[type='file']");

    public RecruitmentPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    public void openRecruitment() {
        driver.get(RECRUITMENT_URL + "viewCandidates");
        wait.until(ExpectedConditions.visibilityOfElementLocated(candidatesTab));
    }

    public void openCandidates() {
        wait.until(ExpectedConditions.elementToBeClickable(candidatesTab)).click();
        wait.until(ExpectedConditions.urlContains("viewCandidates"));
    }

    public void openVacancies() {
        wait.until(ExpectedConditions.elementToBeClickable(vacanciesTab)).click();
        wait.until(ExpectedConditions.urlContains("viewJobVacancy"));
    }

    public void addVacancy(String name, String jobTitle, String description,
                           String hiringManager, String positions) {
        wait.until(ExpectedConditions.elementToBeClickable(addButton)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(input("Vacancy Name")));

        selectDropdown("Job Title", jobTitle);
        enter(input("Vacancy Name"), name);
        enter(textArea("Description"), description);
        enter(input("Hiring Manager"), hiringManager);
        chooseSuggestion(hiringManager);
        enter(input("Number of Positions"), positions);
        clickSaveWhenFormReady();
        // After creating a vacancy OrangeHRM may keep the form open at
        // /addJobVacancy/<id>. That route means it saved and opened the new record.
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("addJobVacancy/"),
                ExpectedConditions.invisibilityOfElementLocated(input("Vacancy Name")),
                ExpectedConditions.urlContains("viewJobVacancy")));
    }

    public void searchVacancy(String name) {
        selectDropdown("Vacancy", name);
        wait.until(ExpectedConditions.elementToBeClickable(searchButton)).click();
        waitForRowOrEmptyState();
    }

    public boolean isVacancyListed(String name) {
        return isRowListed(name);
    }

    public boolean isVacancyActive(String name) {
        By rowLocator = rowContaining(name);
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(rowLocator))
                    .getText().lines().anyMatch(line -> line.trim().equalsIgnoreCase("Active"));
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void openVacancyForEdit(String name) {
        WebElement row = wait.until(ExpectedConditions.visibilityOfElementLocated(rowContaining(name)));
        row.findElement(By.xpath(".//button[.//i[contains(@class,'bi-pencil')]]")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(input("Vacancy Name")));
    }

    public String getInputValue(String label) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(input(label))).getDomProperty("value");
    }

    public String getTextAreaValue(String label) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(textArea(label))).getDomProperty("value");
    }

    public String getDropdownValue(String label) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(dropdown(label))).getText().trim();
    }

    public void cancelForm() {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space()='Cancel']"))).click();
    }

    public void addCandidate(String firstName, String lastName, String email,
                             String vacancy, Path resume) {
        wait.until(ExpectedConditions.elementToBeClickable(addButton)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("firstName")));
        enter(By.name("firstName"), firstName);
        enter(By.name("lastName"), lastName);
        selectDropdown("Vacancy", vacancy);
        enter(input("Email"), email);
        wait.until(ExpectedConditions.presenceOfElementLocated(resumeInput))
                .sendKeys(resume.toAbsolutePath().toString());
        clickSaveWhenFormReady();
        By initialStatus = By.xpath("//*[contains(normalize-space(),'Application Initiated')]");
        By candidateProfile = By.xpath("//*[normalize-space()='Candidate Profile']");
        By validationMessages = By.cssSelector(".oxd-input-field-error-message");
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(initialStatus),
                    ExpectedConditions.visibilityOfElementLocated(candidateProfile),
                    ExpectedConditions.visibilityOfElementLocated(validationMessages)));
        } catch (TimeoutException e) {
            throw new TimeoutException("After saving the candidate, neither the candidate profile nor "
                    + "validation messages appeared. Current page text: "
                    + driver.findElement(By.tagName("body")).getText(), e);
        }

        List<WebElement> errors = driver.findElements(validationMessages);
        if (!errors.isEmpty()) {
            String errorText = errors.stream().map(WebElement::getText)
                    .filter(text -> !text.isBlank()).reduce((left, right) -> left + "; " + right)
                    .orElse("Unknown validation error");
            throw new IllegalStateException("Candidate form was not saved: " + errorText);
        }
    }

    public boolean isCandidateApplicationDisplayed(String name, String vacancy) {
        String body = wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body"))).getText();
        return normalize(body).contains(normalize(name)) && normalize(body).contains(normalize(vacancy));
    }

    public boolean hasStatus(String status) {
        return wait.until(d -> d.findElement(By.tagName("body")).getText().contains("Status: " + status));
    }

    public void shortlistCandidate() {
        completeStatusAction("Shortlist", "Shortlisted");
    }

    public void scheduleInterview(String title, String interviewer, String date, String time) {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space()='Schedule Interview']"))).click();
        enter(input("Interview Title"), title);
        enter(input("Interviewer"), interviewer);
        chooseSuggestion(interviewer);
        enter(input("Date"), date);
        if (time != null && !time.isBlank() && !driver.findElements(input("Time")).isEmpty()) {
            enter(input("Time"), time);
        }
        clickSaveWhenFormReady();
        wait.until(d -> d.findElement(By.tagName("body")).getText()
                .contains("Status: Interview Scheduled"));
    }

    public void markInterviewPassed() {
        completeStatusAction("Mark Interview Passed", "Interview Passed");
    }

    public void offerJob() {
        completeStatusAction("Offer Job", "Job Offered");
    }

    public void hireCandidate() {
        completeStatusAction("Hire", "Hired");
    }

    private void completeStatusAction(String action, String status) {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space()='" + action + "']"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
                "//*[self::h5 or self::h6][contains(normalize-space(), '" + action + "')]")));
        clickSaveWhenFormReady();
        wait.until(d -> d.findElement(By.tagName("body")).getText().contains("Status: " + status));
    }

    private void clickSaveWhenFormReady() {
        // Try the requested action as soon as its button is ready. Wait only if
        // OrangeHRM's form overlay actually intercepts that click.
        WebElement save = wait.until(ExpectedConditions.elementToBeClickable(saveButton));
        try {
            save.click();
        } catch (ElementClickInterceptedException intercepted) {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(
                    By.cssSelector(".oxd-form-loader")));
            wait.until(ExpectedConditions.elementToBeClickable(saveButton)).click();
        }
    }

    public void searchCandidate(String name) {
        enter(input("Candidate Name"), name);
        chooseSuggestion(name.substring(name.lastIndexOf(' ') + 1));
        wait.until(ExpectedConditions.elementToBeClickable(searchButton)).click();
        waitForRowOrEmptyState();
    }

    public boolean isCandidateListed(String name) {
        return isRowListed(name.substring(name.lastIndexOf(' ') + 1));
    }

    public boolean candidateHasListStatus(String name, String status) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(
                    rowContaining(name.substring(name.lastIndexOf(' ') + 1))))
                    .getText().contains(status);
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void downloadCandidateResume(String name) {
        WebElement row = wait.until(ExpectedConditions.visibilityOfElementLocated(
                rowContaining(name.substring(name.lastIndexOf(' ') + 1))));
        row.findElement(By.xpath(".//button[.//i[contains(@class,'bi-download')]]")).click();
    }

    public void searchEmployee(String name) {
        enter(input("Employee Name"), name);
        chooseSuggestion(name.substring(name.lastIndexOf(' ') + 1));
        wait.until(ExpectedConditions.elementToBeClickable(searchButton)).click();
        waitForRowOrEmptyState();
    }

    public void openEmployeeList() {
        driver.get(EMPLOYEE_LIST_URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[normalize-space()='Employee Information']")));
    }

    public boolean isEmployeeListed(String name) {
        return isRowListed(name.substring(name.lastIndexOf(' ') + 1));
    }

    public void deleteVacancy(String name) {
        openVacancies();
        searchVacancy(name);
        WebElement row = wait.until(ExpectedConditions.visibilityOfElementLocated(rowContaining(name)));
        row.findElement(By.xpath(".//button[.//i[contains(@class,'bi-trash')]]")).click();
        wait.until(ExpectedConditions.elementToBeClickable(deleteConfirmation)).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(rowContaining(name)));
    }

    public void deleteCandidate(String name) {
        openCandidates();
        searchCandidate(name);
        WebElement row = wait.until(ExpectedConditions.visibilityOfElementLocated(
                rowContaining(name.substring(name.lastIndexOf(' ') + 1))));
        row.findElement(By.xpath(".//button[.//i[contains(@class,'bi-trash')]]")).click();
        wait.until(ExpectedConditions.elementToBeClickable(deleteConfirmation)).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                rowContaining(name.substring(name.lastIndexOf(' ') + 1))));
    }

    public boolean isSuccessMessageDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".oxd-toast")))
                    .getText().toLowerCase().contains("success");
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void logout() {
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".oxd-userdropdown-tab"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[normalize-space()='Logout']"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));
    }

    private void selectDropdown(String label, String value) {
        wait.until(ExpectedConditions.elementToBeClickable(dropdown(label))).click();
        By option = By.xpath("//div[contains(@class,'oxd-select-dropdown')]"
                + "//*[self::span or self::div][normalize-space()='" + value + "']");
        wait.until(ExpectedConditions.elementToBeClickable(option)).click();
    }

    private void chooseSuggestion(String expectedText) {
        WebElement option = wait.until(d -> {
            for (WebElement candidate : d.findElements(autocompleteOptions)) {
                try {
                    String text = normalize(candidate.getText());
                    if (matchesSuggestion(text, expectedText) && !text.contains("no records")) {
                        return candidate;
                    }
                } catch (StaleElementReferenceException ignored) {
                    // Suggestions can be replaced while the autocomplete refreshes.
                }
            }
            return null;
        });
        option.click();
    }

    private boolean matchesSuggestion(String actual, String expected) {
        String normalizedExpected = normalize(expected);
        if (actual.contains(normalizedExpected)) return true;

        String[] nameParts = normalizedExpected.split(" ");
        return nameParts.length > 1
                && actual.contains(nameParts[0])
                && actual.contains(nameParts[nameParts.length - 1]);
    }

    private void enter(By locator, String value) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        field.clear();
        field.sendKeys(value);
    }

    private By input(String label) {
        return By.xpath("//label[normalize-space()='" + label
                + "']/ancestor::div[contains(@class,'oxd-input-group')]//input");
    }

    private By textArea(String label) {
        return By.xpath("//label[normalize-space()='" + label
                + "']/ancestor::div[contains(@class,'oxd-input-group')]//textarea");
    }

    private By dropdown(String label) {
        return By.xpath("//label[normalize-space()='" + label
                + "']/ancestor::div[contains(@class,'oxd-input-group')]//div[contains(@class,'oxd-select-text-input')]");
    }

    private By rowContaining(String text) {
        return By.xpath("//div[contains(@class,'oxd-table-card')][contains(.,'" + text + "')]");
    }

    private boolean isRowListed(String text) {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(tableRows),
                    ExpectedConditions.visibilityOfElementLocated(noRecords)));
            for (WebElement row : driver.findElements(tableRows)) {
                try {
                    if (normalize(row.getText()).contains(normalize(text))) return true;
                } catch (StaleElementReferenceException ignored) {
                    // Re-read the current rows below if the results table refreshes.
                }
            }
            return driver.findElements(tableRows).stream().anyMatch(row -> {
                try {
                    return normalize(row.getText()).contains(normalize(text));
                } catch (StaleElementReferenceException ignored) {
                    return false;
                }
            });
        } catch (TimeoutException e) {
            return false;
        }
    }

    private void waitForRowOrEmptyState() {
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(tableRows),
                ExpectedConditions.visibilityOfElementLocated(noRecords)));
    }

    private String normalize(String value) {
        return value == null ? "" : value.replaceAll("\\s+", " ").trim().toLowerCase();
    }
}
