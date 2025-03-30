import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Optional;

public class Test {

    public static final File parentDir = new File("lang");

    public static void fileReader(File dir) {
        File[] files = dir.listFiles();
        for (File file : files) {
            if(file.isDirectory()) {
                fileReader(file);
            } else {
                try {
                    BufferedReader fileReader = new BufferedReader(new FileReader(file));
                    fileReader.lines().forEach(System.out::println);
                } catch (FileNotFoundException e) {
                    return;
                }
            }
        }
    }

    public static void main(String[] args) {
        fileReader(parentDir);
    }
}
