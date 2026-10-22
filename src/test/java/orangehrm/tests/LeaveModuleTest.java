package orangehrm.tests;

import orangehrm.base.BaseTest;
import orangehrm.pages.ApplyLeavePage;
import orangehrm.pages.DashboardPage;
import orangehrm.pages.LeavePage;
import orangehrm.pages.LeaveListPage;
import orangehrm.pages.MyLeaveListPage;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

/**
 * Leave workflow tests: Apply Leave (TC-AL), My Leave List (TC-ML),
 * Leave List (TC-LL) and one E2E flow (TC-E2E-01).
 *
 * Tests are skipped when the Leave menu is missing (see DEF-LM-001).
 */
public class LeaveModuleTest extends BaseTest {

    private static final String LEAVE_TYPE    = "CAN - Bereavement";
    private static final String LEAVE_COMMENT = "Automated test - E2E";

    // Day equals month, so the dates are valid in both yyyy-mm-dd and yyyy-dd-mm
    private static final String APPLY_FROM_DATE = "2026-09-09";
    private static final String APPLY_TO_DATE   = "2026-09-09";
    private static final String E2E_FROM_DATE   = "2026-11-11";
    private static final String E2E_TO_DATE     = "2026-11-11";

    // -------------------------------------------------------
    // SHARED SETUP
    // -------------------------------------------------------

    /** Logs in as Admin and opens the Leave menu. */
    private LeavePage loginAndOpenLeaveMenu() {
        loginAsAdmin();
        return openLeaveMenu();
    }

    /** Opens the Leave menu; skips the test if it is missing (DEF-LM-001). */
    private LeavePage openLeaveMenu() {
        DashboardPage dashboardPage = new DashboardPage(driver);
        if (!dashboardPage.isLeaveMenuDisplayed()) {
            throw new SkipException(
                    "Leave menu not available for the logged-in user on the shared demo site (DEF-LM-001)");
        }
        dashboardPage.clickLeaveMenu();
        return new LeavePage(driver);
    }

    // -------------------------------------------------------
    // APPLY LEAVE
    // -------------------------------------------------------

    @Test(priority = 1)
    public void verifyApplyLeavePageLoads() {
        LeavePage leavePage = loginAndOpenLeaveMenu();
        leavePage.goToApplyLeave();
        ApplyLeavePage applyLeavePage = new ApplyLeavePage(driver);
        Assert.assertTrue(
                applyLeavePage.isApplyLeavePageDisplayed(),
                "TC-AL-01 FAILED: Apply Leave page heading not visible");
        System.out.println("TC-AL-01 PASSED: Apply Leave page loaded successfully");
    }

    @Test(priority = 2)
    public void verifyApplyLeaveFormElementsAreDisplayed() {
        LeavePage leavePage = loginAndOpenLeaveMenu();
        leavePage.goToApplyLeave();
        ApplyLeavePage applyLeavePage = new ApplyLeavePage(driver);
        Assert.assertTrue(
                applyLeavePage.areFormElementsDisplayed(),
                "TC-AL-02 FAILED: One or more form elements are not displayed");
        System.out.println("TC-AL-02 PASSED: All Apply Leave form elements are visible");
    }

    @Test(priority = 3)
    public void verifyRequiredValidationOnEmptyFormSubmit() {
        LeavePage leavePage = loginAndOpenLeaveMenu();
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
        LeavePage leavePage = loginAndOpenLeaveMenu();
        leavePage.goToApplyLeave();
        ApplyLeavePage applyLeavePage = new ApplyLeavePage(driver);
        applyLeavePage.applyForLeave(LEAVE_TYPE, APPLY_FROM_DATE, APPLY_TO_DATE, LEAVE_COMMENT);
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
        LeavePage leavePage = loginAndOpenLeaveMenu();
        leavePage.goToMyLeaveList();
        MyLeaveListPage myLeaveListPage = new MyLeaveListPage(driver);
        Assert.assertTrue(
                myLeaveListPage.isMyLeaveListPageDisplayed(),
                "TC-ML-01 FAILED: My Leave List page heading not visible");
        System.out.println("TC-ML-01 PASSED: My Leave List page loaded successfully");
    }

    @Test(priority = 6)
    public void verifySearchWithDefaultFiltersDoesNotCrash() {
        LeavePage leavePage = loginAndOpenLeaveMenu();
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
        LeavePage leavePage = loginAndOpenLeaveMenu();
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
        LeavePage leavePage = loginAndOpenLeaveMenu();
        leavePage.goToLeaveList();
        LeaveListPage leaveListPage = new LeaveListPage(driver);
        Assert.assertTrue(
                leaveListPage.isLeaveListPageDisplayed(),
                "TC-LL-01 FAILED: Leave List page heading not visible");
        System.out.println("TC-LL-01 PASSED: Leave List page loaded successfully");
    }

    @Test(priority = 9)
    public void verifySearchByValidEmployeeNameReturnsRecords() {
        LeavePage leavePage = loginAndOpenLeaveMenu();
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
        LeavePage leavePage = loginAndOpenLeaveMenu();
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
        LeavePage leavePage = loginAndOpenLeaveMenu();
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
        LeavePage leavePage = loginAndOpenLeaveMenu();
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

    /** TC-E2E-01: Apply leave, then verify it in My Leave List and Leave List. */
    @Test(priority = 13)
    public void verifyAppliedLeaveAppearsInMyLeaveListAndLeaveList() {
        LeavePage leavePage = loginAndOpenLeaveMenu();

        // Step 1: Apply for leave
        leavePage.goToApplyLeave();
        ApplyLeavePage applyLeavePage = new ApplyLeavePage(driver);
        applyLeavePage.applyForLeave(LEAVE_TYPE, E2E_FROM_DATE, E2E_TO_DATE, LEAVE_COMMENT);
        Assert.assertTrue(
                applyLeavePage.isSuccessMessageDisplayed(),
                "TC-E2E-01 Step 1 FAILED: Leave not saved — toast not displayed");
        System.out.println("TC-E2E-01 Step 1 PASSED: Leave applied successfully");

        // Step 2: Verify in My Leave List
        leavePage = openLeaveMenu();
        leavePage.goToMyLeaveList();
        MyLeaveListPage myLeaveListPage = new MyLeaveListPage(driver);
        myLeaveListPage.selectLeaveStatus("Pending Approval");
        myLeaveListPage.clickSearch();
        Assert.assertTrue(
                myLeaveListPage.isRecordDisplayed(),
                "TC-E2E-01 Step 2 FAILED: Applied leave not visible in My Leave List");
        System.out.println("TC-E2E-01 Step 2 PASSED: Leave visible in My Leave List");

        // Step 3: Verify in Leave List (admin view)
        leavePage = openLeaveMenu();
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
