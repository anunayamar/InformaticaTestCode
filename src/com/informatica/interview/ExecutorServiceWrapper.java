package com.informatica.interview;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * It's a thread pool wrapper, submits sorting jobs to thread
 * @author Anunay Amar
 *
 */
public class ExecutorServiceWrapper {
	private ExecutorService executorService;	
	private final Object lock;

	public ExecutorServiceWrapper(Object lock) {
		executorService = Executors.newFixedThreadPool(Constants.MAX_NUMBER_OF_THREADS);
		this.lock = lock;
	}
	
	public ExecutorServiceWrapper() {
		executorService = Executors.newFixedThreadPool(Constants.MAX_NUMBER_OF_REDUCER_THREADS);
		lock = null;
	}
	
	/**
	 * This generates sorted files
	 * @param content
	 */
	public void feedConsumer(String content) {
		Processor processor = new Processor(content, lock);
		executorService.submit(processor);
	}
	
	/**
	 * This merges sorted files
	 * @param fileList
	 */
	public void feedReducers(List<File> fileList) {		
		if (fileList.size() == 1) {
			KWayMergeSort kWayMergeSort = new KWayMergeSort(fileList.get(0), null, null);
			executorService.submit(kWayMergeSort);
		}
		if (fileList.size() == 2) {
			KWayMergeSort kWayMergeSort = new KWayMergeSort(fileList.get(0), fileList.get(1), null);
			executorService.submit(kWayMergeSort);
		}
		if (fileList.size() == 3) {
			KWayMergeSort kWayMergeSort = new KWayMergeSort(fileList.get(0), fileList.get(1),
					fileList.get(2));
			executorService.submit(kWayMergeSort);
		}
	}

	public void stop() {
		executorService.shutdown();
		try {
			executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			System.out.println("Terminated");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
