import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TextProcessor {

    private final File langParentDir;
    private final List<LangModel> languages;
    private final Map<String, List<String>> allLanguages;

    public TextProcessor(String langParentDir) {
        this.languages = new ArrayList<>();
        this.allLanguages = new HashMap<>();
        this.langParentDir = new File(langParentDir);
    }

    public static double[] getLetterDistribution(String letters) {
        var letterCount = letters.length();
        var distribution = new double[26];
        for (int i = 0; i < letterCount; i++) {
            int index = letters.charAt(i) - 'a';
            ++distribution[index];
        }
        return Arrays.stream(distribution)
                     .map(x -> x / letterCount)
                     .toArray();
    }

    public void processText() {
        processText(this.langParentDir, null);
        for(LangModel lang : this.languages) {
            for(Map.Entry<String, List<String>> entry : this.allLanguages.entrySet()) {
                if(!entry.getKey().equals(lang.getLang())) {
                    for(String letters : entry.getValue()) {
                        var distribution = getLetterDistribution(letters);
                        lang.addTrainingData(distribution, 0);
                    }
                }
            }
        }
    }

    public static String processFileContent(Stream<String> content) {
        return content.map(String::toLowerCase)
                      .map(str -> str.replaceAll("[^a-z]", ""))
                      .collect(Collectors.joining());
    }

    public void processText(File parentDir, LangModel langModel) {
        File[] files = Optional.ofNullable(parentDir.listFiles()).orElse(new File[0]);
        for (File file : files) {
            if (file.isDirectory()) {
                LangModel lang = new LangModel(file.getName(), createPerceptron());
                processText(file, lang);
                this.languages.add(lang);
            } else {
                try(var reader = new BufferedReader(new FileReader(file))) {
                    var letters = processFileContent(reader.lines());
                    langModel.addTrainingData(getLetterDistribution(letters), 1);
                    this.allLanguages.computeIfAbsent(langModel.getLang(), k -> new ArrayList<>()).add(letters);
                } catch (IOException e) {
                    System.err.println("Error reading file " + file.getName());
                    return;
                }
            }
        }
    }

    private Perceptron createPerceptron() {
        return new Perceptron(26, 0.01, 0.6);
    }

    public List<LangModel> getLanguages() {
        return languages;
    }
}
