package wtf.casper.amethyst.core.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

@Slf4j
public class FakeJavaLogger extends Logger {

    private final Logger javaLogger;

    public FakeJavaLogger(Logger originalLogger) {
        super(originalLogger.getName(), originalLogger.getResourceBundleName());
        this.javaLogger = originalLogger;
    }

    @Override
    public void log(LogRecord record) {
        try {
            log.info(record.getMessage());
        } catch (MissingResourceException e) {
            javaLogger.log(record);
        }
    }

    @Override
    public void log(Level level, String msg) {
        try {
            log.info(msg);
        } catch (MissingResourceException e) {
            javaLogger.log(level, msg);
        }
    }

    @Override
    public void log(Level level, Supplier<String> msgSupplier) {
        try {
            log.info(msgSupplier.get());
        } catch (MissingResourceException e) {
            javaLogger.log(level, msgSupplier);
        }
    }

    @Override
    public void log(Level level, String msg, Object param1) {
        try {
            log.info(msg, param1);
        } catch (MissingResourceException e) {
            javaLogger.log(level, msg, param1);
        }
    }

    @Override
    public void log(Level level, String msg, Object[] params) {
        try {
            log.info(msg, params);
        } catch (MissingResourceException e) {
            javaLogger.log(level, msg, params);
        }
    }

    @Override
    public void log(Level level, String msg, Throwable thrown) {
        try {
            log.info(msg, thrown);
        } catch (MissingResourceException e) {
            javaLogger.log(level, msg, thrown);
        }
    }

    @Override
    public void log(Level level, Throwable thrown, Supplier<String> msgSupplier) {
        try {
            log.info(msgSupplier.get(), thrown);
        } catch (MissingResourceException e) {
            javaLogger.log(level, thrown, msgSupplier);
        }
    }

    @Override
    public void logp(Level level, String sourceClass, String sourceMethod, String msg) {
        try {
            log.info(msg);
        } catch (MissingResourceException e) {
            javaLogger.logp(level, sourceClass, sourceMethod, msg);
        }
    }

    @Override
    public void logp(Level level, String sourceClass, String sourceMethod, Supplier<String> msgSupplier) {
        try {
            log.info(msgSupplier.get());
        } catch (MissingResourceException e) {
            javaLogger.logp(level, sourceClass, sourceMethod, msgSupplier);
        }
    }

    @Override
    public void logp(Level level, String sourceClass, String sourceMethod, String msg, Object param1) {
        try {
            log.info(msg, param1);
        } catch (MissingResourceException e) {
            javaLogger.logp(level, sourceClass, sourceMethod, msg, param1);
        }
    }

    @Override
    public void logp(Level level, String sourceClass, String sourceMethod, String msg, Object[] params) {
        try {
            log.info(msg, params);
        } catch (MissingResourceException e) {
            javaLogger.logp(level, sourceClass, sourceMethod, msg, params);
        }
    }

    @Override
    public void logp(Level level, String sourceClass, String sourceMethod, String msg, Throwable thrown) {
        try {
            log.info(msg, thrown);
        } catch (MissingResourceException e) {
            javaLogger.logp(level, sourceClass, sourceMethod, msg, thrown);
        }
    }

    @Override
    public void logp(Level level, String sourceClass, String sourceMethod, Throwable thrown, Supplier<String> msgSupplier) {
        try {
            log.info(msgSupplier.get(), thrown);
        } catch (MissingResourceException e) {
            javaLogger.logp(level, sourceClass, sourceMethod, thrown, msgSupplier);
        }
    }

    @Override
    public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg) {
        try {
            log.info(msg);
        } catch (MissingResourceException e) {
            javaLogger.logrb(level, sourceClass, sourceMethod, bundleName, msg);
        }
    }

    @Override
    public void logrb(Level level, ResourceBundle bundle, String msg, Object... params) {
        try {
            log.info(msg, params);
        } catch (MissingResourceException e) {
            javaLogger.logrb(level, bundle, msg, params);
        }
    }

    @Override
    public void logrb(Level level, String sourceClass, String sourceMethod, ResourceBundle bundle, String msg, Object... params) {
        try {
            log.info(msg, params);
        } catch (MissingResourceException e) {
            javaLogger.logrb(level, sourceClass, sourceMethod, bundle, msg, params);
        }
    }

    @Override
    public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg, Object... params) {
        try {
            log.info(msg, params);
        } catch (MissingResourceException e) {
            javaLogger.logrb(level, sourceClass, sourceMethod, bundleName, msg, params);
        }
    }

    @Override
    public void logrb(Level level, ResourceBundle bundle, String msg, Throwable thrown) {
        try {
            log.info(msg, thrown);
        } catch (MissingResourceException e) {
            javaLogger.logrb(level, bundle, msg, thrown);
        }
    }

    @Override
    public void logrb(Level level, String sourceClass, String sourceMethod, ResourceBundle bundle, String msg, Throwable thrown) {
        try {
            log.info(msg, thrown);
        } catch (MissingResourceException e) {
            javaLogger.logrb(level, sourceClass, sourceMethod, bundle, msg, thrown);
        }
    }

    @Override
    public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg, Throwable thrown) {
        try {
            log.info(msg, thrown);
        } catch (MissingResourceException e) {
            javaLogger.logrb(level, sourceClass, sourceMethod, bundleName, msg, thrown);
        }
    }

    @Override
    public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg, Object param1) {
        try {
            log.info(msg, param1);
        } catch (MissingResourceException e) {
            javaLogger.logrb(level, sourceClass, sourceMethod, bundleName, msg, param1);
        }
    }

    @Override
    public boolean isLoggable(Level level) {
        try {
            return log.isInfoEnabled();
        } catch (MissingResourceException e) {
            return javaLogger.isLoggable(level);
        }
    }

    @Override
    public Logger getParent() {
        return javaLogger.getParent();
    }
}
