package wtf.casper.amethyst.core.utils;

import redempt.crunch.Crunch;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    public static double randomDouble(double min, double max) {
        if (min == max) {
            return min;
        }
        if (min > max) {
            return ThreadLocalRandom.current().nextDouble(max, min);
        }
        return ThreadLocalRandom.current().nextDouble(min, max);
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
