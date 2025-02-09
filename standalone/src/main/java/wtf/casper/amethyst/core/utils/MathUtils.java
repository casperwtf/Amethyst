package wtf.casper.amethyst.core.utils;

import redempt.crunch.Crunch;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MathUtils {

    /**
     * @param value  The value to round
     * @param places The number of decimal places to round to
     * @return The rounded value
     */
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     * Returns a random integer between min and max, inclusive.
     */
    public static int randomInt(int min, int max) {
        if (min == max) {
            return min;
        }
        if (min > max) {
            return ThreadLocalRandom.current().nextInt(max, min);
        }
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    /**
     * @param mean   The mean of the distribution
     * @param stdDev The standard deviation of the distribution
     * @return A random integer between min and max, inclusive in a gaussian distribution.
     */
    public static int gaussianRandomInt(int mean, int stdDev) {
        return (int) (ThreadLocalRandom.current().nextGaussian() * stdDev + mean);
    }

    public static int gaussianRandomInt(int mean, int stdDev, int min, int max) {
        int value;
        do {
            value = (int) (ThreadLocalRandom.current().nextGaussian() * stdDev + mean);
        } while (value < min || value > max);
        return value;
    }

    /*
     * @param min The minimum value
     * @param max The maximum value
     * @return A random double between min and max, inclusive.
     * */
    public static double randomDouble(double min, double max) {
        if (min == max) {
            return min;
        }
        if (min > max) {
            return ThreadLocalRandom.current().nextDouble(max, min);
        }
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    /*
     * @param min The minimum value
     * @param max The maximum value
     * @return A random float between min and max, inclusive.
     * */
    public static float randomFloat(float min, float max) {
        if (min == max) {
            return min;
        }
        if (min > max) {
            return ThreadLocalRandom.current().nextFloat(max, min);
        }
        return ThreadLocalRandom.current().nextFloat(min, max);
    }

    /**
     * @param mean   The mean of the distribution
     * @param stdDev The standard deviation of the distribution
     * @return A random double between min and max, inclusive in a gaussian distribution.
     */
    public static double gaussianRandomDouble(double mean, double stdDev) {
        return ThreadLocalRandom.current().nextGaussian() * stdDev + mean;
    }

    /**
     * @param mean   The mean of the distribution
     * @param stdDev The standard deviation of the distribution
     * @param min    The minimum value
     * @param max    The maximum value
     * @return A random double between min and max, inclusive in a gaussian distribution.
     */
    public static double gaussianRandomDouble(double mean, double stdDev, double min, double max) {
        double value;
        do {
            value = ThreadLocalRandom.current().nextGaussian() * stdDev + mean;
        } while (value < min || value > max);
        return value;
    }

    /**
     * @param input The input to check
     * @return Whether the input is a valid integer
     */
    public static boolean validateInt(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (final NumberFormatException e) {
            return false;
        }
    }


    /**
     * @param input The input to check
     * @return Whether the input is a valid double
     */
    public static boolean validateLong(String input) {
        try {
            Long.parseLong(input);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * @param input The input to check
     * @return Whether the input is a valid double
     */
    public static boolean validateDouble(String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * @param input The numbers to check.
     * @return The input with the outliers removed.
     */
    public static List<Double> getOutliers(Number[] input) {
        List<Double> output = new ArrayList<>();
        Double[] data1 = new Double[]{};
        Double[] data2 = new Double[]{};

        if (input.length % 2 == 0) {
            for (int i = 0; i < input.length; i++) {
                if (i < input.length / 2) {
                    data1 = Arrays.copyOf(data1, data1.length + 1);
                    data1[data1.length - 1] = input[i].doubleValue();
                } else {
                    data2 = Arrays.copyOf(data2, data2.length + 1);
                    data2[data2.length - 1] = input[i].doubleValue();
                }
            }
        } else {
            for (int i = 0; i < input.length; i++) {
                if (i < input.length / 2 + 1) {
                    data1 = Arrays.copyOf(data1, data1.length + 1);
                    data1[data1.length - 1] = input[i].doubleValue();
                } else if (i > input.length / 2 + 1) {
                    data2 = Arrays.copyOf(data2, data2.length + 1);
                    data2[data2.length - 1] = input[i].doubleValue();
                }
            }
        }
        double q1 = getMedian(data1);
        double q3 = getMedian(data2);

        double iqr = q3 - q1;
        double lowerFence = q1 - 1.5 * iqr;
        double upperFence = q3 + 1.5 * iqr;
        for (Number aDouble : input) {
            double e = aDouble.doubleValue();
            if (e < lowerFence || e > upperFence) {
                output.add(e);
            }
        }
        return output;
    }

    /**
     * @param data The numbers to check.
     * @return The median of the input.
     */
    public static double getMedian(Double[] data) {
        if (data.length % 2 == 0) {
            return (data[data.length / 2] + data[data.length / 2 - 1]) / 2;
        } else {
            return data[data.length / 2];
        }
    }

    /**
     * @param data The numbers to check.
     * @return The median of the input.
     */
    public static int getMedian(Integer[] data) {
        if (data.length % 2 == 0) {
            return (data[data.length / 2] + data[data.length / 2 - 1]) / 2;
        } else {
            return data[data.length / 2];
        }
    }

    /**
     * Linear interpolation
     *
     * @param a The start value
     * @param b The end value
     * @param f The fraction
     */
    public static double lerp(double a, double b, double f) {
        return a + f * (b - a);
    }

    /**
     * <a href="https://www.desmos.com/calculator/cahqdxeshd">Cubic Bézier Curves...</a>
     * Also see <a href="https://en.wikipedia.org/wiki/B%C3%A9zier_curve">Bézier curve</a> and <a href="https://create.roblox.com/docs/mechanics/bezier-curves">the lua version</a>
     *
     * @param t  The time, between 0 and 1
     * @param p0 The start point
     * @param p1 The first control point
     * @param p2 The second control point
     * @param p3 The end point
     */
    public static double bezier(double t, double p0, double p1, double p2, double p3) {
        return Math.pow(1 - t, 3) * p0 + 3 * Math.pow(1 - t, 2) * t * p1 + 3 * (1 - t) * Math.pow(t, 2) * p2 + Math.pow(t, 3) * p3;
    }

    /**
     * @param value The value to clamp
     * @param min   The minimum value
     * @param max   The maximum value
     * @return The value if between min and max, otherwise the closest bound
     */
    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * @param value The value to clamp
     * @param min   The minimum value
     * @param max   The maximum value
     * @return The value if between min and max, otherwise the closest bound
     */
    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * https://github.com/Redempt/Crunch
     *
     * @param input A string with a math equation.
     * @return The result of the equation.
     */
    public static double parseExpression(String input) {
        return Crunch.compileExpression(input).evaluate();
    }
}
