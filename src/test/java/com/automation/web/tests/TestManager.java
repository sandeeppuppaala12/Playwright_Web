package com.automation.web.tests;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;

import com.automation.web.listeners.ExtentListener;
import com.automation.web.manager.DriverManager;
import com.automation.web.pages.HomePage;
import com.automation.web.utils.CommonUtils;
import com.microsoft.playwright.Page;

@Listeners(ExtentListener.class)
public class TestManager {

	DriverManager manager;
	Page page;
	HomePage homePage;

	@BeforeTest
	public void setUp() throws Exception {
		manager = new DriverManager();
		page = manager.initDriver("Chrome");
		homePage = new HomePage(page);
		CommonUtils.directorySetup();
	}

	@AfterTest
	public void tearDown() {
		manager.terminateThread();
	}

}
