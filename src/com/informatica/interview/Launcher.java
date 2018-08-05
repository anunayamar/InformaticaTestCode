package com.informatica.interview;

import java.io.File;
import java.util.Scanner;

/**
 * Launches the application
 * @author Anunay Amar
 *
 */
public class Launcher {
	public static void main(String[] args) {
		Launcher launcher = new Launcher();
		launcher.createOutputFolder();
		String inputFileName = launcher.takeUserInput();
		launcher.launch(inputFileName);
	}

	public void launch(String inputFileName) {
		Object lock = new Object();

		ExecutorServiceWrapper executorServiceWrapper = new ExecutorServiceWrapper(lock);
		BigFileProcessor bigFileProcessor = new BigFileProcessor();
		bigFileProcessor.processFile(inputFileName, executorServiceWrapper, lock);
	}

	/**
	 * takes file name
	 * @return
	 */
	public String takeUserInput() {
		Scanner reader = new Scanner(System.in);
		String inputFileName = "";
		while (true) {
			System.out.println("Enter the file name with complete path: ");
			inputFileName = reader.nextLine();

			File file = new File(inputFileName);
			if (file.exists()) {
				break;
			}
		}
		reader.close();
		return inputFileName;
	}

	/**
	 * creates output folder
	 */
	public void createOutputFolder() {
		File file = new File("output");
		boolean isDirectoryCreated = file.mkdir();

		if (!isDirectoryCreated) {
			deleteDir(file); // Invoke recursive method
			file.mkdir();
		}
	}

	public void deleteDir(File dir) {
		File[] files = dir.listFiles();

		for (File myFile : files) {
			if (myFile.isDirectory()) {
				deleteDir(myFile);
			}
			myFile.delete();

		}
	}
}
