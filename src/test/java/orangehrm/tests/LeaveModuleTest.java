package orangehrm.tests;

import orangehrm.base.BaseTest;
import orangehrm.pages.ApplyLeavePage;
import orangehrm.pages.DashboardPage;
import orangehrm.pages.LeavePage;
import orangehrm.pages.LeaveListPage;
import orangehrm.pages.MyLeaveListPage;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * LeaveModuleTest
 *
 * Tests the Leave module across three pages:
 *   1. Apply Leave        — /leave/applyLeave
 *   2. My Leave List      — /leave/viewMyLeaveList
 *   3. Leave List         — /leave/viewLeaveList (admin view)
 *
 * Execution order (TestNG priority):
 *   1–4  → Apply Leave    (TC-AL-01 to TC-AL-04)
 *   5–7  → My Leave List  (TC-ML-01 to TC-ML-03)
 *   8–12 → Leave List     (TC-LL-01 to TC-LL-05)
 *   13   → E2E flow       (TC-E2E-01)
 *
 * All tests call loginAsAdmin() to guard against session rotation (DEF-LM-001).
 * Date format for all date inputs: "yyyy-dd-mm" (OrangeHRM picker placeholder).
 */
public class LeaveModuleTest extends BaseTest {

    private static final String LEAVE_TYPE    = "CAN - Bereavement";
    private static final String FROM_DATE     = "2026-01-08";
    private static final String TO_DATE       = "2026-03-08";
    private static final String LEAVE_COMMENT = "Automated test - E2E";

    // -------------------------------------------------------
    // APPLY LEAVE
    // -------------------------------------------------------

    @Test(priority = 1)
    public void verifyApplyLeavePageLoads() {
        loginAsAdmin();
        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.clickLeaveMenu();
        LeavePage leavePage = new LeavePage(driver);
        leavePage.goToApplyLeave();
        ApplyLeavePage applyLeavePage = new ApplyLeavePage(driver);
        Assert.assertTrue(
                applyLeavePage.isApplyLeavePageDisplayed(),
                "TC-AL-01 FAILED: Apply Leave page heading not visible");
        System.out.println("TC-AL-01 PASSED: Apply Leave page loaded successfully");
    }

    @Test(priority = 2)
    public void verifyApplyLeaveFormElementsAreDisplayed() {
        loginAsAdmin();
        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.clickLeaveMenu();
        LeavePage leavePage = new LeavePage(driver);
        leavePage.goToApplyLeave();
        ApplyLeavePage applyLeavePage = new ApplyLeavePage(driver);
        Assert.assertTrue(
                applyLeavePage.areFormElementsDisplayed(),
                "TC-AL-02 FAILED: One or more form elements are not displayed");
        System.out.println("TC-AL-02 PASSED: All Apply Leave form elements are visible");
    }

    @Test(priority = 3)
    public void verifyRequiredValidationOnEmptyFormSubmit() {
        loginAsAdmin();
        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.clickLeaveMenu();
        LeavePage leavePage = new LeavePage(driver);
        leavePage.goToApplyLeave();
        ApplyLeavePage applyLeavePage = new ApplyLeavePage(driver);
        applyLeavePage.clickApply();
        Assert.assertTrue(
                applyLeavePage.isRequiredValidationDisplayed(),
                "TC-AL-03 FAILED: Required validation not shown on empty submit");
        System.out.println("TC-AL-03 PASSED: Required validation triggered on empty submit");
    }

    @Test(priority = 4)
    public void verifySuccessfulLeaveApplicationSubmission() {
        loginAsAdmin();
        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.clickLeaveMenu();
        LeavePage leavePage = new LeavePage(driver);
        leavePage.goToApplyLeave();
        ApplyLeavePage applyLeavePage = new ApplyLeavePage(driver);
        applyLeavePage.applyForLeave(LEAVE_TYPE, FROM_DATE, TO_DATE, LEAVE_COMMENT);
        Assert.assertTrue(
                applyLeavePage.isSuccessMessageDisplayed(),
                "TC-AL-04 FAILED: 'Successfully Saved' toast not displayed");
        System.out.println("TC-AL-04 PASSED: Leave application submitted successfully");
    }

    // -------------------------------------------------------
    // MY LEAVE LIST
    // -------------------------------------------------------

    @Test(priority = 5)
    public void verifyMyLeaveListPageLoads() {
        loginAsAdmin();
        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.clickLeaveMenu();
        LeavePage leavePage = new LeavePage(driver);
        leavePage.goToMyLeaveList();
        MyLeaveListPage myLeaveListPage = new MyLeaveListPage(driver);
        Assert.assertTrue(
                myLeaveListPage.isMyLeaveListPageDisplayed(),
                "TC-ML-01 FAILED: My Leave List page heading not visible");
        System.out.println("TC-ML-01 PASSED: My Leave List page loaded successfully");
    }

    @Test(priority = 6)
    public void verifySearchWithDefaultFiltersDoesNotCrash() {
        loginAsAdmin();
        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.clickLeaveMenu();
        LeavePage leavePage = new LeavePage(driver);
        leavePage.goToMyLeaveList();
        MyLeaveListPage myLeaveListPage = new MyLeaveListPage(driver);
        myLeaveListPage.clickSearch();
        boolean hasResults  = myLeaveListPage.isRecordDisplayed();
        boolean hasNoRecord = myLeaveListPage.isNoRecordsMessageDisplayed();
        Assert.assertTrue(
                hasResults || hasNoRecord,
                "TC-ML-02 FAILED: Page is in an unexpected state after default search");
        System.out.println("TC-ML-02 PASSED: Default search rendered a valid page state");
    }

    @Test(priority = 7)
    public void verifyResetClearsFiltersOnMyLeaveList() {
        loginAsAdmin();
        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.clickLeaveMenu();
        LeavePage leavePage = new LeavePage(driver);
        leavePage.goToMyLeaveList();
        MyLeaveListPage myLeaveListPage = new MyLeaveListPage(driver);
        myLeaveListPage.selectLeaveStatus("Pending Approval");
        myLeaveListPage.clickReset();
        Assert.assertTrue(
                myLeaveListPage.areFiltersReset(),
                "TC-ML-03 FAILED: Filters not reset to default after clicking Reset");
        System.out.println("TC-ML-03 PASSED: Reset cleared all filters on My Leave List");
    }

    // -------------------------------------------------------
    // LEAVE LIST (ADMIN VIEW)
    // -------------------------------------------------------

    @Test(priority = 8)
    public void verifyLeaveListPageLoads() {
        loginAsAdmin();
        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.clickLeaveMenu();
        LeavePage leavePage = new LeavePage(driver);
        leavePage.goToLeaveList();
        LeaveListPage leaveListPage = new LeaveListPage(driver);
        Assert.assertTrue(
                leaveListPage.isLeaveListPageDisplayed(),
                "TC-LL-01 FAILED: Leave List page heading not visible");
        System.out.println("TC-LL-01 PASSED: Leave List page loaded successfully");
    }

    @Test(priority = 9)
    public void verifySearchByValidEmployeeNameReturnsRecords() {
        loginAsAdmin();
        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.clickLeaveMenu();
        LeavePage leavePage = new LeavePage(driver);
        leavePage.goToLeaveList();
        LeaveListPage leaveListPage = new LeaveListPage(driver);
        leaveListPage.enterEmployeeName("Rahul Doe");
        leaveListPage.clickSearch();
        Assert.assertTrue(
                leaveListPage.isRecordDisplayed(),
                "TC-LL-02 FAILED: No records returned for valid employee 'Rahul Doe'");
        System.out.println("TC-LL-02 PASSED: Records returned for valid employee name search");
    }

    @Test(priority = 10)
    public void verifySearchByInvalidEmployeeNameShowsNoRecords() {
        loginAsAdmin();
        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.clickLeaveMenu();
        LeavePage leavePage = new LeavePage(driver);
        leavePage.goToLeaveList();
        LeaveListPage leaveListPage = new LeaveListPage(driver);
        leaveListPage.enterInvalidEmployeeName("ZZZZINVALID99999");
        leaveListPage.clickSearch();
        Assert.assertTrue(
                leaveListPage.isNoRecordsMessageDisplayed(),
                "TC-LL-03 FAILED: 'No Records Found' not shown for invalid employee name");
        System.out.println("TC-LL-03 PASSED: 'No Records Found' displayed for invalid employee");
    }

    @Test(priority = 11)
    public void verifySearchByLeaveStatusPendingApproval() {
        loginAsAdmin();
        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.clickLeaveMenu();
        LeavePage leavePage = new LeavePage(driver);
        leavePage.goToLeaveList();
        LeaveListPage leaveListPage = new LeaveListPage(driver);
        leaveListPage.selectLeaveStatus("Pending Approval");
        leaveListPage.clickSearch();
        Assert.assertTrue(
                leaveListPage.isRecordDisplayed(),
                "TC-LL-04 FAILED: No records returned for status 'Pending Approval'");
        System.out.println("TC-LL-04 PASSED: Records returned for 'Pending Approval' status");
    }

    @Test(priority = 12)
    public void verifyResetClearsFiltersOnLeaveList() {
        loginAsAdmin();
        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.clickLeaveMenu();
        LeavePage leavePage = new LeavePage(driver);
        leavePage.goToLeaveList();
        LeaveListPage leaveListPage = new LeaveListPage(driver);
        leaveListPage.selectLeaveStatus("Pending Approval");
        leaveListPage.clickReset();
        Assert.assertTrue(
                leaveListPage.areFiltersReset(),
                "TC-LL-05 FAILED: Filters not reset to default after clicking Reset");
        System.out.println("TC-LL-05 PASSED: Reset cleared all filters on Leave List");
    }

    // -------------------------------------------------------
    // E2E — FULL CROSS-PAGE FLOW
    // -------------------------------------------------------

    /**
     * TC-E2E-01: Applies leave, then verifies it appears in My Leave List
     * and Leave List in a single browser session.
     * ensureAdminSession() guards against mid-test session rotation between steps.
     */
    @Test(priority = 13)
    public void verifyAppliedLeaveAppearsInMyLeaveListAndLeaveList() {
        loginAsAdmin();
        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.clickLeaveMenu();
        LeavePage leavePage = new LeavePage(driver);

        // Step 1: Apply for leave
        leavePage.goToApplyLeave();
        ApplyLeavePage applyLeavePage = new ApplyLeavePage(driver);
        applyLeavePage.applyForLeave(LEAVE_TYPE, FROM_DATE, TO_DATE, LEAVE_COMMENT);
        Assert.assertTrue(
                applyLeavePage.isSuccessMessageDisplayed(),
                "TC-E2E-01 Step 1 FAILED: Leave not saved — toast not displayed");
        System.out.println("TC-E2E-01 Step 1 PASSED: Leave applied successfully");

        // Step 2: Verify in My Leave List
        ensureAdminSession();
        dashboardPage.clickLeaveMenu();
        leavePage.goToMyLeaveList();
        MyLeaveListPage myLeaveListPage = new MyLeaveListPage(driver);
        myLeaveListPage.selectLeaveStatus("Pending Approval");
        myLeaveListPage.clickSearch();
        Assert.assertTrue(
                myLeaveListPage.isRecordDisplayed(),
                "TC-E2E-01 Step 2 FAILED: Applied leave not visible in My Leave List");
        System.out.println("TC-E2E-01 Step 2 PASSED: Leave visible in My Leave List");

        // Step 3: Verify in Leave List (admin view)
        ensureAdminSession();
        dashboardPage.clickLeaveMenu();
        leavePage.goToLeaveList();
        LeaveListPage leaveListPage = new LeaveListPage(driver);
        leaveListPage.selectLeaveType(LEAVE_TYPE);
        leaveListPage.selectLeaveStatus("Pending Approval");
        leaveListPage.clickSearch();
        Assert.assertTrue(
                leaveListPage.isRecordDisplayed(),
                "TC-E2E-01 Step 3 FAILED: Applied leave not visible in Leave List (admin view)");
        System.out.println("TC-E2E-01 Step 3 PASSED: Leave visible in Leave List (admin view)");
        System.out.println("TC-E2E-01 PASSED: Full leave lifecycle verified across all 3 pages");
    }
}
