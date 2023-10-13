package wtf.casper.amethyst.paper;

public final class InternalAmethystPlugin extends AmethystPlugin {

    private final AmethystPaper amethystPaper;

    public InternalAmethystPlugin() {
        amethystPaper = new AmethystPaper(this, false);
    }

    @Override
    public void onEnable() {
        amethystPaper.initAmethyst(this);
    }

    @Override
    public void onDisable() {
        amethystPaper.disableAmethyst();
    }
}
