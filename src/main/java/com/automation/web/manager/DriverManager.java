package com.automation.web.manager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.automation.web.utils.ConfigParser;
import com.automation.web.utils.PathDirectory;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DriverManager {

	private static final Logger log = LoggerFactory.getLogger(DriverManager.class);

	private static ThreadLocal<Page> page = new ThreadLocal<>();
	private static ThreadLocal<Playwright> play = new ThreadLocal<>();
	private static ThreadLocal<Browser> browser = new ThreadLocal<>();

	public Page initDriver(String browserInstanceType) {
		try {
			ConfigParser config = ConfigParser.getInstance();

			if (browserInstanceType == null || browserInstanceType.trim().isEmpty()) {
				log.info("BrowserType is: " + browserInstanceType);
				browserInstanceType = config.getPropertyValue("DEFAULT_BROWSER");
			}

			boolean headless = Boolean.parseBoolean(config.getPropertyValue("HEADLESS"));
			
//			Playwright.create().webkit().launch(new BrowserType.LaunchOptions().setHeadless(headless));

			play.set(Playwright.create());
			log.info("Starting Playwright for browser: {} (headless={})", browserInstanceType, headless);

			BrowserType.LaunchOptions options = new BrowserType.LaunchOptions().setHeadless(headless);

			if (browserInstanceType.toLowerCase().contains("webkit")) {
				browser.set(play.get().webkit().launch(options));
			} else if (browserInstanceType.toLowerCase().contains("firefox")) {
				browser.set(play.get().firefox().launch(options.setChannel("firefox")));
			} else if (browserInstanceType.toLowerCase().contains("edge") || browserInstanceType.toLowerCase().contains("msedge")) {
				browser.set(play.get().chromium().launch(options.setChannel("msedge")));
			} else if (browserInstanceType.toLowerCase().contains("chrome") || browserInstanceType.toLowerCase().contains("chromium")) {
				browser.set(play.get().chromium().launch(options.setChannel("chrome")));
			} else {
				throw new IllegalArgumentException("Invalid Browser: " + browserInstanceType);
			}

			page.set(browser.get().newPage());
			String url = config.getPropertyValue("URL");
			log.info("Navigating to URL: {}", url);
			page.get().navigate(url);
			return page.get();
		} catch (RuntimeException e) {
			log.error("Failed to initialize driver", e);
			throw e;
		}
	}

	public void terminateThread() {
		try {
			if (page.get() != null) {
				try {
					page.get().close();
				} catch (Exception ex) {
					log.warn("Error closing page", ex);
				}
			}

			try {
				if (browser.get() != null) {
					browser.get().close();
				}
			} catch (Exception ex) {
				log.warn("Error closing browser", ex);
			}

			try {
				if (play.get() != null) {
					play.get().close();
				}
			} catch (Exception ex) {
				log.warn("Error closing Playwright", ex);
			}
		} finally {
			page.remove();
			browser.remove();
			play.remove();
		}
	}

	public static String screenshot() {
		try {
			Path screenshotsDir = Paths.get(PathDirectory.SCREENSHOTS_PATH);
			Files.createDirectories(screenshotsDir);
			String path = PathDirectory.SCREENSHOTS_PATH + System.currentTimeMillis() + ".png";

			if (page.get() != null) {
				page.get().screenshot(new Page.ScreenshotOptions().setPath(Paths.get(path)).setFullPage(true));
				log.info("Taking screenshot and saving to: {}", path);
				return path;
			} else {
				log.warn("No page available for screenshot");
				return null;
			}

		} catch (Exception e) {
			log.error("Error taking screenshot", e);
			return null;
		}
	}

}
