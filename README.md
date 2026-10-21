# OrangeHRM Selenium Automation

Automated UI tests for the OrangeHRM Open Source Demo, written in Java with Selenium WebDriver and TestNG. The tests use page objects to keep browser interactions separate from assertions.

## Application and tools

- **Application:** OrangeHRM Open Source Demo
- **URL:** https://opensource-demo.orangehrmlive.com/web/index.php/auth/login
- **Language:** Java
- **Browser automation:** Selenium WebDriver with Chrome
- **Test framework:** TestNG
- **Build and dependencies:** Maven
- **IDE:** IntelliJ IDEA
- **Design:** Page Object Model (POM)

## Test suite summary

The active classes configured in `testng.xml` contain **63 test methods across seven modules**. The latest full-suite result reported by the project was **63 passed, 0 failed**.

| Module | Test cases | Reported passed | Reported failed | Test class(es) |
|---|---:|---:|---:|---|
| Login | 9 | 9 | 0 | `LoginTest` |
| Admin | 10 | 10 | 0 | `AdminModuleTest` |
| Time | 4 | 4 | 0 | `TimeModuleTest` |
| Dashboard | 4 | 4 | 0 | `DashboardTest` |
| PIM | 6 | 6 | 0 | `PIMModuleTest` |
| Recruitment | 18 | 18 | 0 | `RecruitmentTest`, `RecruitmentValidationTest` |
| Leave Configuration | 12 | 12 | 0 | `LeaveConfigTest` |
| **Total** | **63** | **63** | **0** | |

The separate `LeaveModuleTest` class has 13 tests, but its `<test>` entry is commented out in `testng.xml`; those tests are not included in the 63-test active suite.

## Test case coverage

### Login — 9 tests

- Valid login and successful dashboard access.
- Invalid username and password.
- Empty username, empty password, and both credentials empty.
- Forgot password navigation.
- Logout.
- Username with surrounding spaces and password with surrounding spaces.

### Admin — 10 tests

- Open the Admin module and verify the system users list.
- Search by username, Admin role, and Enabled status.
- Verify an invalid username returns no records.
- Reset the search form.
- Add a user with valid data and verify it appears in the list.
- Validate the empty Add User form.
- Delete the user created by the test and verify it is gone.

### Time — 4 tests

- Open the Time page.
- Open the Timesheets page.
- Verify the employee name field and pending action section on Timesheets.

### Dashboard — 4 tests

- Verify the Dashboard opens after login.
- Verify the Time menu is displayed.
- Verify the profile dropdown opens.
- Return to the Dashboard after navigating to Time.

### PIM — 6 tests

- Open PIM and verify its heading and Employee Information list.
- Open the Add Employee page.
- Verify employee name and employee ID search fields.
- Verify a search for a nonexistent employee returns no records.

### Recruitment — 18 tests

**Hiring workflow (9 tests):**

1. Create a QA vacancy and verify it is listed and active.
2. Add a candidate to the vacancy and verify the application starts at `Application Initiated`.
3. Shortlist the candidate and verify the `Shortlisted` status.
4. Schedule an interview and verify the `Interview Scheduled` status.
5. Mark the interview as passed and verify the `Interview Passed` status.
6. Offer the candidate the job and verify the `Job Offered` status.
7. Hire the candidate and verify the `Hired` status.
8. Search PIM and verify the hired candidate appears in the Employee List.
9. Delete the test vacancy.

**Validation and alternate paths (9 tests):**

10. Submit an empty candidate form and verify required-field validation.
11. Submit an invalid email address and verify it is rejected.
12. Submit an empty vacancy form and verify vacancy name, job title, and hiring manager are required.
13. Search for an unknown candidate without selecting an autocomplete result and verify validation appears.
14. Filter vacancies by vacancy name and Active status and verify the matching vacancy is returned.
15. Submit an interview form without its required title, interviewer, or date and verify validation appears.
16. Reject a candidate from the initial application stage and verify the rejected status.
17. Upload an unsupported resume file type and verify it is rejected.
18. Upload a resume larger than 1 MB and verify it is rejected.

### Leave Configuration — 12 tests

- **Leave Types (3):** page loads, table has records, Add button is displayed.
- **Work Week (3):** page loads, all seven day rows are displayed, Save button is displayed.
- **Holiday List (4):** page loads, filter buttons and Add button are displayed, search renders a valid result state.
- **Leave Period (2):** page loads and Save button is displayed.

## Running the active suite

1. Install a compatible Java Development Kit, Maven, and Google Chrome.
2. Open the project as a Maven project in IntelliJ IDEA and let Maven resolve the dependencies.
3. Right-click `testng.xml` and choose **Run**.

The suite runs the test classes listed in `testng.xml`. Recruitment workflow methods depend on one another and are intended to run in order. Some tests create temporary demo records and attempt to clean them up; the public demo site may be slow or change its data, so results can vary between runs.

## Framework design

- **Page Object Model:** Keeps page locators and browser interactions in page classes, separate from test assertions.
- **TestNG:** Organizes and executes test methods.
- **Maven:** Manages project dependencies and build configuration.
- **TestNG XML:** Configures the active test classes as a suite.

## Project structure

```text
Project/
├── src/
│   └── test/
│       └── java/
│           └── orangehrm/
│               ├── base/
│               │   └── BaseTest.java
│               ├── pages/
│               │   ├── AdminPage.java
│               │   ├── ApplyLeavePage.java
│               │   ├── DashboardPage.java
│               │   ├── LeaveConfigPage.java
│               │   ├── LeaveListPage.java
│               │   ├── LeavePage.java
│               │   ├── LoginPage.java
│               │   ├── MyLeaveListPage.java
│               │   ├── PIMPage.java
│               │   ├── RecruitmentPage.java
│               │   ├── TimePage.java
│               │   └── TimesheetPage.java
│               └── tests/
│                   ├── AdminModuleTest.java
│                   ├── DashboardTest.java
│                   ├── LeaveConfigTest.java
│                   ├── LeaveModuleTest.java
│                   ├── LoginTest.java
│                   ├── PIMModuleTest.java
│                   ├── RecruitmentTest.java
│                   ├── RecruitmentValidationTest.java
│                   └── TimeModuleTest.java
├── pom.xml
└── testng.xml
```

## Notes

- Test counts above are based on the active test classes in `testng.xml` and the latest full-suite result reported for this project.
- The OrangeHRM demo is shared public infrastructure; application availability and demo data can affect later runs.
- This project is for learning and portfolio demonstration.

## Learning outcomes

- Automating web applications with Selenium WebDriver and Java.
- Organizing page interactions with the Page Object Model.
- Writing and executing TestNG test cases across multiple application modules.
- Configuring and running an XML-based TestNG suite with Maven.
- Validating form behavior, navigation, records, and workflow status changes.

## Disclaimer

This project uses the publicly available OrangeHRM demo application for educational and portfolio purposes. Test results reflect the environment and execution date.
