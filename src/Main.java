import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.stream.Stream;


public class Main {

    public static void main(String[] args) {
        LangDetector detector = new LangDetector("lang_train");
        detector.learn(500);
        detector.test("lang_test").forEach(System.out::println);
        File file = testFileChooser();
        System.out.println("Prediction: " + detector.classifyFile(file.getAbsolutePath()));
        System.out.println("Prediction: " + testTextFromConsole(detector));
        System.out.println("Prediction: " + testFileDirectly(detector));
    }

    public static File testFileChooser() {
        System.out.println("Choose test language:");
        System.out.println("1. de");
        System.out.println("2. en");
        System.out.println("3. es");
        System.out.println("4. fr");
        System.out.println("5. pl - default");

        Scanner scanner = new Scanner(System.in);
        String option = scanner.nextLine();

        String prefix = switch (option) {
            case "1" -> "lang_test/de";
            case "2" -> "lang_test/en";
            case "3" -> "lang_test/es";
            case "4" -> "lang_test/fr";
            default -> "lang_test/pl";
        };

        try(Stream<Path> paths = Files.list(Paths.get(prefix))) {
            File[] files = paths.filter(Files::isRegularFile).map(Path::toFile).toArray(File[]::new);

            System.out.println("Choose a file:");
            printFiles(files);

            option = scanner.nextLine();
            int fileIndex = 0;
            boolean isInputCorrect;
            do {
                try {
                    fileIndex = Integer.parseInt(option);
                    if(fileIndex < 1 || fileIndex > files.length)
                        throw new IllegalArgumentException("Number must be between 1 and " + (files.length));
                    isInputCorrect = true;
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number");
                    printFiles(files);
                    option = scanner.nextLine();
                    isInputCorrect = false;
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                    System.out.println("Please enter a valid number");
                    printFiles(files);
                    option = scanner.nextLine();
                    isInputCorrect = false;
                }
            } while(!isInputCorrect);

            File chosenFile = files[--fileIndex];
            System.out.println("File chosen: " + chosenFile.getName());
            return chosenFile;
        } catch (IOException e) {
            System.err.println("Error while reading test file");
            throw new RuntimeException(e);
        }
    }

    public static void printFiles(File[] files) {
        for(int i = 0; i < files.length; i++)
            System.out.println(i+1 + ". " + files[i].getName());
    }

    public static String testTextFromConsole(LangDetector detector) {
        Scanner scanner = new Scanner(System.in);
        for(;;) {
            try {
                System.out.println("Enter text in one of  the following languages [de, en, es, fr, pl]:]");
                String text = scanner.nextLine();
                return detector.classifyTextDirectly(text);
            } catch (IllegalArgumentException e) {
                System.out.println("Please enter a valid text");
            }
        }
    }

    public static String testFileDirectly(LangDetector detector) {
        Scanner scanner = new Scanner(System.in);
        for(;;) {
            try {
                System.out.println("Enter the path to the file:");
                String input = scanner.nextLine().trim();
                return detector.classifyFile(input);
            } catch (RuntimeException e) {
                System.out.println("File not found! Try again.");
            }
        }
    }
}