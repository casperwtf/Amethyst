package wtf.casper.amethyst.core.storage.id.exceptions;

public class IdNotFoundException extends Exception {

    public IdNotFoundException() {
        super("Field or method annotated with @Id not found!");
    }
}
