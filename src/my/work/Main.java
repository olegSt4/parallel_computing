package my.work;

import my.work.IndexBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {
    private static int START = 0;
    private static int END = 1;

    public static void main(String[] args) {
        File sourceFolder = new File("files");

        if(sourceFolder.listFiles().length == 0) {
            System.out.println("The source folder is empty!");
            return;
        }

        File[][] parts = new File[sourceFolder.listFiles().length][];
        for(int i = 0; i < sourceFolder.listFiles().length; i++) {
            parts[i] = sourceFolder.listFiles()[i].listFiles();
        }

        Scanner scan = new Scanner(System.in);
        System.out.println("Enter the num of threads (from 1 to 100) or 0 to exit: ");

        int threadsNum;
        while(true) {
            String input = scan.next();
            if(input.matches("\\d+")) {
                if(Integer.valueOf(input) >= 0 && Integer.valueOf(input) <= 100) {
                    threadsNum = Integer.valueOf(input);
                    break;
                }
            }
            System.out.println("The input is incorrect! Try one more time.");
            System.out.println("(Enter the num of threads from 1 to 100 or 0 to exit)");
        }

        long startTime = System.currentTimeMillis();

        Map<String, PriorityQueue<String>> finalIndex = null;
        if(threadsNum == 1) {
            finalIndex = singleThreadProcessing(parts);
        } else {
            Thread[] threads = new Thread[threadsNum];
            List<HashMap<String, List<String>>> results = new LinkedList<>();
            for(int i = 0; i < threadsNum; i++) {
                HashMap<String, List<String>> blockIndex = new HashMap<>();
                results.add(blockIndex);

                int[][] bounds = new int[parts.length][2];
                for(int j = 0; j < bounds.length; j++) {
                    bounds[j][START] = parts[j].length/threadsNum*i;
                    bounds[j][END] = i == threadsNum - 1 ? parts[j].length : parts[j].length/threadsNum*(i + 1);
                }

                IndexBuilder newThread = new IndexBuilder(parts, bounds, blockIndex, i);
                threads[i] = newThread;
                newThread.run();
            }

            try {
                for (int i = 0; i < threadsNum; i++) {
                    threads[i].join();
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

            for(HashMap<String, List<String>> blockIndex : results) {
                String[] words = new String[blockIndex.size()];
                blockIndex.keySet().toArray(words);

                for(String word : words) {
                    addItemToIndex(finalIndex, word, blockIndex.get(word));
                    blockIndex.remove(word);
                }
            }
        }
        System.out.println("Final index has been successfully built! (Size is " + finalIndex.size() + ")");
        System.out.println("Time: " + (System.currentTimeMillis() - startTime) + "ms");

        System.out.println("Do you want to save the index to the file?");

        String input = "";
        while (true) {
            System.out.println("(Write \"yes\" or \"no\")");
            input = scan.next();
            if (input.toLowerCase().compareTo("yes") == 0) {
                writeIndexToTheFile(finalIndex, threadsNum);
                break;
            } else if (input.toLowerCase().compareTo("no") == 0) {
                break;
            }
        }
    }

    private static Map<String, PriorityQueue<String>> singleThreadProcessing(File[][] parts) {
        Map<String, PriorityQueue<String>> invertedIndex = new HashMap<>();
        Scanner scan;

        try {
            for(File[] part : parts) {
                for(File file : part) {
                    String fileName = file.getName().replaceAll(".txt", "");
                    scan = new Scanner(file);

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

                            if(invertedIndex.containsKey(word)) {
                                invertedIndex.get(word).add("1:" + fileName);
                            } else {
                                PriorityQueue<String> newList = new PriorityQueue<>();
                                newList.add("1:" + fileName);
                                invertedIndex.put(word, newList);
                            }
                            prev = word;
                        }
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println("On of the files is not found!");
        }
        return invertedIndex;
    }

    private static void addItemToIndex(Map<String, PriorityQueue<String>> index, String key, List<String> value) {
        if(index.containsKey(key)) {
            index.get(key).addAll(value);
        } else {
            PriorityQueue<String> wordPositions = new PriorityQueue<>(value);
            index.put(key, wordPositions);
        }
    }

    private static void writeIndexToTheFile(Map<String, PriorityQueue<String>> invertedIndex, int threadsNum) {
        Calendar cal = new GregorianCalendar();
        String fileName = threadsNum + "thr_";
        fileName += cal.get(Calendar.DAY_OF_MONTH) + "_" + (cal.get(Calendar.MONTH) + 1);
        fileName += "_" + cal.get(Calendar.HOUR_OF_DAY) + "-" + cal.get(Calendar.MINUTE);

        try {
            FileWriter fw = new FileWriter(fileName + ".txt");
            Queue<String> dictionary = new PriorityQueue<>(invertedIndex.keySet());

            while(dictionary.size() != 0) {
                String word = dictionary.poll();
                String line = word + ": ";
                while(invertedIndex.get(word).size() != 0) {
                    line = line + invertedIndex.get(word).poll() + "  ";
                }
                fw.write(line + "\n");
                fw.flush();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println("The inverted index has been successfully written to the file!");
    }
}
