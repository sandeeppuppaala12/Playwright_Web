package com.automation.web.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigParser {

	FileInputStream fis;
	Properties props;

	public ConfigParser() throws IOException {

		fis = new FileInputStream(PathDirectory.CONFIG_PATH);
		props = new Properties();
		props.load(fis);

	}

	public String getPropertyValue(String propName) {
		return props.getProperty(propName);
	}

}
