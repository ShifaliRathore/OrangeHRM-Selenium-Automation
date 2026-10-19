package orangehrm.tests;

import orangehrm.base.BaseTest;
import orangehrm.pages.RecruitmentPage;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/** Negative-input checks kept separate from the Recruitment happy-path workflow. */
public class RecruitmentValidationTest extends BaseTest {

    private static final By ADD_BUTTON = By.xpath("//button[normalize-space()='Add']");
    private static final By SAVE_BUTTON = By.cssSelector("button[type='submit']");
    private static final By FIELD_ERRORS = By.cssSelector(".oxd-input-field-error-message");
    private static final By SEARCH_BUTTON = By.xpath("//button[normalize-space()='Search']");
    private static final By TABLE_ROWS = By.cssSelector(".oxd-table-body .oxd-table-card");

    private RecruitmentPage recruitment;
    private String testVacancyName;
    private String testCandidateFirstName;
    private String testCandidateLastName;
    private String testCandidateEmail;
    private Path testResume;
    private boolean vacancyCreated;
    private boolean candidateCreated;

    @BeforeMethod
    public void loginAndOpenRecruitment() {
        loginAsAdmin();
        recruitment = new RecruitmentPage(driver);
        recruitment.openRecruitment();
        testVacancyName = null;
        testCandidateFirstName = null;
        testCandidateLastName = null;
        testCandidateEmail = null;
        testResume = null;
        vacancyCreated = false;
        candidateCreated = false;
    }

    @AfterMethod(alwaysRun = true)
    public void cleanUpValidationData() {
        if (driver == null) return;
        boolean cleanupSessionReady = true;
        if (candidateCreated || vacancyCreated) {
            try {
                ensureAdminSession();
            } catch (Exception e) {
                cleanupSessionReady = false;
                System.err.println("Could not restore Admin session for validation cleanup: " + e.getMessage());
            }
        }
        if (candidateCreated && cleanupSessionReady) {
            try {
                recruitment.openRecruitment();
                recruitment.deleteCandidate(candidateFullName());
            } catch (Exception e) {
                System.err.println("Validation candidate cleanup did not complete: " + e.getMessage());
            }
        }
        if (vacancyCreated && cleanupSessionReady) {
            try {
                recruitment.openRecruitment();
                recruitment.deleteVacancy(testVacancyName);
            } catch (Exception e) {
                System.err.println("Validation vacancy cleanup did not complete: " + e.getMessage());
            }
        }
        try {
            if (testResume != null) Files.deleteIfExists(testResume);
        } catch (Exception e) {
            System.err.println("Validation resume cleanup did not complete: " + e.getMessage());
        }
    }

    @Test(description = "Candidate form shows required errors when submitted empty")
    public void candidateFormRejectsEmptySubmission() {
        openCandidateForm();
        driver.findElement(SAVE_BUTTON).click();

        List<WebElement> errors = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(FIELD_ERRORS));
        Assert.assertFalse(errors.isEmpty(), "An empty candidate form should show required-field errors");
        Assert.assertTrue(errors.stream().anyMatch(error -> error.getText().trim().equalsIgnoreCase("Required")),
                "The candidate form should identify at least one required field");
    }

    @Test(description = "Candidate form rejects an invalid email format")
    public void candidateFormRejectsInvalidEmail() {
        openCandidateForm();
        driver.findElement(By.name("firstName")).sendKeys("Validation");
        driver.findElement(By.name("lastName")).sendKeys("Candidate");
        driver.findElement(emailInput()).sendKeys("not-an-email");
        driver.findElement(SAVE_BUTTON).click();

        WebElement emailError = waitForEmailError();
        Assert.assertTrue(emailError.isDisplayed(), "Invalid email should show a field validation error");
        Assert.assertFalse(emailError.getText().isBlank(), "Email validation should explain the error");
    }

    @Test(description = "Vacancy form shows required errors when submitted empty")
    public void vacancyFormRequiresMandatoryFields() {
        recruitment.openVacancies();
        new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.elementToBeClickable(ADD_BUTTON)).click();
        new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//label[normalize-space()='Vacancy Name']/ancestor::div[contains(@class,'oxd-input-group')]//input")));
        driver.findElement(SAVE_BUTTON).click();

        List<WebElement> errors = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(FIELD_ERRORS));
        Assert.assertFalse(errors.isEmpty(), "Submitting an empty vacancy form should show validation errors");
        Assert.assertTrue(errorFor("Vacancy Name").isDisplayed(), "Vacancy Name should be required");
        Assert.assertTrue(errorFor("Job Title").isDisplayed(), "Job Title should be required");
        Assert.assertTrue(errorFor("Hiring Manager").isDisplayed(), "Hiring Manager should be required");
    }

    @Test(description = "Candidate search reports an unknown candidate name")
    public void candidateSearchWithUnknownNameShowsValidation() {
        String unknownName = "UnknownCandidate" + ThreadLocalRandom.current().nextInt(10000, 100000);
        driver.findElement(By.xpath("//label[normalize-space()='Candidate Name']"
                + "/ancestor::div[contains(@class,'oxd-input-group')]//input")).sendKeys(unknownName);
        driver.findElement(SEARCH_BUTTON).click();

        Assert.assertTrue(errorFor("Candidate Name").isDisplayed(),
                "An unselected candidate name should be marked invalid by the search form");
    }

    @Test(description = "Vacancy filters return the active vacancy that matches")
    public void vacancyFiltersFindActiveVacancy() {
        createTestVacancy();
        recruitment.openVacancies();
        chooseDropdown("Vacancy", testVacancyName);
        chooseDropdown("Status", "Active");
        driver.findElement(SEARCH_BUTTON).click();

        String matchingRowText = new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(d -> {
                    for (WebElement row : d.findElements(TABLE_ROWS)) {
                        try {
                            String rowText = row.getText();
                            if (rowText.contains(testVacancyName)) return rowText;
                        } catch (StaleElementReferenceException ignored) {
                            // The table refreshes after Search; retry with newly located rows.
                        }
                    }
                    return null;
                });
        Assert.assertTrue(matchingRowText.contains("Active"),
                "Filtering by vacancy and Active status should return the active vacancy");
    }

    @Test(description = "Interview title, interviewer, and date are required")
    public void interviewFormRequiresMandatoryFields() throws Exception {
        createTestVacancy();
        createTestCandidate();
        recruitment.shortlistCandidate();
        driver.findElement(By.xpath("//button[normalize-space()='Schedule Interview']")).click();
        clickSubmit();

        Assert.assertTrue(errorFor("Interview Title").isDisplayed(), "Interview Title should be required");
        Assert.assertTrue(errorFor("Interviewer").isDisplayed(), "Interviewer should be required");
        Assert.assertTrue(errorFor("Date").isDisplayed(), "Interview Date should be required");
    }

    @Test(description = "Candidate can be rejected from Application Initiated")
    public void rejectCandidateFromInitialStage() throws Exception {
        createTestVacancy();
        createTestCandidate();
        driver.findElement(By.xpath("//button[normalize-space()='Reject']")).click();
        new WebDriverWait(driver, Duration.ofSeconds(15)).until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath(
                        "//*[self::h5 or self::h6][contains(normalize-space(),'Reject Candidate')]")));
        clickSubmit();

        Assert.assertTrue(new WebDriverWait(driver, Duration.ofSeconds(15)).until(
                        d -> d.findElement(By.tagName("body")).getText().contains("Status: Rejected")),
                "Candidate status should change to Rejected");
    }

    @Test(description = "Unsupported resume files are rejected")
    public void candidateFormRejectsUnsupportedResume() throws Exception {
        createTestVacancy();
        openAndFillCandidateForm();
        testResume = Files.createTempFile("unsupported-resume-", ".jpg");
        Files.writeString(testResume, "Not a supported resume format");
        driver.findElement(By.cssSelector("input[type='file']")).sendKeys(testResume.toAbsolutePath().toString());
        clickSubmit();

        assertResumeValidation("file", "type", "format", "supported");
    }

    @Test(description = "Resume files larger than 1 MB are rejected")
    public void candidateFormRejectsResumeOverOneMb() throws Exception {
        createTestVacancy();
        openAndFillCandidateForm();
        testResume = Files.createTempFile("oversized-resume-", ".txt");
        Files.write(testResume, new byte[1024 * 1024 + 1]);
        driver.findElement(By.cssSelector("input[type='file']")).sendKeys(testResume.toAbsolutePath().toString());
        clickSubmit();

        assertResumeValidation("1mb", "1 mb", "size", "large");
    }

    private void createTestVacancy() {
        testVacancyName = "Validation Vacancy " + ThreadLocalRandom.current().nextInt(100000, 1000000);
        recruitment.openVacancies();
        vacancyCreated = true; // Ensure cleanup runs if save succeeds but a later wait fails.
        recruitment.addVacancy(testVacancyName, "QA Engineer", "Validation test vacancy",
                "Adrienne Jeffery Bennett", "1");
    }

    private void createTestCandidate() throws Exception {
        testCandidateFirstName = "Validation";
        testCandidateLastName = "Candidate" + ThreadLocalRandom.current().nextInt(100000, 1000000);
        testCandidateEmail = "validation" + System.nanoTime() + "@example.com";
        testResume = Files.createTempFile("validation-resume-", ".txt");
        Files.writeString(testResume, "Validation test resume");
        recruitment.openCandidates();
        recruitment.addCandidate(testCandidateFirstName, testCandidateLastName,
                testCandidateEmail, testVacancyName, testResume);
        candidateCreated = true;
    }

    private void openAndFillCandidateForm() {
        recruitment.openCandidates();
        openCandidateForm();
        testCandidateFirstName = "Validation";
        testCandidateLastName = "Upload" + ThreadLocalRandom.current().nextInt(100000, 1000000);
        testCandidateEmail = "upload" + System.nanoTime() + "@example.com";
        driver.findElement(By.name("firstName")).sendKeys(testCandidateFirstName);
        driver.findElement(By.name("lastName")).sendKeys(testCandidateLastName);
        chooseDropdown("Vacancy", testVacancyName);
        driver.findElement(emailInput()).sendKeys(testCandidateEmail);
    }

    private void chooseDropdown(String label, String value) {
        By dropdown = By.xpath("//label[normalize-space()='" + label
                + "']/ancestor::div[contains(@class,'oxd-input-group')]//div[contains(@class,'oxd-select-text-input')]");
        new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.elementToBeClickable(dropdown)).click();
        By option = By.xpath("//div[contains(@class,'oxd-select-dropdown')]"
                + "//*[self::span or self::div][normalize-space()='" + value + "']");
        new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.elementToBeClickable(option)).click();
    }

    private void clickSubmit() {
        WebElement submit = new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.elementToBeClickable(SAVE_BUTTON));
        try {
            submit.click();
        } catch (ElementClickInterceptedException e) {
            new WebDriverWait(driver, Duration.ofSeconds(20))
                    .until(ExpectedConditions.invisibilityOfElementLocated(
                            By.cssSelector(".oxd-form-loader")));
            new WebDriverWait(driver, Duration.ofSeconds(20))
                    .until(ExpectedConditions.elementToBeClickable(SAVE_BUTTON)).click();
        }
    }

    private void assertResumeValidation(String... acceptedMessageParts) {
        By toast = By.cssSelector(".oxd-toast");
        new WebDriverWait(driver, Duration.ofSeconds(15)).until(d ->
                !d.findElements(FIELD_ERRORS).isEmpty() || !d.findElements(toast).isEmpty());
        String messages = driver.findElements(FIELD_ERRORS).stream()
                .filter(WebElement::isDisplayed).map(WebElement::getText)
                .reduce("", (left, right) -> left + " " + right)
                + " " + driver.findElements(toast).stream().filter(WebElement::isDisplayed)
                .map(WebElement::getText).reduce("", (left, right) -> left + " " + right);
        String normalizedMessages = messages.toLowerCase().replaceAll("\\s+", " ");
        Assert.assertTrue(List.of(acceptedMessageParts).stream().anyMatch(normalizedMessages::contains),
                "Expected resume validation message, received: " + messages);
    }

    private String candidateFullName() {
        return testCandidateFirstName + " " + testCandidateLastName;
    }

    private void openCandidateForm() {
        new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.elementToBeClickable(ADD_BUTTON)).click();
        new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.visibilityOfElementLocated(By.name("firstName")));
    }

    private By emailInput() {
        return By.xpath("//label[normalize-space()='Email']/ancestor::div[contains(@class,'oxd-input-group')]//input");
    }

    private WebElement errorFor(String label) {
        By error = By.xpath("//label[normalize-space()='" + label
                + "']/ancestor::div[contains(@class,'oxd-input-group')]//span[contains(@class,'oxd-input-field-error-message')]");
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(error));
    }

    private WebElement waitForEmailError() {
        By error = By.xpath("//label[normalize-space()='Email']/ancestor::div[contains(@class,'oxd-input-group')]"
                + "//span[contains(@class,'oxd-input-field-error-message')]");
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(error));
    }
}
