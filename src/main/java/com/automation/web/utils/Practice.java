package com.automation.web.utils;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.Page.GetByTextOptions;

public class Practice {

	static ThreadLocal<Playwright> play = new ThreadLocal<Playwright>();
	static ThreadLocal<Browser> browser = new ThreadLocal<Browser>();
	static ThreadLocal<BrowserContext> browserCont = new ThreadLocal<BrowserContext>();
	static ThreadLocal<Page> page = new ThreadLocal<Page>();

	public static Page initDriver() {
		play.set(Playwright.create());
		browser.set(
				play.get().chromium().launch(new BrowserType.LaunchOptions().setChannel("chrome").setHeadless(false)));
		page.set(browser.get().newPage());
		page.get().navigate("https://www.gmail.com");
		return page.get();
	}

	public static void main(String[] args) {
		Page age = initDriver();
		age.getByLabel("Email or phone").fill("sandeep.puppalaa@gmail.com");
		age.getByRole(AriaRole.BUTTON,new Page.GetByRoleOptions().setName("Next")).click();
		age.getByRole(AriaRole.LINK,new Page.GetByRoleOptions().setName("Try again")).click();
	}

}
