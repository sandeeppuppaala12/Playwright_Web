package com.automation.web.utils;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Formatter;

public class CommonUtils {

	public static void directorySetup() {
		try {
			File reportsPath = new File(PathDirectory.REPORTS_PATH);
			File screenshotsPath = new File(PathDirectory.SCREENSHOTS_PATH);
			File samplesPath = new File(PathDirectory.SAMPLES_PATH);
			if (!reportsPath.mkdir()) {
				System.out.println("Reports directory exists!!!");
			}
			if (!samplesPath.mkdir()) {
				System.out.println("Samples directory exists!!!");
			}
			if (!screenshotsPath.mkdir()) {
				System.out.println("Screenshots directory exists!!!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getDateTime() {
		LocalDateTime datetime = LocalDateTime.now();
		DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yy_HHmmss");
		return format.format(datetime);
	}

}
