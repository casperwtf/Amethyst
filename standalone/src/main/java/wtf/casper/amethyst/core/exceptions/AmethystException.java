package wtf.casper.amethyst.core.exceptions;

public class AmethystException extends Exception {

    public AmethystException(String message) {
        super(message);
    }

    public AmethystException(String message, Throwable cause) {
        super(message, cause);
    }

    public AmethystException(Throwable cause) {
        super(cause);
    }

    public AmethystException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
