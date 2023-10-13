package wtf.casper.amethyst.core.exceptions;

public class NotImplementedException extends RuntimeException {

    public NotImplementedException() {
        super("This feature is not implemented yet.");
    }

    public NotImplementedException(String message) {
        super(message);
    }
}
