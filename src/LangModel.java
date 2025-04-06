import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LangModel {

    private final String lang;
    private final Perceptron perceptron;
    private final List<TrainingData> trainingData;

    public LangModel(String lang, Perceptron perceptron) {
        this.lang = lang;
        this.perceptron = perceptron;
        this.trainingData = new ArrayList<>();
    }

    public void learn(int epochs) {
        Collections.shuffle(this.trainingData);
        this.perceptron.learn(this.trainingData, epochs);
    }

    public void addTrainingData(double[] data, int answer) {
        this.trainingData.add(new TrainingData(data, answer));
    }

    public int classify(double[] data) {
        return this.perceptron.classify(data);
    }

    public String getLang() {
        return lang;
    }

    public double getNetValue() {
        return this.perceptron.getNetValue();
    }

    @Override
    public String toString() {
        return "LangModel{" +
                "lang='" + lang + '\'' +
                ", perceptron=" + perceptron +
                ", trainingData=" + trainingData +
                '}';
    }
}
