package wtf.casper.amethyst.core.utils;

public class LogUtils {

    public static String getFileAndLine() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length < 2) {
            return "Unknown"; // how?
        }
        StackTraceElement element = stackTrace[1];
        return element.getFileName() + ":" + element.getLineNumber();
    }

    public static String getCaller() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length < 3) {
            return "Unknown"; // how?
        }
        StackTraceElement element = stackTrace[2];
        return element.getClassName() + "." + element.getMethodName();
    }

    public static String getCallerClass() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length < 2) {
            return "Unknown"; // how?
        }
        StackTraceElement element = stackTrace[1];
        return element.getClassName();
    }

    public static String getCallerClassName() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length < 2) {
            return "Unknown"; // how?
        }
        StackTraceElement element = stackTrace[1];
        String[] split = element.getClassName().split("\\.");
        return split[split.length - 1];
    }

}
