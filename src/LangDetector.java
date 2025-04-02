import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class LangDetector {

    private final TextProcessor textProcessor;

    public LangDetector(String dir) {
        this.textProcessor = new TextProcessor(dir);
        this.textProcessor.processText();
    }

    public void learn(int epochs) {
        Collections.shuffle(this.textProcessor.getLanguages());
        this.textProcessor.getLanguages().forEach(lang -> lang.learn(epochs));
    }

    private record ClassificationResult(LangModel lang, double netValue) { }

    public String classify(double[] letterFrequencies) {
        return textProcessor.getLanguages()
                .stream()
                .map(lang -> {
                    lang.classify(letterFrequencies);
                    double net = lang.getNetValue();
                    return new ClassificationResult(lang, net);
                })
                .max(Comparator.comparingDouble(res -> res.netValue))
                .map(res -> res.lang.getLang())
                .orElse("");
    }

    public String classifyTextDirectly(String text) {
        String letters = TextProcessor.processFileContent(text);
        double[] data = TextProcessor.getLetterDistribution(letters);
        return classify(data);
    }

    public String classifyFile(String file) {
        try(Stream<String> lines = Files.lines(Paths.get(file))) {
            String letters = TextProcessor.processFileContent(lines);
            double[] data = TextProcessor.getLetterDistribution(letters);
            return classify(data);
        } catch (IOException e) {
            System.err.println("Error while reading file " + file);
            throw new RuntimeException(e);
        }
    }

    public static class LangStats {
        int correct;
        int count;
        final String language;

        public LangStats(int correct, int count, String language) {
            this.correct = correct;
            this.count = count;
            this.language = language;
        }
    }

    public List<String> test(String file) {
        File dir = new File(file);
        List<LangStats> stats = new ArrayList<>();
        test(dir, stats, null);
        return stats.stream().map(lang -> {
            double acc = lang.correct / (double) lang.count;
            return "Language: " + lang.language + " accuracy: " + String.format("%.2f%%", 100*acc);
        }).toList();
    }

    public void test(File currFile, List<LangStats> langStats, LangStats currentStats) {
        File[] files = Optional.ofNullable(currFile.listFiles())
                .orElseThrow(() -> new RuntimeException("No files in current directory"));
        for(File file : files) {
            if(file.isDirectory()) {
                LangStats statistics = new LangStats(0, 0, file.getName());
                test(file, langStats, statistics);
                langStats.add(statistics);
            } else {
                if(Objects.isNull(currentStats))
                    return;
                ++currentStats.count;
                String actualLang = currentStats.language;
                String lang = classifyFile(file.getAbsolutePath());
                if(actualLang.equals(lang))
                    ++currentStats.correct;
                System.out.println("Actual language: " + actualLang + ", " + "Predicted language: " + lang);
            }
        }
    }


}
