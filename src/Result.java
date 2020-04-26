import java.util.*;

public class Result {
    private Map<String, List<String>> blockIndex;
    private Queue<String> words;
    private int id;

    public Result(int id) {
        this.id = id;
        blockIndex = new HashMap<>();
        words = new PriorityQueue<>();
    }

    public void setBlockIndex(HashMap<String, List<String>> blockIndex) {
        this.blockIndex = blockIndex;
        words.addAll(blockIndex.keySet());
    }

    public int getId() {
        return id;
    }

    public IndexItem getNextItem() {
        if(words.size() == 0) {
            return null;
        }

        String nextWord = words.poll();
        List<String> index = blockIndex.get(nextWord);

        IndexItem newItem = new IndexItem(nextWord, index, id);
        blockIndex.remove(nextWord);

        return newItem;
    }
}
