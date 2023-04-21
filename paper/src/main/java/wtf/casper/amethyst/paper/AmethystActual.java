package wtf.casper.amethyst.paper;

public final class AmethystActual extends AmethystPlugin {

    private final AmethystPaper amethystPaper;

    public AmethystActual() {
        amethystPaper = new AmethystPaper(this, true);
    }

    @Override
    public void enable() {
        amethystPaper.enable();
    }

    @Override
    public void disable() {
        amethystPaper.disableAmethyst();
    }
}
