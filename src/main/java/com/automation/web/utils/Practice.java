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
	
	public void instantiateBrowser() {
	
	Playwright play = Playwright.create();
	Browser browser= play.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false).setChannel("chrome"));
	BrowserContext context= browser.newContext();
	Page page= context.newPage();
	String url = "https://www.google.com";
	page.navigate(url);
	}
	
	public void instantiate_API() {
		Playwright play = Playwright.create();
		APIRequest request= play.request();
		APIRequestContext context= request.newContext();
		String url = "https://jsonplaceholder.typicode.com/posts/1";
		context.get(url);
	}
	
	public void windows() {
		Playwright play= Playwright.create();
		Browser browser= play.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false).setChannel("chrome"));
		BrowserContext cont= browser.newContext();
		
	}
	
	public static void Stringreverse(String str) {
		StringBuilder sb= new StringBuilder(str);
		System.out.println(sb.reverse());
		String st="";
		for(int i=str.length()-1;i>=0;i--) {
			st= st+str.charAt(i);
		}
		System.out.println(st);
	}
	
	public static void duplicates(String str) {
		HashMap<Character,Integer> map= new HashMap<Character,Integer>();
		char[] arr= str.toCharArray();
		for(int i=0;i<=arr.length-1;i++) {
			if(map.containsKey(arr[i])) {
				map.put(arr[i], map.get(arr[i])+1);
			}
			else {
				map.put(arr[i], 1);
			}
		}
		for(char a: map.keySet()) {
			System.out.println(a+": "+map.get(a));
		}
	}
	
	public static void removeDuplicates(String str) {
		Set<Character> set= new LinkedHashSet<Character>();
		char[] arr= str.toCharArray();
		for(char c: arr) {
			set.add(c);
		}
		System.out.println(set.toString());
	}
	
	public static void apid() {
		Playwright play= Playwright.create();
		Browser browser= play.chromium().launch(new BrowserType.LaunchOptions().setChannel("chrome").setHeadless(false));
		BrowserContext context= browser.newContext();
		Page page= context.newPage();
		page.route("**/api", route -> {
			route.fulfill(new FulfillOptions()
					.setStatus(200)
					.setContentType("application/json"));
		});
		
	}
	
	
	
	public static void main(String[] args) {
		removeDuplicates("Hello World");
	}

}
