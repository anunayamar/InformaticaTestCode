package com.informatica.interview;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Performs K-way merge sort. Each thread performs K-way merge sort
 * @author eanuama
 *
 */
public class KWayMergeSort implements Runnable {
	private static Integer sortedFileCounter = 0;

	private File unsortedFile1;
	private File unsortedFile2;
	private File unsortedFile3;

	public KWayMergeSort(File file1, File file2, File file3) {
		this.unsortedFile1 = file1;
		this.unsortedFile2 = file2;
		this.unsortedFile3 = file3;
	}

	public void run() {
		System.out.println("Merging sorted files using k-way merge sort:" + Thread.currentThread().getName());
		sort(unsortedFile1, unsortedFile2, unsortedFile3);
	}

	public void sort(File file1, File file2, File file3) {
		if (file1 == null && file2 == null && file3 == null) {
			return;
		}
		if (file1 == null && file2 == null) {
			// you have a single sorted file
			return;
		}
		if (file2 == null && file3 == null) {
			// you have a single sorted file
			return;
		}
		if (file3 == null && file1 == null) {
			// you have a single sorted file
			return;
		}

		if (file1 == null) {
			// do two way merge sort
			twoWayMergeSort(file1, file2);
			return;
		}
		if (file2 == null) {
			// do two way merge sort
			twoWayMergeSort(file1, file3);
			return;
		}
		if (file3 == null) {
			// do two way merge sort
			twoWayMergeSort(file1, file2);
			return;
		}
		// do three way merge sort
		threeWayMergeSort(file1, file2, file3);

	}

	/**
	 * When thread has 3 files, it performs 3-way merge sort 
	 * @param inputFile1
	 * @param inputFile2
	 * @param inputFile3
	 */
	public void threeWayMergeSort(File inputFile1, File inputFile2, File inputFile3) {
		FileInputStream inputStream1 = null;
		Scanner scanner1 = null;
		FileInputStream inputStream2 = null;
		Scanner scanner2 = null;
		FileInputStream inputStream3 = null;
		Scanner scanner3 = null;

		File sortedOutputFile = null;
		synchronized (sortedFileCounter) {
			sortedOutputFile = new File("output/" + sortedFileCounter + "_output");
			sortedFileCounter++;
		}
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(sortedOutputFile));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			inputStream1 = new FileInputStream(inputFile1);
			scanner1 = new Scanner(inputStream1);

			inputStream2 = new FileInputStream(inputFile2);
			scanner2 = new Scanner(inputStream2);

			inputStream3 = new FileInputStream(inputFile3);
			scanner3 = new Scanner(inputStream3);

			if (isTwoInputFilesEmpty(scanner1, scanner2)) {
				// dump scanner3 in the output file
				copySortedItemsOutputFile(writer, scanner3);
				// delete file1, file2, file3
				// deleteInputFiles(inputFile1, inputFile2, inputFile3);
				return;
			}

			if (isTwoInputFilesEmpty(scanner1, scanner3)) {
				// dump scanner 2 in the output file
				copySortedItemsOutputFile(writer, scanner2);
				// delete file1, file2, file3
				// deleteInputFiles(inputFile1, inputFile2, inputFile3);
				return;
			}

			if (isTwoInputFilesEmpty(scanner2, scanner3)) {
				// dump scanner 1 in the output file
				copySortedItemsOutputFile(writer, scanner1);
				// delete file1, file2, file3
				// deleteInputFiles(inputFile1, inputFile2, inputFile3);
				return;
			}

			String first = null;
			String second = null;
			String third = null;
			String outputString = null;
			String previousString = null;
			first = getNextString(scanner1);
			second = getNextString(scanner2);
			third = getNextString(scanner3);

			while (scanner1.hasNextLine() && scanner2.hasNextLine() && scanner3.hasNextLine()) {
				if (isSmallerThanOther(first, second, third)) {
					outputString = first;
					first = getNextString(scanner1);
				} else if (isSmallerThanOther(second, first, third)) {
					outputString = second;
					second = getNextString(scanner2);
				} else if (isSmallerThanOther(third, first, second)) {
					outputString = third;
					third = getNextString(scanner3);
				}

				if (previousString == null || !outputString.equals(previousString)) {
					// dump output to output file
					copySortedSetOutputFile(writer, outputString);
					previousString = outputString;
				}
			}

			while (scanner1.hasNextLine() && scanner2.hasNextLine()) {
				if (isSmallerThanOther(first, second, null)) {
					outputString = first;
					first = getNextString(scanner1);
				} else if (isSmallerThanOther(second, first, null)) {
					outputString = second;
					second = getNextString(scanner2);
				}
				if (previousString == null || !outputString.equals(previousString)) {
					// dump output to output file
					copySortedSetOutputFile(writer, outputString);
					previousString = outputString;
				}
			}

			while (scanner1.hasNextLine() && scanner3.hasNextLine()) {
				if (isSmallerThanOther(first, null, third)) {
					outputString = first;
					first = getNextString(scanner1);
				} else if (isSmallerThanOther(third, first, null)) {
					outputString = third;
					third = getNextString(scanner3);
				}

				if (previousString == null || !outputString.equals(previousString)) {
					// dump output to output file
					copySortedSetOutputFile(writer, outputString);
					previousString = outputString;
				}
			}

			while (scanner2.hasNextLine() && scanner3.hasNextLine()) {
				if (isSmallerThanOther(second, null, third)) {
					outputString = second;
					second = getNextString(scanner2);
				} else if (isSmallerThanOther(third, null, second)) {
					outputString = third;
					third = getNextString(scanner3);
				}

				if (previousString == null || !outputString.equals(previousString)) {
					// dump output to output file
					copySortedSetOutputFile(writer, outputString);
					previousString = outputString;
				}
			}

			if (first != null ) {
				//copy
				copyUncopiedString(first, previousString, writer);
			}else if(second != null) {
				copyUncopiedString(second, previousString, writer);
			}else if (third != null) {
				copyUncopiedString(third, previousString, writer);
			}

			if (isFileEmpty(scanner1) && isFileEmpty(scanner2) && isFileEmpty(scanner3)) {
				return;
			}

			if (isFileEmpty(scanner1) && isFileEmpty(scanner2)) {
				// dump content of scanner3 in the output file
				copySortedItemsOutputFile(writer, scanner3);
				return;
			}
			if (isFileEmpty(scanner1) && isFileEmpty(scanner3)) {
				// dump content of scanner2 in the output file
				copySortedItemsOutputFile(writer, scanner2);
				return;
			}
			if (isFileEmpty(scanner2) && isFileEmpty(scanner3)) {
				// dump content of scanner1 in the output file
				copySortedItemsOutputFile(writer, scanner1);
				return;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("--------------------");
			System.exit(0);
		} finally {
			scanner1.close();
			scanner2.close();
			scanner3.close();
			try {
				writer.write("\n");
				writer.close();
				inputStream1.close();
				inputStream2.close();
				inputStream3.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			deleteInputFiles(inputFile1, inputFile2, inputFile3);
		}

	}

	private void copyUncopiedString(String outputString, String previousString, BufferedWriter writer) {
		if(!outputString.equals(previousString)) {
			copySortedSetOutputFile(writer, outputString);
		}
	}

	/**
	 * When thread has 2 files it performs 2-way merge sort
	 * @param inputFile1
	 * @param inputFile2
	 */
	public void twoWayMergeSort(File inputFile1, File inputFile2) {
		FileInputStream inputStream1 = null;
		Scanner scanner1 = null;
		FileInputStream inputStream2 = null;
		Scanner scanner2 = null;

		File sortedOutputFile = null;
		synchronized (sortedFileCounter) {
			sortedOutputFile = new File("output/" + sortedFileCounter + "_output");
			sortedFileCounter++;
		}
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(sortedOutputFile));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			inputStream1 = new FileInputStream(inputFile1);
			scanner1 = new Scanner(inputStream1);

			inputStream2 = new FileInputStream(inputFile2);
			scanner2 = new Scanner(inputStream2);

			if (isTwoInputFilesEmpty(scanner1, scanner2)) {
				// delete file1, file2, file3
				deleteInputFiles(inputFile1, inputFile2);
				return;
			}

			String first = null;
			String second = null;
			String outputString = null;
			String previousString = null;
			first = getNextString(scanner1);
			second = getNextString(scanner2);

			while (scanner1.hasNextLine() && scanner2.hasNextLine()) {
				if (first.compareTo(second) <= 0) {
					outputString = first;
					first = getNextString(scanner1);
				} else {
					outputString = second;
					second = getNextString(scanner2);
				}
				if (previousString == null || !outputString.equals(previousString)) {
					// dump output to output file
					copySortedSetOutputFile(writer, outputString);
					previousString = outputString;
				}
			}
			
			if(first!=null) {
				copyUncopiedString(first, previousString, writer);
			}else if (second!=null) {
				copyUncopiedString(second, previousString, writer);
			}

			if (isFileEmpty(scanner1) && isFileEmpty(scanner2)) {
				return;
			}
			if (isFileEmpty(scanner1)) {

				// dump content of scanner2 in the output file
				copySortedItemsOutputFile(writer, scanner2);
				// deleteInputFiles(inputFile1, inputFile2);
				return;
			}
			if (isFileEmpty(scanner2)) {

				// dump content of scanner1 in the output file
				copySortedItemsOutputFile(writer, scanner1);
				// deleteInputFiles(inputFile1, inputFile2);
				return;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("--------------------");
			// System.exit(0);
		} finally {
			scanner1.close();
			scanner2.close();
			try {
				writer.write("\n");
				writer.close();
				inputStream1.close();
				inputStream2.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// deletes already processed sorted files
			deleteInputFiles(inputFile1, inputFile2);
		}

	}

	public void deleteInputFiles(File... inputFiles) {
		for (File inputFile : inputFiles) {
			inputFile.delete();
		}
	}

	public boolean isSmallerThanOther(String first, String second, String third) {
		if (first == null) {
			return false;
		}
		if (second == null) {
			if (first.compareTo(third) <= 0) {
				return true;
			}
			return false;
		}
		if (third == null) {
			if (first.compareTo(second) <= 0) {
				return true;
			}
			return false;
		}
		if (second != null && third != null) {
			if (first.compareTo(second) <= 0 && first.compareTo(third) <= 0) {
				return true;
			}
		}
		return false;
	}

	public String getNextString(Scanner scanner) {
		if (scanner.hasNextLine()) {
			String buffer = scanner.nextLine();
			if (buffer.isEmpty()) {
				return null;
			}
			return buffer;
		}
		return null;
	}

	public boolean isTwoInputFilesEmpty(Scanner scanner1, Scanner scanner2) {
		if (!scanner1.hasNextLine() && !scanner2.hasNextLine()) {
			return true;
		}
		return false;
	}

	public boolean isFileEmpty(Scanner scanner) {
		return !scanner.hasNextLine();
	}

	public void copySortedItemsOutputFile(BufferedWriter writer, Scanner scanner) {
		try {
			while (scanner.hasNextLine()) {
				String sortedWord = scanner.nextLine();
				if (!sortedWord.isEmpty()) {
					writer.append(sortedWord + "\n");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void copySortedSetOutputFile(BufferedWriter writer, String outputString) {
		try {
			writer.append(outputString + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
