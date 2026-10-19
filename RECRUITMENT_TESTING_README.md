# Recruitment Test Automation

## Purpose

This document describes the Recruitment module automation added to the OrangeHRM Selenium project. The tests cover the main candidate hiring workflow and separate validation scenarios using Selenium WebDriver, Java, and TestNG.

## Files

| File | Responsibility |
|---|---|
| `src/test/java/orangehrm/pages/RecruitmentPage.java` | Recruitment page locators and browser actions, including vacancy and candidate forms, status changes, search, and deletion. |
| `src/test/java/orangehrm/tests/RecruitmentTest.java` | End-to-end successful hiring workflow. |
| `src/test/java/orangehrm/tests/RecruitmentValidationTest.java` | Validation, filtering, and rejection scenarios. |
| `testng.xml` | TestNG suite configuration. Recruitment happy-path and validation classes are included in the suite. |
| `src/test/java/orangehrm/base/BaseTest.java` | Shared WebDriver setup and Admin login helpers used by the validation tests. |

The page object keeps Selenium interactions separate from the test assertions. TestNG organizes the tests and controls their order and setup/cleanup methods.

## Successful hiring workflow

`RecruitmentTest` contains nine dependent TestNG methods. They run in sequence in the same browser session:

1. Log in as Admin and create a uniquely named active `QA Engineer` vacancy with Adrienne Jeffery Bennett as hiring manager.
2. Search for the vacancy and verify that it is listed and active.
3. Create a candidate with a generated email and temporary text resume; verify the selected vacancy and `Application Initiated` status.
4. Shortlist the candidate.
5. Schedule an interview with Adrienne Jeffery Bennett and verify `Interview Scheduled`.
6. Mark the interview as passed.
7. Offer the candidate the job.
8. Hire the candidate and verify that the new employee appears in the PIM Employee List.
9. Delete the test vacancy.

Random suffixes and a unique email reduce collisions with records from previous runs. The generated resume is stored temporarily and removed at the end of the class.

## Validation and alternate-flow coverage

`RecruitmentValidationTest` contains nine independent scenarios. It creates a fresh browser and Admin session for each test:

- Submitting an empty candidate form shows required-field errors.
- An invalid email address is rejected.
- Submitting an empty vacancy form shows required-field errors for vacancy name, job title, and hiring manager.
- An unknown, unselected candidate name is marked invalid in candidate search.
- Vacancy and Active status filters return the matching active vacancy.
- Interview title, interviewer, and date are required.
- A candidate can be rejected from the initial application stage.
- An unsupported resume file type is rejected.
- A resume larger than 1 MB is rejected.

Created test vacancies, candidates, and temporary resumes are cleaned up after each test where possible. Cleanup is best-effort because the public demo can rotate sessions or respond slowly.

## Running the tests

### Run the Recruitment classes

In IntelliJ IDEA, right-click `testng.xml` and select **Run**. The Recruitment group runs both `RecruitmentTest` and `RecruitmentValidationTest`.

To debug one scenario, right-click its test method or test class and run it directly. The methods in `RecruitmentTest` depend on the earlier workflow steps, so run that class from the beginning for a complete hiring-flow run.

### Run the full project suite

Other project test groups are also active in `testng.xml` (Login, Admin, Time, Dashboard, and Leave Config). The Leave Module group remains commented out. Running the XML executes the active project groups, not only Recruitment.

## Test data and environment notes

- Application: OrangeHRM Open Source Demo at `https://opensource-demo.orangehrmlive.com/`.
- Browser: Google Chrome with ChromeDriver managed by WebDriverManager.
- The demo is shared and can be slow or rotate the active session. A login or autocomplete timeout may therefore occur intermittently even when a test passes by itself.
- `RecruitmentTest` deletes its vacancy as the final workflow step. Its `cleanupRecruitmentData` system property can additionally request best-effort candidate and vacancy cleanup after the class. The hired employee may remain in PIM on the shared demo.
- `RecruitmentValidationTest` creates a separate vacancy for several scenarios and attempts to remove its records in `@AfterMethod`.
- These are UI automation checks against a public demo, so results depend on the site and its current data as well as the test code.

## Summary for project presentation

The Recruitment automation verifies vacancy creation and search, candidate creation with a resume, candidate status progression through interview and hiring, transfer of the hired candidate into PIM, and cleanup of the test vacancy. Separate tests cover form validation, invalid email and resume input, vacancy filtering, interview requirements, unknown candidate search, and candidate rejection.
