import java.util.List;

public class Perceptron {

    private double theta;
    private double netValue;
    private final double alpha;
    private final double[] weights;

    public Perceptron(int dim, double alpha, double theta) {
        this.alpha = alpha;
        this.weights = new double[dim ];
        for(int i = 0; i < dim; ++i)
            weights[i] = 0;
        this.theta = theta;
    }

    private int compute(double[] input) {
        double sum = 0.0;
        if(input.length != this.weights.length)
            throw new IllegalArgumentException("Input array length does not match weights array length");
        for(int i = 0; i < this.weights.length; ++i)
            sum += input[i] * this.weights[i];
        sum -= this.theta;
        this.netValue = sum;
        return sum >= 0 ? 1 : 0;
    }

    public void learn(List<double[]> input, List<Integer> d, int epochs) {
        for(int epoch = 0; epoch < epochs; ++epoch) {
            for(int i = 0; i < input.size(); ++i) {
                double[] x = input.get(i);
                int y = compute(x);
                int diff = d.get(i) - y;
                for(int j = 0; j < weights.length; ++j)
                    weights[j] += diff * this.alpha * x[j];
                this.theta -= diff * this.alpha;
            }
        }
    }

    public int classify(double[] data) {
        return compute(data);
    }

    public double getNetValue() {
        return netValue;
    }
}
