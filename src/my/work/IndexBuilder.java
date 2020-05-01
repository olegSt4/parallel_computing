package my.work;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class IndexBuilder extends Thread {
    private static final int START = 0;
    private static final int END = 1;

    private File[][] parts;
    private int[][] bounds;
    private HashMap<String, List<String>> blockIndex;
    private int threadId;

    public IndexBuilder(File[][] p, int[][] b, HashMap<String, List<String>> bi, int thi) {
        parts = p;
        bounds = b;
        blockIndex = bi;
        threadId = thi;
    }

    @Override
    public void run() {
        try {
            System.out.println("Thread " + threadId + " started");
            Scanner scan;

            for(int i = 0; i < parts.length; i++) {
                for (int j = bounds[i][START]; j < bounds[i][END]; j++) {
                    String fileName = parts[i][j].getName().replaceAll(".txt", "");
                    scan = new Scanner(parts[i][j]);

                    while (scan.hasNext()) {
                        String input = scan.nextLine();
                        input = input.replaceAll("\\d+", "")
                                .replaceAll("<br />", " ")
                                .replaceAll("[^A-Za-zА-Яа-я0-9\\s]", "")
                                .replaceAll(" +", " ")
                                .trim()
                                .toLowerCase();

                        Queue<String> words = new PriorityQueue<>(Arrays.asList(input.split(" ")));

                        String prev = "";
                        while(words.size() != 0) {
                            String word = words.poll();
                            if(word.compareTo(prev) == 0 || word.length() == 1) {
                                continue;
                            }

                            if(blockIndex.containsKey(word)) {
                                blockIndex.get(word).add(i + ":" + fileName);
                            } else {
                                List<String> newList = new LinkedList<>();
                                newList.add(i + ":" + fileName);
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
