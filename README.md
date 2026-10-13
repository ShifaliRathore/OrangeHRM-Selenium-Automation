# OrangeHRM Time & Dashboard Automation Testing

## Project Overview

This project automates functional tests for the Time and Dashboard modules of the OrangeHRM demo application using Selenium WebDriver, Java, and TestNG.

The project follows the Page Object Model (POM) design pattern to separate page interactions from test logic and improve maintainability.

## Application Under Test

- **Application:** OrangeHRM Open Source Demo
- **URL:** https://opensource-demo.orangehrmlive.com/web/index.php/auth/login

## Technologies Used

- Java
- Selenium WebDriver
- TestNG
- Maven
- IntelliJ IDEA
- Chrome browser
- Page Object Model (POM)

## Project Structure

```text
Project/
├── src/
│   └── test/
│       └── java/
│           └── orangehrm/
│               ├── base/
│               │   └── BaseTest.java
│               ├── pages/
│               │   ├── LoginPage.java
│               │   ├── DashboardPage.java
│               │   ├── TimePage.java
│               │   └── TimesheetPage.java
│               └── tests/
│                   ├── LoginTest.java
│                   ├── AdminModuleTest.java
│                   ├── TimeModuleTest.java
│                   └── DashboardTest.java
├── pom.xml
└── testng.xml
```

## Key Test Scenarios

### Dashboard Module
- Verify that the Dashboard opens after successful login.
- Verify that the Dashboard heading is displayed.

### Time Module
- Verify that the Time page opens.
- Verify that the Timesheets page opens.

### Additional Test Coverage
- Login validation scenarios.
- Admin module navigation and user management scenarios.

## Framework Design

- **Page Object Model:** Keeps page locators and interactions in page classes.
- **TestNG:** Organizes and executes test methods.
- **Maven:** Manages project dependencies.
- **TestNG XML:** Executes configured test classes as a suite.

## How to Run the Tests

1. Install Java and IntelliJ IDEA.
2. Clone or download this repository.
3. Open the project in IntelliJ IDEA as a Maven project.
4. Allow Maven to resolve the dependencies.
5. Open `testng.xml`.
6. Right-click the file and select Run.

## Test Execution Results

The latest recorded full-suite run showed:

- Total tests passed: 22
- Failed tests: 0 reported

Results may vary when the application, browser, or test environment changes.

## Learning Outcomes

- Implementing Selenium WebDriver automation with Java.
- Designing page classes using POM.
- Writing and executing TestNG test cases.
- Configuring XML-based test suite execution.
- Validating web page navigation and displayed elements.

## Disclaimer

This project uses the publicly available OrangeHRM demo application for educational and portfolio purposes. Test results reflect the environment and execution date.
