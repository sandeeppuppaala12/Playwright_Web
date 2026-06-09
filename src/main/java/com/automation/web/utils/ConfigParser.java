package com.automation.web.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Thread-safe singleton ConfigParser implementation
 * Loads configuration from properties file with support for environment variable override
 */
public class ConfigParser {

	private static final Logger log = LoggerFactory.getLogger(ConfigParser.class);

	private static volatile ConfigParser instance;
	private static final Object lockConfigParser = new Object();
	
	private Properties props = new Properties();

	/**
	 * Private constructor to prevent instantiation
	 */
	private ConfigParser() {
		loadConfiguration();
	}

	/**
	 * Thread-safe singleton getter using double-checked locking
	 * @return ConfigParser singleton instance
	 */
	public static ConfigParser getInstance() {
		if (instance == null) {
			synchronized (lockConfigParser) {
				if (instance == null) {
					instance = new ConfigParser();
				}
			}
		}
		return instance;
	}

	/**
	 * Loads configuration from Config.properties file
	 */
	private void loadConfiguration() {
		String configPath = PathDirectory.CONFIG_PATH;
		
		try {
			// Validate configuration file exists
			if (!Files.exists(Paths.get(configPath))) {
				log.error("Configuration file not found at: {}", configPath);
				throw new IOException("Config.properties not found at: " + configPath);
			}
			
			try (FileInputStream fis = new FileInputStream(configPath)) {
				props.load(fis);
				log.info("Configuration loaded successfully from: {}", configPath);
			}
		} catch (IOException e) {
			log.error("Unable to load config from {}", configPath, e);
			throw new UncheckedIOException("Failed to load configuration: " + configPath, e);
		}
	}

	/**
	 * Gets property value with null check
	 * @param propName Property name to retrieve
	 * @return Property value or null if not found
	 */
	public String getPropertyValue(String propName) {
		String value = props.getProperty(propName);
		if (value == null) {
			log.warn("Property '{}' not found in configuration", propName);
		}
		return value;
	}

	/**
	 * Gets property value with default fallback
	 * @param propName Property name to retrieve
	 * @param defaultValue Default value if property not found
	 * @return Property value or default value
	 */
	public String getPropertyValue(String propName, String defaultValue) {
		String value = props.getProperty(propName, defaultValue);
		if (!value.equals(defaultValue)) {
			log.debug("Using property value for '{}': {}", propName, value);
		} else {
			log.debug("Property '{}' not found, using default value: {}", propName, defaultValue);
		}
		return value;
	}

}
