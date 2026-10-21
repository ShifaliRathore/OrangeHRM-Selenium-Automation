package orangehrm.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import orangehrm.pages.RecruitmentPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/** Automated version of the Recruitment workflow manually checked in OrangeHRM. */
public class RecruitmentTest {

    private static final String LOGIN_URL =
            "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login";
    private static final By USERNAME = By.name("username");
    private static final By PASSWORD = By.name("password");
    private static final By LOGIN_BUTTON = By.cssSelector("button[type='submit']");
    private static final By DASHBOARD_HEADING = By.xpath("//h6[normalize-space()='Dashboard']");

    private WebDriver driver;
    private final int randomNumber = ThreadLocalRandom.current().nextInt(10, 100);
    private final String uniqueId = Long.toString(System.currentTimeMillis());
    private final String vacancyName = "Tester " + randomNumber;
    private final String candidateFirstName = "Automation";
    private final String candidateLastName = "Candidate" + randomNumber;
    private final String candidateName = candidateFirstName + " " + candidateLastName;
    private final String email = "candidate" + uniqueId + "@example.com";
    private final String interviewTitle = "Automation Testing " + uniqueId;
    private final String interviewDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-dd-MM"));
    private final boolean cleanupRecruitmentData = Boolean.getBoolean("cleanupRecruitmentData");
    private Path resume;
    private boolean vacancyCreated;
    private boolean vacancyDeleted;
    private boolean candidateCreated;

    @BeforeClass
    public void loginAsAdmin() throws Exception {
        WebDriverManager.chromedriver().setup();
        Path downloadDirectory = Path.of("target", "downloads").toAbsolutePath();
        Files.createDirectories(downloadDirectory);
        Map<String, Object> browserPreferences = new HashMap<>();
        browserPreferences.put("download.default_directory", downloadDirectory.toString());
        browserPreferences.put("download.prompt_for_download", false);

        ChromeOptions options = new ChromeOptions();
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        options.setExperimentalOption("prefs", browserPreferences);
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.get(LOGIN_URL);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.visibilityOfElementLocated(USERNAME)).sendKeys("Admin");
        wait.until(ExpectedConditions.visibilityOfElementLocated(PASSWORD)).sendKeys("admin123");
        wait.until(ExpectedConditions.elementToBeClickable(LOGIN_BUTTON)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(DASHBOARD_HEADING));
    }

    @AfterClass(alwaysRun = true)
    public void closeBrowser() {
        if (driver == null) return;
        RecruitmentPage recruitment = new RecruitmentPage(driver);
        if (cleanupRecruitmentData) {
            try {
                if (candidateCreated) {
                    recruitment.openRecruitment();
                    recruitment.deleteCandidate(candidateName);
                }
            } catch (Exception e) {
                System.err.println("Candidate cleanup did not complete: " + e.getMessage());
            }
            try {
                if (vacancyCreated && !vacancyDeleted) recruitment.deleteVacancy(vacancyName);
            } catch (Exception e) {
                System.err.println("Vacancy cleanup did not complete: " + e.getMessage());
            }
        }
        try {
            if (resume != null) Files.deleteIfExists(resume);
        } catch (Exception e) {
            System.err.println("Temporary resume cleanup did not complete: " + e.getMessage());
        }
        driver.quit();
    }

    @Test(description = "Create and verify the unique QA vacancy")
    public void createAndVerifyVacancy() {
        RecruitmentPage recruitment = new RecruitmentPage(driver);
        recruitment.openRecruitment();
        recruitment.openVacancies();
        vacancyCreated = true;
        recruitment.addVacancy(vacancyName, "QA Engineer", "Expert in Selenium Webdriver",
                "a", "2");
        recruitment.openCandidates();
        recruitment.openVacancies();
        recruitment.searchVacancy(vacancyName);
        Assert.assertTrue(recruitment.isVacancyListed(vacancyName),
                "The newly created vacancy should appear in search results");
        Assert.assertTrue(recruitment.isVacancyActive(vacancyName), "The vacancy should be Active");
    }

    @Test(dependsOnMethods = "createAndVerifyVacancy", description = "Add a candidate and verify the initial application")
    public void addCandidateToVacancy() throws Exception {
        RecruitmentPage recruitment = new RecruitmentPage(driver);
        resume = Files.createTempFile("orangehrm-candidate-", ".txt");
        Files.writeString(resume, "Automation test candidate resume\nSample QA experience.\n");
        recruitment.openCandidates();
        candidateCreated = true; // Let @AfterClass clean up even if the save wait fails.
        recruitment.addCandidate(candidateFirstName, candidateLastName, email, vacancyName, resume);
        Assert.assertTrue(recruitment.isCandidateApplicationDisplayed(candidateName, vacancyName),
                "Candidate application should show the candidate and selected vacancy");
        Assert.assertTrue(recruitment.hasStatus("Application Initiated"));
    }

    @Test(dependsOnMethods = "addCandidateToVacancy", description = "Shortlist the candidate")
    public void shortlistCandidate() {
        RecruitmentPage recruitment = new RecruitmentPage(driver);
        recruitment.shortlistCandidate();
        Assert.assertTrue(recruitment.hasStatus("Shortlisted"));
    }

    @Test(dependsOnMethods = "shortlistCandidate", description = "Schedule the candidate interview")
    public void scheduleCandidateInterview() {
        RecruitmentPage recruitment = new RecruitmentPage(driver);
        recruitment.scheduleInterview(interviewTitle, "a", interviewDate, "01:00 PM");
        Assert.assertTrue(recruitment.hasStatus("Interview Scheduled"));
    }

    @Test(dependsOnMethods = "scheduleCandidateInterview", description = "Mark the interview as passed")
    public void markInterviewPassed() {
        RecruitmentPage recruitment = new RecruitmentPage(driver);
        recruitment.markInterviewPassed();
        Assert.assertTrue(recruitment.hasStatus("Interview Passed"));
    }

    @Test(dependsOnMethods = "markInterviewPassed", description = "Offer the candidate the job")
    public void offerJobToCandidate() {
        RecruitmentPage recruitment = new RecruitmentPage(driver);
        recruitment.offerJob();
        Assert.assertTrue(recruitment.hasStatus("Job Offered"));
    }

    @Test(dependsOnMethods = "offerJobToCandidate", description = "Hire the candidate")
    public void hireCandidate() {
        RecruitmentPage recruitment = new RecruitmentPage(driver);
        recruitment.hireCandidate();
        Assert.assertTrue(recruitment.hasStatus("Hired"));
    }

    @Test(dependsOnMethods = "hireCandidate", description = "Verify the hired employee in PIM")
    public void verifyHiredEmployeeInPim() {
        RecruitmentPage recruitment = new RecruitmentPage(driver);
        recruitment.openEmployeeList();
        recruitment.searchEmployee(candidateName);
        Assert.assertTrue(recruitment.isEmployeeListed(candidateName),
                "The hired candidate should be present in PIM Employee List");
    }

    @Test(dependsOnMethods = "verifyHiredEmployeeInPim", description = "Delete the test vacancy")
    public void deleteTestVacancy() {
        RecruitmentPage recruitment = new RecruitmentPage(driver);
        recruitment.openRecruitment();
        recruitment.deleteVacancy(vacancyName);
        vacancyDeleted = true;
    }

}
