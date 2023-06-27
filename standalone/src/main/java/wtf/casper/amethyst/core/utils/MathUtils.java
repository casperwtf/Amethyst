package wtf.casper.amethyst.core.utils;

import redempt.crunch.Crunch;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MathUtils {

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static int randomInt(int min, int max) {
        if (min == max) {
            return min;
        }
        if (min > max) {
            return ThreadLocalRandom.current().nextInt(max, min);
        }
        return ThreadLocalRandom.current().nextInt(min, max);
    }

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

    public static double randomDouble(double min, double max) {
        if (min == max) {
            return min;
        }
        if (min > max) {
            return ThreadLocalRandom.current().nextDouble(max, min);
        }
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    public static double gaussianRandomDouble(double mean, double stdDev) {
        return ThreadLocalRandom.current().nextGaussian() * stdDev + mean;
    }

    public static double gaussianRandomDouble(double mean, double stdDev, double min, double max) {
        double value;
        do {
            value = ThreadLocalRandom.current().nextGaussian() * stdDev + mean;
        } while (value < min || value > max);
        return value;
    }

    public static boolean validateInt(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (final NumberFormatException e) {
            return false;
        }
    }

    public static boolean validateLong(String args) {
        try {
            Long.parseLong(args);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean validateDouble(String args) {
        try {
            Double.parseDouble(args);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static List<Double> getOutliers(Double[] input) {
        List<Double> output = new ArrayList<>();
        Double[] data1 = new Double[]{};
        Double[] data2 = new Double[]{};

        if (input.length % 2 == 0) {
            for (int i = 0; i < input.length; i++) {
                if (i < input.length / 2) {
                    data1 = Arrays.copyOf(data1, data1.length + 1);
                    data1[data1.length - 1] = input[i];
                } else {
                    data2 = Arrays.copyOf(data2, data2.length + 1);
                    data2[data2.length - 1] = input[i];
                }
            }
        } else {
            for (int i = 0; i < input.length; i++) {
                if (i < input.length / 2 + 1) {
                    data1 = Arrays.copyOf(data1, data1.length + 1);
                    data1[data1.length - 1] = input[i];
                } else if (i > input.length / 2 + 1) {
                    data2 = Arrays.copyOf(data2, data2.length + 1);
                    data2[data2.length - 1] = input[i];
                }
            }
        }
        double q1 = getMedian(data1);
        double q3 = getMedian(data2);

        double iqr = q3 - q1;
        double lowerFence = q1 - 1.5 * iqr;
        double upperFence = q3 + 1.5 * iqr;
        for (Double aDouble : input) {
            if (aDouble < lowerFence || aDouble > upperFence) {
                output.add(aDouble);
            }
        }
        return output;
    }

    public static List<Integer> getOutliers(Integer[] input) {
        List<Integer> output = new ArrayList<>();
        Integer[] data1 = new Integer[]{};
        Integer[] data2 = new Integer[]{};

        if (input.length % 2 == 0) {
            for (int i = 0; i < input.length; i++) {
                if (i < input.length / 2) {
                    data1 = Arrays.copyOf(data1, data1.length + 1);
                    data1[data1.length - 1] = input[i];
                } else {
                    data2 = Arrays.copyOf(data2, data2.length + 1);
                    data2[data2.length - 1] = input[i];
                }
            }
        } else {
            for (int i = 0; i < input.length; i++) {
                if (i < input.length / 2 + 1) {
                    data1 = Arrays.copyOf(data1, data1.length + 1);
                    data1[data1.length - 1] = input[i];
                } else if (i > input.length / 2 + 1) {
                    data2 = Arrays.copyOf(data2, data2.length + 1);
                    data2[data2.length - 1] = input[i];
                }
            }
        }
        double q1 = getMedian(data1);
        double q3 = getMedian(data2);

        double iqr = q3 - q1;
        double lowerFence = q1 - 1.5 * iqr;
        double upperFence = q3 + 1.5 * iqr;
        for (Integer aDouble : input) {
            if (aDouble < lowerFence || aDouble > upperFence) {
                output.add(aDouble);
            }
        }
        return output;
    }

    public static double getMedian(Double[] data) {
        if (data.length % 2 == 0) {
            return (data[data.length / 2] + data[data.length / 2 - 1]) / 2;
        } else {
            return data[data.length / 2];
        }
    }

    public static int getMedian(Integer[] data) {
        if (data.length % 2 == 0) {
            return (data[data.length / 2] + data[data.length / 2 - 1]) / 2;
        } else {
            return data[data.length / 2];
        }
    }

    /**
     * Linear interpolation
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
     * @param t The time, between 0 and 1
     * @param p0 The start point
     * @param p1 The first control point
     * @param p2 The second control point
     * @param p3 The end point
     */
    public static double bezier(double t, double p0, double p1, double p2, double p3) {
        return Math.pow(1 - t, 3) * p0 + 3 * Math.pow(1 - t, 2) * t * p1 + 3 * (1 - t) * Math.pow(t, 2) * p2 + Math.pow(t, 3) * p3;
    }

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public static double parseCrunch(String input) {
        return Crunch.compileExpression(input).evaluate();
    }
}
