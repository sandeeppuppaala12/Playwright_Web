package com.automation.web.utils;

import java.io.File;

/**
 * @author sans12 This class has the paths to all the folder and files
 */
public class PathDirectory {

	public static final String PROJECT_PATH = System.getProperty("user.dir");

	public static final String SRC_PATH = PROJECT_PATH + File.separator + "src" + File.separator + "main"
			+ File.separator + "java" + File.separator;

	public static final String SRC_RESOURCE_PATH = PROJECT_PATH + File.separator + "src" + File.separator + "main"
			+ File.separator + "resources" + File.separator;

	public static final String TEST_PATH = PROJECT_PATH + File.separator + "src" + File.separator + "test"
			+ File.separator + "java" + File.separator;

	public static final String TEST_RESOURCE_PATH = PROJECT_PATH + File.separator + "src" + File.separator + "test"
			+ File.separator + "resources" + File.separator;

	public static final String CONFIG_PATH = SRC_RESOURCE_PATH + File.separator + "Config.properties";

	public static final String TESTDATA_PATH = TEST_RESOURCE_PATH + "testData" + File.separator + "TestData.xlsx";

	public static final String REPORTS_PATH = PROJECT_PATH + File.separator + "Reports" + File.separator;

	public static final String SCREENSHOTS_PATH = PROJECT_PATH + File.separator + "Reports" + File.separator
			+ "Screenshots" + File.separator;

	public static final String SAMPLES_PATH = PROJECT_PATH + File.separator + "Samples" + File.separator;

}
