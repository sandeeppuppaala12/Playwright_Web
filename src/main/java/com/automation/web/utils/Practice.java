package com.automation.web.utils;

public class Practice {
	
	
	
	
	
	public static void main(String[] args) {
		boolean headless = Boolean.parseBoolean(ConfigParser.getInstance().getPropertyValue("HEADLESS"));
		System.out.println(headless);
	}

}
