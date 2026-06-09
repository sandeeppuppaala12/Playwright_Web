package com.automation.web.tests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.automation.web.listeners.ExtentListener;
import com.automation.web.manager.DriverManager;
import com.automation.web.pages.HomePage;
import com.automation.web.utils.CommonUtils;
import com.microsoft.playwright.Page;

@Listeners(ExtentListener.class)
public class TestManager {
	
	private static final Logger log = LoggerFactory.getLogger(TestManager.class);

	DriverManager manager;
	Page page;
	HomePage homePage;

	@BeforeTest
	@Parameters({"browser"})
	public void setUp(@Optional("chrome") String browser) throws Exception {
		manager = new DriverManager();
		CommonUtils.directorySetup();
		log.info("Browser parameter received: " + browser+". Starting setup...");
		// Priority: TestNG parameter -> system property -> config
		page = manager.initDriver(browser);
		log.info("Initialized Playwright driver and opened the page");
		homePage = new HomePage(page);
		
	}

	@AfterTest
	public void tearDown() {
		manager.terminateThread();
		log.info("Browser closed and Playwright driver terminated");
	}

}
