package com.informatica.interview;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

/**
 * This produces sorted files
 * @author Anunay Amar
 *
 */
public class Processor implements Runnable{
	private static Integer sortedFileCounter = 0;
	private String content;
	private final Object lock;

	public Processor(String content, Object lock) {
		this.content = content;
		this.lock = lock;
	}

	public void run() {
		process();
		synchronized (lock) {
			lock.notify();
		}
	}

	public void process() {
		Set<String> wordSet = new TreeSet<>();
		String words[] = content.split("\\s+");
		content = null;
		for (String word : words) {
			word = word.replaceAll("[^\\w]", "");
			wordSet.add(word);
		}
		writeSortedChunk(wordSet);
	}

	public void writeSortedChunk(Set<String> sortedWords) {
		File file = null;
		synchronized (sortedFileCounter) {
			file = new File("output/" + sortedFileCounter + "_sortedWords");
			sortedFileCounter++;
		}
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			for (String word : sortedWords) {
				if (word != null && !word.isEmpty()) {
					writer.append(word + "\n");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.append("\n");
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static void main(String arg[]) {
		Processor processor = new Processor("a b d e f g", new Object());
		processor.process();
	}

}
