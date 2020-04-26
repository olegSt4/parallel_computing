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
    }

    private static void singleThreadProcessing(File[] fSetFirst, File[] fSetSecond) {
        Map<String, List<String>> invertedIndex = new HashMap<>();
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
                            List<String> newList = new LinkedList<>();
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
                            List<String> newList = new LinkedList<>();
                            newList.add("2:" + fileName);
                            invertedIndex.put(word, newList);
                        }
                        prev = word;
                    }
                }
            }
            System.out.println("Inverted index has been successfully built!");
            System.out.println("The size of inverted index is: " + invertedIndex.size());

            scan = new Scanner(System.in);
            System.out.println("Enter the name of the file for saving the index: ");
            String fileName = scan.nextLine();

            FileWriter fw = new FileWriter("files//" + fileName + ".txt");
            for(Map.Entry<String, List<String>> couple : invertedIndex.entrySet()) {
                String line = couple.getKey() + ": ";
                for(String fName : couple.getValue()) {
                    line = line + fName + "  ";
                }
                fw.write(line + "\n");
                fw.flush();
            }
            System.out.println("The inverted index has been successfully written to the file!");

        } catch (FileNotFoundException ex) {
            System.out.println("On of the files is not found!");
        } catch (IOException ex) {
            System.out.println("Something wrong with writing the file!");
            ex.printStackTrace();
        }


    }
}
