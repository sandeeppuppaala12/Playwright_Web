package com.automation.web.listeners;

import java.io.IOException;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.automation.web.manager.DriverManager;
import com.automation.web.utils.CommonUtils;
import com.automation.web.utils.ConfigParser;
import com.automation.web.utils.PathDirectory;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentListener implements ITestListener {

	private static String reportName = PathDirectory.REPORTS_PATH + "Report_" + CommonUtils.getDateTime() + ".html";

	private static ExtentReports report;
	ThreadLocal<ExtentTest> tests = new ThreadLocal<ExtentTest>();

	public static void ReportInitilizer() throws IOException {
		ConfigParser parse = new ConfigParser();
		if (report == null) {
			report = new ExtentReports();
			ExtentSparkReporter reporter = new ExtentSparkReporter(reportName);
			reporter.config().setReportName("Playwright Web Automation Report");
			reporter.config().setDocumentTitle("Automation Testing");
			report.attachReporter(reporter);
			report.setSystemInfo("URL", parse.getPropertyValue("URL"));
			report.setSystemInfo("ENVIRONMENT", parse.getPropertyValue("ENVIRONMENT"));
			report.setSystemInfo("VERSION", parse.getPropertyValue("VERSION"));
		}
	}

	@Override
	public void onTestStart(ITestResult result) {
		ExtentTest test = report.createTest(result.getMethod().getMethodName());
		tests.set(test);
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		tests.get().pass("Test Passed!!! ->" + result.getMethod().getMethodName());
		tests.get().log(Status.PASS, result.getMethod().getMethodName() + ": Passed");
	}

	@Override
	public void onTestFailure(ITestResult result) {
		tests.get().fail("Test Passed!!! ->" + result.getMethod().getMethodName());
		tests.get().log(Status.FAIL, result.getMethod().getMethodName() + ": Failed");
		// Screenshot methods
		tests.get().addScreenCaptureFromPath(DriverManager.screenshot());
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		tests.get().skip("Test Passed!!! ->" + result.getMethod().getMethodName());
		tests.get().log(Status.SKIP, result.getMethod().getMethodName() + ": Skipped");
		// Screenshot methods
		tests.get().addScreenCaptureFromPath(DriverManager.screenshot());
	}

	@Override
	public void onStart(ITestContext context) {
		try {
			ReportInitilizer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onFinish(ITestContext context) {
		report.flush();
		tests.remove();
	}

}
