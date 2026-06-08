package com.automation.web.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

public class HomePageTest extends TestManager {

//	@Test
//	public void validateMenuLinks() {
//		List<String> links = homePage.getMenuLinks();
//		for (String li : links) {
//			System.out.println(li);
//		}
//	}
	
	@Test
	public void validateMenuLinks() {
		Assert.assertEquals(false, true);
	}
	
	@Test
	public void validateMenuLinks_2() {
		Assert.assertEquals(false, false);
	}

}
