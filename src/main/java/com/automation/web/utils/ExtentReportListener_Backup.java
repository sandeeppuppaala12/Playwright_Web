package com.automation.web.utils;

import java.util.Arrays;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentReportListener_Backup implements ITestListener {

	private static String reportName = "TestReport" + System.currentTimeMillis() + "html";

	private static ExtentReports reports;

	private static ThreadLocal<ExtentTest> tests = new ThreadLocal<ExtentTest>();

	private static void init() {
		ExtentSparkReporter report = new ExtentSparkReporter(reportName);
		report.config().setReportName("Playwright Automation");
		report.config().setDocumentTitle("Playwright Web Automation Testing");
		reports.attachReporter(report);
		reports.setSystemInfo("Environment", "QA");
	}

	@Override
	public void onTestStart(ITestResult result) {

		try {

			/*
			 * Read DataProvider Parameters
			 */
			Object[] data = result.getParameters();

			System.out.println("Received Parameters : " + Arrays.toString(data));

			/*
			 * Validate parameter count
			 */
			String dynamicTestName;

			if (data != null && data.length >= 2) {

				String tcId = String.valueOf(data[0]);

				String requestType = String.valueOf(data[1]);

				dynamicTestName = "[" + tcId + "] " + requestType;

			} else {

				/*
				 * Fallback name
				 */
				dynamicTestName = result.getMethod().getMethodName();
			}

			/*
			 * Create Extent Test
			 */
			ExtentTest test = reports.createTest(dynamicTestName);

			/*
			 * Store in ThreadLocal
			 */
			tests.set(test);

			System.out.println("Started Test : " + dynamicTestName);

		} catch (Exception e) {

			System.out.println("Error in onTestStart() : " + e.getMessage());

			e.printStackTrace();
		}
	}

	@Override
	public void onTestSuccess(ITestResult result) {

		if (tests.get() != null) {

			tests.get().pass("Test Passed");

		} else {

			System.out.println("ExtentTest is NULL in onTestSuccess()");
		}
	}

	@Override
	public void onTestFailure(ITestResult result) {

		if (tests.get() != null) {

			tests.get().fail(result.getThrowable());

		} else {

			System.out.println("ExtentTest is NULL in onTestFailure()");
		}
	}

	@Override
	public void onTestSkipped(ITestResult result) {

		if (tests.get() != null) {

			tests.get().skip("Test Skipped");

		} else {

			System.out.println("ExtentTest is NULL in onTestSkipped()");
		}
	}

	public void onStart(ITestContext context) {
		init();
	}

	public void onFinish(ITestContext context) {
		reports.flush();
		tests.remove();
	}

}
