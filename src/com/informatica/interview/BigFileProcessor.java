package com.informatica.interview;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * Creates a sorted file. 
 * @author Anunay Amar
 *
 */
public class BigFileProcessor {

	/**
	 * Reads chunks from the input file and feeds it to the thread to generate multiple sorted files
	 * @param bigFileName
	 * @param executorServiceWrapper
	 * @param lock
	 */
	public void processFile(String bigFileName, ExecutorServiceWrapper executorServiceWrapper, Object lock) {
		FileInputStream inputStream = null;
		Scanner scanner = null;
		//String bigFileName = "input/" + inputFile;

		try {
			inputStream = new FileInputStream(new File(bigFileName));
			scanner = new Scanner(inputStream);

			int lineCounter = 0;
			int memoryLineCounter = 0;
			StringBuilder stringBuilder = new StringBuilder();
			while (scanner.hasNextLine()) {
				stringBuilder.append(scanner.nextLine());
				stringBuilder.append(" ");
				lineCounter++;
				if (lineCounter == Constants.MAX_LINE_READ_COUNT) {
					// Submit
					executorServiceWrapper.feedConsumer(stringBuilder.toString());
					stringBuilder = new StringBuilder();
					lineCounter = 0;
					System.out.println("submiting for sort");
				}
				if (memoryLineCounter == Constants.MAX_MEMORY_LINE_CONDITION) {
					synchronized (lock) {
						try {
							lock.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					memoryLineCounter -= Constants.MAX_LINE_READ_COUNT;
				}
			}
			if (lineCounter != 0) {
				executorServiceWrapper.feedConsumer(stringBuilder.toString());
			}
			executorServiceWrapper.stop();

			// Creating threads of reducers that will merge the sorted files using k-way
			// merge sort
			Set<File> fileSet = new HashSet<>();
			ExecutorServiceWrapper executorServiceWrapperReducers = new ExecutorServiceWrapper();
			sortedFileHelper(executorServiceWrapperReducers, lock, fileSet, new ArrayList<File>());
			executorServiceWrapperReducers.stop();
			renameFinalResult();
			System.exit(0);


		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			scanner.close();
		}
	}

	/**
	 * Feeds sorted files group to thread which performs 3-way and 2-way merge sort
	 * @param executorServiceWrapperReducers
	 * @param lock
	 * @param fileSet
	 * @param fileList
	 */
	public void sortedFileHelper(ExecutorServiceWrapper executorServiceWrapperReducers, Object lock, Set<File> fileSet,
			List<File> fileList) {
		File file = new File("output");
		File files[] = file.listFiles();
		if (files.length == 1) {
			return;
		}

		int counter = 0;
		if (fileList != null) {
			counter = fileList.size();
		} else {
			fileList = new ArrayList<>();
		}
		//System.out.println("*************************");
		for (File outputFile : files) {			
			//System.out.println(outputFile);
			if (!fileSet.contains(outputFile)) {
				fileList.add(outputFile);
				fileSet.add(outputFile);
				counter++;
			}
			
			if (counter == 3) {
				//System.out.println("Feed " + fileList);
				executorServiceWrapperReducers.feedReducers(fileList);
				fileList = new ArrayList<>();
				counter = 0;
			}
		}

		if (fileList.size() > 1) {
			//System.out.println("Feed " + fileList);
			executorServiceWrapperReducers.feedReducers(fileList);
			fileList = null;
		}
		//System.out.println("$$$$$$$$$$$$$$$$$$$$$$");		
		synchronized (lock) {
			try {
				//System.out.println("waiting");
				Thread.sleep(1000);
				//System.out.println("Notified");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			sortedFileHelper(executorServiceWrapperReducers, lock, fileSet, fileList);
		}
	}

	public void renameFinalResult() {
		File file = new File("output");
		File files[] = file.listFiles();

		if (files.length == 1) {
			files[0].renameTo(new File("output//final_result"));
		}
	}

}
