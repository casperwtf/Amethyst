package wtf.casper.amethyst.core.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AmethystLogger {

    @Getter @Setter
    private static boolean debug = false;
    @Getter @Setter
    private static Filter filter;

    public static void debug(Object... message) {
        if (!debug) {
            return;
        }

        for (Object o : message) {
            getLogger(getCallerClass()).info("[Amethyst Debug] " + o.toString());
        }
    }

    public static void log(Level level, Object... message) {
        for (Object o : message) {
            getLogger(getCallerClass()).log(level, o.toString());
        }
    }

    public static void log(Object... message) {
        for (Object o : message) {
            getLogger(getCallerClass()).info(o.toString());
        }
    }

    public static void warning(Object... message) {
        for (Object o : message) {
            getLogger(getCallerClass()).warning(o.toString());
        }
    }

    public static void severe(Object... message) {
        for (Object o : message) {
            getLogger(getCallerClass()).severe(o.toString());
        }
    }

    public static void error(Object... message) {
        severe(message);
    }

    private static Logger getLogger(Class<?> clazz) {
        Logger logger = Logger.getLogger(clazz.getName());
        if (filter != null) {
            logger.setFilter(filter);
        }
        return logger;
    }

    /**
     * index 0 = Thread
     * index 1 = this
     * index 2 = direct caller, can be self.
     * index 3 ... n = classes and methods that called each other to get to the index 2 and below.
     */
    private static Class<?> getCallerClass() {
        Class<?> callerClass = null;
        StackTraceElement[] stackTrace = new Exception().getStackTrace();

        // index 3 is the class that called the method that called this method.
        // if the method that called this method is static, then index 2 is the class that called this method.
        // we want option 3 because this method is called by the static methods in this class which are called by the classes that want to log.
        if (stackTrace.length > 3) {
            try {
                callerClass = Class.forName(stackTrace[3].getClassName());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (callerClass == null) {
            callerClass = AmethystLogger.class;
        }

//        if (stackTrace.length > 5) {
//            for (int i = 0; i < 5; i++) {
//                System.out.println(stackTrace[i].getClassName() + "." + stackTrace[i].getMethodName() + ":" + stackTrace[i].getLineNumber());
//            }
//        }

        return callerClass;
    }

}
