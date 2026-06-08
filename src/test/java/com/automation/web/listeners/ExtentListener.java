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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtentListener implements ITestListener {

	private static final Logger log = LoggerFactory.getLogger(ExtentListener.class);

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
		tests.get().fail("Test Failed -> " + result.getMethod().getMethodName());
		tests.get().log(Status.FAIL, result.getMethod().getMethodName() + ": Failed");
		// Screenshot methods
		try {
			String path = DriverManager.screenshot();
			if (path != null) {
				tests.get().addScreenCaptureFromPath(path);
			} else {
				log.warn("Screenshot path was null for test: {}", result.getMethod().getMethodName());
			}
		} catch (Exception e) {
			log.error("Failed to attach screenshot for test {}", result.getMethod().getMethodName(), e);
		}
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		tests.get().skip("Test Skipped -> " + result.getMethod().getMethodName());
		tests.get().log(Status.SKIP, result.getMethod().getMethodName() + ": Skipped");
		// Screenshot methods
		try {
			String path = DriverManager.screenshot();
			if (path != null) {
				tests.get().addScreenCaptureFromPath(path);
			} else {
				log.warn("Screenshot path was null for skipped test: {}", result.getMethod().getMethodName());
			}
		} catch (Exception e) {
			log.error("Failed to attach screenshot for skipped test {}", result.getMethod().getMethodName(), e);
		}
	}

	@Override
	public void onStart(ITestContext context) {
		try {
			ReportInitilizer();
			log.info("Extent report initialized at {}", reportName);
		} catch (IOException e) {
			log.error("Failed to initialize Extent report", e);
		}
	}

	@Override
	public void onFinish(ITestContext context) {
		report.flush();
		tests.remove();
	}

}
