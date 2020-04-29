package my.work;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;

/**
 * This class is used to organize parallel building of the inverted index
 * It this program it processes particular part of files in each directory, that is in source-directory
 */
public class IndexBuilder extends Thread {
    private static final int START = 0;
    private static final int END = 1;

    /** Set of file arrays to be processed*/
    private File[][] parts;
    /** boundaries (beginning and end) of the processing area of each set */
    private int[][] bounds;
    private HashMap<String, List<String>> blockIndex;
    private int threadId;

    public IndexBuilder(File[][] parts, int[][] bounds, HashMap<String, List<String>> blockIndexi, int threadIdi) {
        this.parts = parts;
        this.bounds = bounds;
        this.blockIndex = blockIndexi;
        this.threadId = threadIdi;
    }

    @Override
    public void run() {
        try {
            Scanner scan;

            for (int i = 0; i < parts.length; i++) {
                for (int j = bounds[i][START]; j < bounds[i][END]; j++) {
                    String fileName = parts[i][j].getName().replaceAll(".txt", "");
                    scan = new Scanner(parts[i][j]);

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
                                blockIndex.get(word).add("1:" + fileName);
                            } else {
                                List<String> newList = new LinkedList<>();
                                newList.add("1:" + fileName);
                                blockIndex.put(word, newList);
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
