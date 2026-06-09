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

	private static volatile ExtentReports report;
	private static final Object lockReport = new Object();
	private static volatile boolean reportInitialized = false;
	
	ThreadLocal<ExtentTest> tests = new ThreadLocal<ExtentTest>();

	public static void ReportInitilizer() throws IOException {
		// Double-checked locking pattern for thread-safe singleton initialization
		if (!reportInitialized) {
			synchronized (lockReport) {
				if (!reportInitialized) {
					ConfigParser parse = ConfigParser.getInstance();
					report = new ExtentReports();
					ExtentSparkReporter reporter = new ExtentSparkReporter(reportName);
					reporter.config().setReportName("Playwright Web Automation Report");
					reporter.config().setDocumentTitle("Automation Testing");
					report.attachReporter(reporter);
					report.setSystemInfo("URL", parse.getPropertyValue("URL"));
					report.setSystemInfo("ENVIRONMENT", parse.getPropertyValue("ENVIRONMENT"));
					report.setSystemInfo("VERSION", parse.getPropertyValue("VERSION"));
					report.setSystemInfo("OS", System.getProperty("os.name"));
					report.setSystemInfo("Java Version", System.getProperty("java.version"));
					log.info("Extent report initialized successfully at {}", reportName);
					reportInitialized = true;
				}
			}
		}
	}

	@Override
	public void onTestStart(ITestResult result) {
		// Get browser parameter from test context and append to test name
		String browserName = result.getTestContext().getCurrentXmlTest().getName();
		String browser = extractBrowserName(browserName);
		String testMethodName = result.getMethod().getMethodName();
		String uniqueTestName = testMethodName + "_" + browser;
		
		synchronized (lockReport) {
			ExtentTest test = report.createTest(uniqueTestName, "Browser: " + browser);
			tests.set(test);
			log.debug("Test started: {} on browser: {}", testMethodName, browser);
		}
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		synchronized (lockReport) {
			if (tests.get() != null) {
				tests.get().pass("Test Passed!!! ->" + result.getMethod().getMethodName());
				tests.get().log(Status.PASS, result.getMethod().getMethodName() + ": Passed");
				log.info("Test PASSED: {} on browser: {}", result.getMethod().getMethodName(), 
					extractBrowserName(result.getTestContext().getCurrentXmlTest().getName()));
			}
		}
	}

	@Override
	public void onTestFailure(ITestResult result) {
		synchronized (lockReport) {
			if (tests.get() != null) {
				tests.get().fail("Test Failed -> " + result.getMethod().getMethodName());
				tests.get().log(Status.FAIL, result.getMethod().getMethodName() + ": Failed");
				
				// Screenshot methods
				try {
					String path = DriverManager.screenshot();
					if (path != null) {
						tests.get().addScreenCaptureFromPath(path);
						log.info("Screenshot attached for failed test: {}", path);
					} else {
						log.warn("Screenshot path was null for test: {}", result.getMethod().getMethodName());
					}
				} catch (Exception e) {
					log.error("Failed to attach screenshot for test {}", result.getMethod().getMethodName(), e);
				}
				log.error("Test FAILED: {} on browser: {}", result.getMethod().getMethodName(),
					extractBrowserName(result.getTestContext().getCurrentXmlTest().getName()));
			}
		}
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		synchronized (lockReport) {
			if (tests.get() != null) {
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
				log.warn("Test SKIPPED: {} on browser: {}", result.getMethod().getMethodName(),
					extractBrowserName(result.getTestContext().getCurrentXmlTest().getName()));
			}
		}
	}

	@Override
	public void onStart(ITestContext context) {
		try {
			ReportInitilizer();
			log.info("Test execution started: {}", context.getName());
		} catch (IOException e) {
			log.error("Failed to initialize Extent report", e);
		}
	}

	@Override
	public void onFinish(ITestContext context) {
		synchronized (lockReport) {
			if (report != null) {
				report.flush();
				log.info("Extent report flushed for test context: {}", context.getName());
			}
		}
		tests.remove();
	}

	/**
	 * Extracts browser name from test name pattern: "Playwright_Web_Test_<Browser>"
	 * @param testName Full test name from XML configuration
	 * @return Browser name (Chrome, Firefox, Edge) or default "Unknown"
	 */
	private static String extractBrowserName(String testName) {
		if (testName == null || testName.isEmpty()) {
			return "Unknown";
		}
		
		if (testName.toLowerCase().contains("chrome")) {
			return "Chrome";
		} else if (testName.toLowerCase().contains("firefox")) {
			return "Firefox";
		} else if (testName.toLowerCase().contains("edge")) {
			return "Edge";
		} else if (testName.toLowerCase().contains("webkit")) {
			return "WebKit";
		}
		return "Unknown";
	}
}
