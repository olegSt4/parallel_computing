import java.util.List;

public class IndexItem implements Comparable<IndexItem>{
    private String word;
    private List<String> index;
    private int id;

    public IndexItem(String w, List<String> i, int id) {
        word = w;
        index = i;
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public List<String> getIndex() {
        return index;
    }

    public int getId() {
        return id;
    }

    @Override
    public int compareTo(IndexItem other) {
        return this.word.compareTo(other.getWord());
    }
}
