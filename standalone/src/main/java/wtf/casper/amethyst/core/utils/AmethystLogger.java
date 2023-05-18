package wtf.casper.amethyst.core.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AmethystLogger {

    @Getter @Setter
    private static boolean debug = false;
    @Getter
    private static Filter filter;

    private static final Logger logger = Logger.getLogger("Amethyst");

    public static void setFilter(Filter filter) {
        AmethystLogger.filter = filter;
        logger.setFilter(filter);
    }

    public static void debug(Object... message) {
        if (!debug) {
            return;
        }

        log(Level.INFO, message);
    }

    public static void log(Level level, Object... message) {
        for (Object o : message) {
            logger.log(level, o.toString());
        }
    }

    public static void log(Object... message) {
        log(Level.INFO, message);
    }

    public static void warning(Object... message) {
        log(Level.WARNING, message);
    }

    public static void severe(Object... message) {
        log(Level.SEVERE, message);
    }

    public static void error(Object... message) {
        log(Level.SEVERE, message);
    }
}
