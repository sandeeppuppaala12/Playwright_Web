package com.automation.web.pages;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.playwright.Page;

public class HomePage {
	
	private static final Logger log = LoggerFactory.getLogger(HomePage.class);

	private Page page;

	private String menulist_Links = "div#menu.menu-custom-main-menu-container ul li a";

	public HomePage(Page page) {
		this.page = page;
	}

	public List<String> getMenuLinks() {
		return page.locator(menulist_Links).allTextContents();
	}

}
