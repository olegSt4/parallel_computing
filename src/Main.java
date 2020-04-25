import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        File firstDir = new File("files//test");
        File secondDir = new File("files//train");

        File[] fileSetOne = firstDir.listFiles();
        File[] fileSetTwo= secondDir.listFiles();

        Scanner scan = new Scanner(System.in);
        System.out.println("Enter the num of threads (from 1 to 100) or 0 to exit: ");

        int threadsNum = 0;
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
            singleThreadPrcessing(fileSetOne, fileSetTwo);
            return;
        }
    }

    private static void singleThreadPrcessing(File[] fSetFirst, File[] fSetSecond) {
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
                            .replaceAll("\"|\\.|,|'|-|!|\\?|;|:|\\(|\\)|=|\\+|[|]|\\{|}|<|>", "")
                            .replaceAll(" +", " ")
                            .trim()
                            .toLowerCase();

                    Queue<String> words = new PriorityQueue<>(Arrays.asList(input.split(" ")));

                    String prev = "";
                    while(words.size() != 0) {
                        String word = words.poll();
                        if(word.compareTo(prev) == 0) {
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
                            .replaceAll("\"|\\.|,|'|-|!|\\?|;|:|\\(|\\)|=|\\+|[|]|\\{|}|<|>", "")
                            .replaceAll(" +", " ")
                            .trim()
                            .toLowerCase();

                    Queue<String> words = new PriorityQueue<>(Arrays.asList(input.split(" ")));

                    String prev = "";
                    while(words.size() != 0) {
                        String word = words.poll();
                        if(word.compareTo(prev) == 0) {
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

            for(Map.Entry<String, List<String>> couple : invertedIndex.entrySet()) {
                System.out.print(couple.getKey() + " -> ");
                for(String fName : couple.getValue()) {
                    System.out.print(fName + " ");
                }
                System.out.println();
            }

            System.out.println("Inverted index has been successfully built!");
            System.out.println("The size of inverted index is: " + invertedIndex.size());

        } catch (FileNotFoundException ex) {
            System.out.println("On of the files is not found!");
        }

    }
}
