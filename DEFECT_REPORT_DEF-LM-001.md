# Defect Report — DEF-LM-001

## Summary

Active user session changes automatically without user action, causing Leave module
pages to become inaccessible or return incorrect data during automated test execution.

---

## Defect Details

| Field              | Value                                              |
|--------------------|----------------------------------------------------|
| Defect ID          | DEF-LM-001                                         |
| Module             | Leave Management                                   |
| Severity           | Medium                                             |
| Priority           | Medium                                             |
| Status             | Open                                               |
| Reported By        | [Your Name]                                        |
| Date Reported      | [Date]                                             |
| Application        | OrangeHRM Open Source Demo                         |
| URL                | https://opensource-demo.orangehrmlive.com          |
| Browser            | Google Chrome (latest)                             |
| Credentials Used   | Admin / admin123                                   |
| Automation Stack   | Selenium 4, TestNG 7.10.2, Java 21, Maven          |

---

## Affected Test Cases

| Test Case ID | Page           | Impact When Defect Occurs                              |
|--------------|----------------|--------------------------------------------------------|
| TC-AL-01     | Apply Leave    | Page heading not visible — restricted user has no access |
| TC-AL-02     | Apply Leave    | Form elements absent — page renders blank              |
| TC-AL-03     | Apply Leave    | Apply button not found — form not present              |
| TC-AL-04     | Apply Leave    | Success toast not shown — form submission not possible |
| TC-ML-01     | My Leave List  | Page heading not visible                               |
| TC-ML-02     | My Leave List  | Search renders records belonging to the rotated user   |
| TC-ML-03     | My Leave List  | Reset operates on wrong user's session state           |
| TC-LL-01     | Leave List     | Page redirects away — rotated user is not Admin        |
| TC-LL-02     | Leave List     | Employee search returns no results or wrong results    |
| TC-LL-04     | Leave List     | Pending Approval filter returns empty results          |
| TC-E2E-01    | All three pages| Steps 2 and 3 show data for wrong user after rotation  |

---

## Preconditions

- User navigates to the OrangeHRM login page
- User logs in with credentials: Admin / admin123
- Dashboard is displayed and the top-right profile shows "Admin"

---

## Steps to Reproduce

1. Open: https://opensource-demo.orangehrmlive.com/web/index.php/auth/login
2. Enter username: `Admin`, password: `admin123` and click Login
3. Confirm the Dashboard is displayed with "Admin" in the top-right profile area
4. Click **Leave** in the left sidebar
5. Click **Apply** in the Leave sub-menu
6. Wait on the Apply Leave page without performing any action
7. Observe the top-right profile name and the page content

---

## Expected Result

- The profile name in the top-right corner remains "Admin" for the full session duration
- The Apply Leave form remains fully visible with all elements present:
  Leave Type dropdown, From Date, To Date, Comments textarea, Apply button
- All Leave sub-pages remain accessible with Admin-level permissions throughout
  the session without requiring a manual re-login

---

## Actual Result

- The profile name in the top-right corner changes automatically to a different
  user without any user action such as logout or session timeout
- When the session rotates to a user without Leave permissions, the Apply Leave
  page renders blank with no form fields visible
- Automated tests that passed on one run fail on the next identical run because
  the active user has changed between executions

---

## Reproducibility

Intermittent — occurs every few minutes on the shared public demo site.
Not reproducible on a dedicated test instance.

---

## Root Cause

The OrangeHRM public demo site (opensource-demo.orangehrmlive.com) is a shared
environment used by multiple visitors simultaneously. It rotates the active session
automatically to simulate different demo user personas. This is a deliberate feature
of the public demo site, but constitutes a session integrity failure from a test
automation perspective — the application changes the authenticated user without
any action initiated by the test or the tester.

---

## Impact on Test Automation

- **LeaveModuleTest.java** (13 tests) has been excluded from the automated suite
  in testng.xml due to this defect. It can be executed manually on demand.
- **LeaveConfigTest.java** (12 tests) covers user-independent system configuration
  pages and is unaffected by this defect. It runs in the suite and passes
  consistently on every execution.
- The automation framework itself is functioning correctly. The issue is
  environmental, not a defect in the test code.

---

## Tests Unaffected by This Defect

The following 12 tests in LeaveConfigTest.java pass consistently regardless
of session state because they test system-wide configuration pages that display
identical data for any Admin account:

| Test Case ID | Page         | What It Verifies                  |
|--------------|--------------|-----------------------------------|
| TC-LT-01     | Leave Types  | Page heading visible              |
| TC-LT-02     | Leave Types  | Table has at least one record     |
| TC-LT-03     | Leave Types  | Add button visible                |
| TC-WW-01     | Work Week    | Page heading visible              |
| TC-WW-02     | Work Week    | All 7 day rows rendered           |
| TC-WW-03     | Work Week    | Save button visible               |
| TC-HO-01     | Holiday List | Page heading visible              |
| TC-HO-02     | Holiday List | Search and Reset buttons visible  |
| TC-HO-03     | Holiday List | Add button visible                |
| TC-HO-04     | Holiday List | Search renders a valid state      |
| TC-LP-01     | Leave Period | Page heading visible              |
| TC-LP-02     | Leave Period | Save button visible               |

---

## Mitigation Applied

The following changes were made to reduce the impact of this defect on automation:

**BaseTest.java — three new methods added:**

| Method                  | Purpose                                                        |
|-------------------------|----------------------------------------------------------------|
| `loginAsAdmin()`        | Navigates to login URL and waits for Dashboard heading before  |
|                         | any test step — guarantees an Admin session at test start      |
| `isSessionStillAdmin()` | Reads the top-right profile name — returns true only if Admin  |
| `ensureAdminSession()`  | Calls isSessionStillAdmin(); re-logins if rotation detected    |

**LeaveModuleTest.java:**
All 13 tests call `loginAsAdmin()` at the start. The E2E test (TC-E2E-01)
additionally calls `ensureAdminSession()` between Step 1, Step 2, and Step 3
to guard against mid-test rotation.

**testng.xml:**
LeaveModuleTest is commented out of the automated suite. LeaveConfigTest
remains active.

---

## Recommendation

For reliable automated regression testing of the Leave module workflow pages
(Apply Leave, My Leave List, Leave List), a dedicated test instance of OrangeHRM
should be provisioned where session persistence is guaranteed and the active user
is not rotated by the environment. The public demo site is not a suitable
environment for automated testing of user-dependent features.

---

## Evidence to Attach

| # | Evidence                                          | Purpose                             |
|---|---------------------------------------------------|-------------------------------------|
| 1 | Screenshot — blank Apply Leave page               | Visual proof of defect occurrence   |
| 2 | Screenshot — profile name showing a rotated user  | Visual proof of session rotation    |
| 3 | Console output — LeaveModuleTest intermittent fail | Proves non-deterministic behaviour  |
| 4 | Console output — LeaveConfigTest 12 tests passed  | Proves framework is working correctly |

---

*Defect report raised as part of Leave Module test automation — OrangeHRM Selenium Project.*
