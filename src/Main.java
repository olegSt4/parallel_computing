import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        File firstDir = new File("files//test");
        File secondDir = new File("files//train");

        File[] fileSetOne = firstDir.listFiles();
        File[] fileSetTwo = secondDir.listFiles();

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

        if(threadsNum == 1) {
            singleThreadProcessing(fileSetOne, fileSetTwo);
            return;
        }

        Thread[] threads = new Thread[threadsNum];
        Result[] results = new Result[threadsNum];
        for(int i = 0; i < threadsNum; i++) {
            Result res = new Result(i);

            int startIndexOne = fileSetOne.length/threadsNum*i;
            int endIndexOne = i == threadsNum - 1 ? fileSetOne.length : fileSetOne.length/threadsNum*(i+1);
            int startIndexTwo = fileSetTwo.length/threadsNum*i;
            int endIndexTwo = i == threadsNum - 1 ? fileSetTwo.length : fileSetTwo.length/threadsNum*(i+1);

            IndexBuilder newThread = new IndexBuilder(fileSetOne, fileSetTwo, startIndexOne, endIndexOne, startIndexTwo, endIndexTwo, res, i);
            threads[i] = newThread;
            results[i] = res;
            newThread.run();
        }

        try {
            for (int i = 0; i < threadsNum; i++) {
                threads[i].join();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        HashMap<String, PriorityQueue<String>> finalIndex = new HashMap<>();
        Queue<IndexItem> addingQueue = new PriorityQueue<>();

        for(int i = 0; i < threadsNum; i++) {
            addingQueue.add(results[i].getNextItem());
        }

        while(addingQueue.size() != 0) {
            int trace = addItemToIndex(finalIndex, addingQueue.poll());

            IndexItem nextItem = results[trace].getNextItem();
            if(nextItem != null) addingQueue.add(nextItem);
        }
        System.out.println("Final index has been successfully built! (Size is " + finalIndex.size() + ")");

        writeIndexToTheFile(finalIndex);
    }

    private static void singleThreadProcessing(File[] fSetFirst, File[] fSetSecond) {
        Map<String, PriorityQueue<String>> invertedIndex = new HashMap<>();
        Scanner scan;

        try {
            for (File file : fSetFirst) {
                String fileName = file.getName().replaceAll(".txt", "");
                scan = new Scanner(file);

                while (scan.hasNext()) {
                    String input = scan.nextLine();
                    input = input.replaceAll("\\d+", "")
                            .replaceAll("<br />", " ")
                            .replaceAll("\"|\\.|,|'|-|!|\\?|;|:|\\(|\\)|=|\\+|[|]|\\{|}|<|>|&|\\*|%|$|#|@|\\|/", "")
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

            for (File file : fSetSecond) {
                String fileName = file.getName().replaceAll(".txt", "");
                scan = new Scanner(file);

                while (scan.hasNext()) {
                    String input = scan.nextLine();
                    input = input.replaceAll("\\d+", "")
                            .replaceAll("<br />", " ")
                            .replaceAll("\"|\\.|,|'|-|!|\\?|;|:|\\(|\\)|=|\\+|[|]|\\{|}|<|>|&|\\*|%|$|#|@|\\|/", "")
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
                            invertedIndex.get(word).add("2:" + fileName);
                        } else {
                            PriorityQueue<String> newList = new PriorityQueue<>();
                            newList.add("2:" + fileName);
                            invertedIndex.put(word, newList);
                        }
                        prev = word;
                    }
                }
            }
            System.out.println("Inverted index has been successfully built!");
            System.out.println("The size of inverted index is: " + invertedIndex.size());

            writeIndexToTheFile(invertedIndex);

        } catch (FileNotFoundException ex) {
            System.out.println("On of the files is not found!");
        } catch (IOException ex) {
            System.out.println("Something wrong with writing the file!");
            ex.printStackTrace();
        }


    }

    private static int addItemToIndex(HashMap<String, PriorityQueue<String>> index, IndexItem item) {
        if(index.containsKey(item.getWord())) {
            index.get(item.getWord()).addAll(item.getIndex());
        } else {
            PriorityQueue<String> newBlockIndex = new PriorityQueue<>(item.getIndex());
            index.put(item.getWord(), newBlockIndex);
        }

        return item.getId();
    }

    private static void writeIndexToTheFile(Map<String, PriorityQueue<String>> invertedIndex) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter the name of the file for saving the index: ");
        String fileName = scan.nextLine();
        try {
            FileWriter fw = new FileWriter("files//" + fileName + ".txt");
            for(Map.Entry<String, PriorityQueue<String>> couple : invertedIndex.entrySet()) {
                String line = couple.getKey() + ": ";
                for(String fName : couple.getValue()) {
                    line = line + fName + "  ";
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
