package wtf.casper.amethyst.paper;

public final class AmethystActual extends AmethystPlugin {

    private final AmethystPaper amethystPaper;

    public AmethystActual() {
        amethystPaper = new AmethystPaper(this, false);
    }

    @Override
    public void enable() {
        amethystPaper.initAmethyst(this);
    }

    @Override
    public void disable() {
        amethystPaper.disableAmethyst();
    }
}
