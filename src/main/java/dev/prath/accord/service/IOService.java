package dev.prath.accord.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import dev.prath.accord.utility.Properties;

public class IOService {
	
	Properties properties = new Properties();

	public String getIniValue() {
		StringBuilder builder = new StringBuilder();
		try {
			Stream<String> fileStream = Files.lines(properties.getIniPath());
			fileStream.forEach(s -> builder.append(s));
			fileStream.close();
		} catch (Exception e) {
			System.err.println(e.toString());
		}
		return builder.toString();
	}

	public void setIniValue(String val) {
		try {
			Files.write(properties.getIniPath(), val.getBytes());
		} catch (IOException e) {
			System.err.println(e.toString());
		}
	}
	
	public void checkIniFolderPath() {
		try {
			Path path = Paths.get(System.getProperty("user.home"), ".accord");
			if(!Files.exists(path)) {
				Files.createDirectory(path);
			}
			if(!Files.exists(properties.getIniPath())) {
				Files.createFile(properties.getIniPath());
			}
		}
		catch(Exception e) {
			System.err.println(e.toString());
		}
	}

}
