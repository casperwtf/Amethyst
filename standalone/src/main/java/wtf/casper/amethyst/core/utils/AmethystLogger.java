package wtf.casper.amethyst.core.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.logging.*;

public class AmethystLogger {

    private static final Logger log = Logger.getLogger(AmethystLogger.class.getName());
    @Getter @Setter
    private static boolean debug = false;

    static {
        //override log formatter
        for (Handler handler : log.getHandlers()) {
            handler.setFormatter(new Formatter() {
                @Override
                public String format(LogRecord record) {
                    return new Date(record.getMillis()) + " " + record.getLevel() + ": " + record.getMessage() + "\n";
                }
            });
        }

        //override log level
        log.setLevel(Level.ALL);
    }

    public static void debug(Object... message) {
        if (!debug) {
            return;
        }

        for (final Object s : message) {
            log.info("[Amethyst Debug] " + s);
        }
    }

    public static void log(Level level, Object... message) {
        for (final Object s : message) {
            log.log(level, s.toString());
        }
    }

    public static void log(Object... message) {
        for (final Object s : message) {
            log.info(s.toString());
        }
    }

    public static void warning(Object... message) {
        for (final Object s : message) {
            log.warning(s.toString());
        }
    }

    public static void severe(Object... message) {
        for (final Object s : message) {
            log.severe(s.toString());
        }
    }

    public static void error(Object... message) {
        severe(message);
    }

    public static Logger getLogger() {
        return log;
    }
}
