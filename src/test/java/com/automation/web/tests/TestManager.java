package com.automation.web.tests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Parameters;
import org.testng.annotations.Optional;

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
	public void setUp(@Optional String browser) throws Exception {
		manager = new DriverManager();
		log.info("Browser parameter received: " + browser+". Starting setup...");
		// Priority: TestNG parameter -> system property -> config
		com.automation.web.utils.ConfigParser config = new com.automation.web.utils.ConfigParser();

		if (browser == null || browser.trim().isEmpty()) {
			browser = System.getProperty("DEFAULT_BROWSER");
		}

		if (browser == null || browser.trim().isEmpty()) {
			browser = config.getPropertyValue("DEFAULT_BROWSER");
			log.info("Browser parameter received: " + browser+". Setting up with Default browser...");
		}
		
		page = manager.initDriver(browser);
		log.info("Initialized Playwright driver and opened the page");
		homePage = new HomePage(page);
		CommonUtils.directorySetup();
	}

	@AfterTest
	public void tearDown() {
		manager.terminateThread();
		log.info("Browser closed and Playwright driver terminated");
	}

}
