package com.automation.web.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonUtils {

	private static final Logger log = LoggerFactory.getLogger(CommonUtils.class);

	public static void directorySetup() {
		try {
			Path reportsPath = Paths.get(PathDirectory.REPORTS_PATH);
			Path screenshotsPath = Paths.get(PathDirectory.SCREENSHOTS_PATH);
			Path samplesPath = Paths.get(PathDirectory.SAMPLES_PATH);
			Files.createDirectories(reportsPath);
			Files.createDirectories(samplesPath);
			Files.createDirectories(screenshotsPath);
		} catch (Exception e) {
			log.error("Error creating required directories", e);
		}
	}

	public static String getDateTime() {
		LocalDateTime datetime = LocalDateTime.now();
		DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yy_HHmmss");
		return format.format(datetime);
	}

}
