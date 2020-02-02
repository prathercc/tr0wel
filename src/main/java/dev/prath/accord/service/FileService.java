package dev.prath.accord.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import dev.prath.accord.utility.Properties;

@Service
public class FileService {

	private static final Logger logger = LoggerFactory.getLogger(FileService.class);

	public FileService() {
		logger.info("FileService has been initialized.");
	}

	public String getIniValue() {
		StringBuilder builder = new StringBuilder();
		try {
			Stream<String> fileStream = Files.lines(Properties.iniPath);
			fileStream.forEach(s -> builder.append(s));
			fileStream.close();
		} catch (Exception e) {
			logger.error("FileService could not read from path: " + Properties.iniPath.toString());
		}
		logger.info("FileService is returning a value from path: " + Properties.iniPath.toString());
		return builder.toString();
	}

	public void setIniValue(String val) {
		try {
			Files.write(Properties.iniPath, val.getBytes());
			logger.info("FileService successfully wrote value: " + val + ", to path: " + Properties.iniPath.toString());
		} catch (IOException e) {
			logger.error("FileService could not write value: " + val + " to path: " + Properties.iniPath.toString());
		}
	}

	public void checkIniFolderPath() {
		try {
			if (!Files.exists(Properties.iniFolderPath)) {
				Files.createDirectory(Properties.iniFolderPath);
				logger.info("FileService has created directory: " + Properties.iniFolderPath.toString());
			}
			if (!Files.exists(Properties.iniPath)) {
				Files.createFile(Properties.iniPath);
				logger.info("FileService has created file: " + Properties.iniPath.toString());
			}
		} catch (Exception e) {
			logger.error("FileService could not verify ini file/directory exist");
		}
	}
}