import java.io.File;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        File firstDir = new File("files//test");
        File secondDir = new File("files//train");

        File[] filesTest = firstDir.listFiles();
        File[] fileTrain = secondDir.listFiles();

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
    }
}
