package com.automation.web.manager;

import java.nio.file.Paths;

import com.automation.web.utils.ConfigParser;
import com.automation.web.utils.PathDirectory;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

public class DriverManager {

//	private Browser browser;
//	private Page page;
//	private Playwright pw;

	private static ThreadLocal<Page> page = new ThreadLocal<Page>();
	private static ThreadLocal<Playwright> play = new ThreadLocal<Playwright>();
	private static ThreadLocal<Browser> browser = new ThreadLocal<Browser>();

	public Page initDriver(String browserInstanceType) throws Exception {
		ConfigParser config = new ConfigParser();
		play.set(Playwright.create());
		if (browserInstanceType.toLowerCase().contains("chrome")) {
			browser.set(play.get().chromium()
					.launch(new BrowserType.LaunchOptions().setChannel("chrome").setHeadless(false)));

		} else if (browserInstanceType.toLowerCase().contains("firefox")) {
			browser.set(play.get().firefox()
					.launch(new BrowserType.LaunchOptions().setChannel("firefox").setHeadless(false)));

		} else if (browserInstanceType.toLowerCase().contains("edge")) {
//			browser = pw.chromium().launch(new BrowserType.LaunchOptions().setChannel("msedge")
//					.setArgs(Collections.singletonList("--start-maximized")).setHeadless(false));
//			browser = pw.webkit().launch(new BrowserType.LaunchOptions().setHeadless(false));// For webkit
			browser.set(play.get().chromium()
					.launch(new BrowserType.LaunchOptions().setChannel("msedge").setHeadless(false)));
		} else {
			throw new Exception("Invalid Browser !");
		}
		page.set(browser.get().newPage());
		page.get().navigate(config.getPropertyValue("URL"));
		return page.get();

	}

	public void terminateThread() {
		page.get().context().browser().close();
	}

	public static String screenshot() {
		String path = PathDirectory.SCREENSHOTS_PATH + System.currentTimeMillis() + ".png";
		page.get().screenshot(new Page.ScreenshotOptions().setPath(Paths.get(path)).setFullPage(true));
		return path;
	}

}
