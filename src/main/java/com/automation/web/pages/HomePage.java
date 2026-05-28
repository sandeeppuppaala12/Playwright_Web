package com.automation.web.pages;

import java.util.List;

import com.microsoft.playwright.Page;

public class HomePage {

	private Page page;

	private String menulist_Links = "div#menu.menu-custom-main-menu-container ul li a";

	public HomePage(Page page) {
		this.page = page;
	}

	public List<String> getMenuLinks() {
		return page.locator(menulist_Links).allTextContents();
	}

}
