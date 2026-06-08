package com.automation.web.utils;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Route.FulfillOptions;

public class Practice {
	
	
	
	
	
	public static void main(String[] args) {
		ConfigParser config = new ConfigParser();
		boolean headless = Boolean.parseBoolean(config.getPropertyValue("HEADLESS"));
		System.out.println(headless);
	}

}
