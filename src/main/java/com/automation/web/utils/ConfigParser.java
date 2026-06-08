package com.automation.web.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigParser {

	private static final Logger log = LoggerFactory.getLogger(ConfigParser.class);

	private Properties props = new Properties();

	public ConfigParser() {
		try (FileInputStream fis = new FileInputStream(PathDirectory.CONFIG_PATH)) {
			props.load(fis);
		} catch (IOException e) {
			log.error("Unable to load config from {}", PathDirectory.CONFIG_PATH, e);
			throw new UncheckedIOException(e);
		}
	}

	public String getPropertyValue(String propName) {
		return props.getProperty(propName);
	}

	public String getPropertyValue(String propName, String defaultValue) {
		return props.getProperty(propName, defaultValue);
	}

}
