package com.informatica.interview;

public class Constants {
	/**
	 * To avoid out of memory error, tune MAX_LINE_READ_COUNT
	 * It is the chunk of lines read from the input file. If getting
	 * out of memory error reduce the size of the MAX_LINE_READ_COUNT.
	 */	
	public static final int MAX_LINE_READ_COUNT = 10000;
	/**
	 * This to avoid out of memory error. Its limits the maximum number of lines
	 * processed by all threads combined. To avoid out of memory error keep it small.
	 * Respect the following condition: MAX_MEMORY_LINE_CONDITION > MAX_LINE_READ_COUNT
	 */
	public static final int MAX_MEMORY_LINE_CONDITION = 100000;
	/**
	 * This gives the number of threads that generates the sorted files
	 */
	public static final int MAX_NUMBER_OF_THREADS = 10;	
	/**
	 * This gives the number of threads that merges the sorted files and generates a new sorted file.
	 */
	public static final int MAX_NUMBER_OF_REDUCER_THREADS = 10;	
}
