import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class IndexBuilder extends Thread {
    private File[] fileSetOne;
    private File[] fileSetTwo;
    private int starIndexOne;
    private int endIndexOne;
    private int startIndexTwo;
    private int endIndexTwo;
    private HashMap<String, List<String>> blockIndex;
    private int threadId;

    public IndexBuilder(File[] fso, File[] fst, int sio, int eio, int sit, int eit, HashMap<String, List<String>> bi, int thi) {
        fileSetOne = fso;
        fileSetTwo = fst;
        starIndexOne = sio;
        endIndexOne = eio;
        startIndexTwo = sit;
        endIndexTwo = eit;
        blockIndex = bi;
        threadId = thi;
    }

    @Override
    public void run() {
        try {
            Scanner scan;

            for (int i = starIndexOne; i < endIndexOne; i++) {
                String fileName = fileSetOne[i].getName().replaceAll(".txt", "");
                scan = new Scanner(fileSetOne[i]);

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

            for (int i = startIndexTwo; i < endIndexTwo; i++) {
                String fileName = fileSetTwo[i].getName().replaceAll(".txt", "");
                scan = new Scanner(fileSetTwo[i]);

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
                            blockIndex.get(word).add("2:" + fileName);
                        } else {
                            List<String> newList = new LinkedList<>();
                            newList.add("2:" + fileName);
                            blockIndex.put(word, newList);
                        }
                        prev = word;
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
