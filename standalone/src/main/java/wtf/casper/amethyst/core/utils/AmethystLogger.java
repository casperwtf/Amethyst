package wtf.casper.amethyst.core.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AmethystLogger {

    @Getter @Setter
    private static boolean debug = false;
    @Getter
    private static Filter filter;
    @Setter
    private static Logger log = Logger.getLogger("Amethyst");

    public static void debug(Object... message) {
        if (!debug) {
            return;
        }

        log(Level.INFO, message);
    }

    public static void log(Level level, Object... message) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String callingClass = stackTrace[2].getClassName();
        int callingLine = stackTrace[2].getLineNumber();

        for (Object o : message) {
            log.log(level, "("+callingClass + ":" + callingLine + ") " + o.toString());
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
