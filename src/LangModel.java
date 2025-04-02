import java.util.ArrayList;
import java.util.List;

public class LangModel {

    private final String lang;
    private final Perceptron perceptron;
    private final List<double[]> trainingData;
    private final List<Integer> trainingAnswers;

    public LangModel(String lang, Perceptron perceptron) {
        this.lang = lang;
        this.perceptron = perceptron;
        this.trainingData = new ArrayList<>();
        this.trainingAnswers = new ArrayList<>();
    }

    public void learn(int epochs) {
        this.perceptron.learn(this.trainingData, this.trainingAnswers, epochs);
    }

    public void addTrainingData(double[] data, int answer) {
        this.trainingData.add(data);
        this.trainingAnswers.add(answer);
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
                ", trainingAnswers=" + trainingAnswers +
                '}';
    }
}
