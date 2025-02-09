package wtf.casper.amethyst.paper.hooks;

public interface IHook {

    boolean canEnable();

    void enable();

    void disable();

    default int priority() {
        return 0;
    }

}
