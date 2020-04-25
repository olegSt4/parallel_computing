import java.io.File;

public class Main {

    public static void main(String[] args) {
        File firstDir = new File("files//test");
        File secondDir = new File("files//train");

        File[] filesTest = firstDir.listFiles();
        File[] fileTrain = secondDir.listFiles();

    }
}
