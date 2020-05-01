package my.work;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * This class describes the work of one thread in terms of multi-thread processing
 */
public class IndexBuilder extends Thread {
    private static final int START = 0;
    private static final int END = 1;

    /** All files*/
    private File[][] parts;

    /** Special boundaries of corresponding parts for this thread*/
    private int[][] bounds;

    /** The result of thread's work will be written here*/
    private HashMap<String, List<String>> blockIndex;

    private int threadId;

    public IndexBuilder(File[][] parts, int[][] bounds, HashMap<String, List<String>> blockIndex, int threadId) {
        this.parts = parts;
        this.bounds = bounds;
        this.blockIndex = blockIndex;
        this.threadId = threadId;
    }

    @Override
    public void run() {
        try {
            System.out.println("Thread " + threadId + " started");
            Scanner scan;

            for (int partNum = 0; partNum < parts.length; partNum++) {
                for (int fileNum = bounds[partNum][START]; fileNum < bounds[partNum][END]; fileNum++) {
                    String fileName = parts[partNum][fileNum].getName().replaceAll(".txt", "");
                    scan = new Scanner(parts[partNum][fileNum]);

                    while (scan.hasNext()) {
                        String input = scan.nextLine();
                        input = input.replaceAll("<br />", " ")
                                .replaceAll("[^A-Za-z\\s]", "")
                                .replaceAll(" +", " ")
                                .trim()
                                .toLowerCase();

                        Queue<String> words = new PriorityQueue<>(Arrays.asList(input.split(" ")));

                        String prev = "";
                        while (words.size() != 0) {
                            String word = words.poll();
                            if (word.compareTo(prev) == 0 || word.length() == 1) {
                                continue;
                            }

                            if (blockIndex.containsKey(word)) {
                                blockIndex.get(word).add(partNum + ":" + fileName);
                            } else {
                                List<String> wordPositions = new LinkedList<>();
                                wordPositions.add(partNum + ":" + fileName);
                                blockIndex.put(word, wordPositions);
                            }
                            prev = word;
                        }
                    }
                }
            }
            System.out.print("Block's InvertedIndex has been successfully built in thread " + threadId);
            System.out.println(" (The size is: " + blockIndex.size() + ")");
        } catch (FileNotFoundException ex) {
            System.out.println("On of the files is not found! (thread " + threadId + ")");
        }
    }
}