package wtf.casper.amethyst.core.collections;

import lombok.Getter;

public class ExponentialMovingAverage {
    private final double alpha;
    @Getter private double average;

    public ExponentialMovingAverage(double alpha) {
        this.alpha = alpha;
    }

    public void add(double value) {
        if (Double.isNaN(average)) {
            average = value;
        } else {
            average = alpha * value + (1 - alpha) * average;
        }
    }

    public void reset() {
        average = Double.NaN;
    }
}
