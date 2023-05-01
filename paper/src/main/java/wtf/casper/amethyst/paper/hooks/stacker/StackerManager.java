package wtf.casper.amethyst.paper.hooks.stacker;

import lombok.Getter;
import wtf.casper.amethyst.core.utils.AmethystLogger;
import wtf.casper.amethyst.paper.hooks.stacker.impl.DefaultStacker;
import wtf.casper.amethyst.paper.hooks.stacker.impl.RoseStackStacker;

import java.util.List;

public class StackerManager {
    @Getter
    private static IStacker stacker;

    public StackerManager() {
        List<IStacker> stackers = List.of(
                new RoseStackStacker()
        );
        for (IStacker iStacker : stackers) {
            if (iStacker.canEnable()) {
                iStacker.enable();
                stacker = iStacker;
                AmethystLogger.log("Stacker hook enabled: " + iStacker.getClass().getSimpleName());
                break;
            }
        }

        if (stacker == null) {
            stacker = new DefaultStacker();
            AmethystLogger.log("Stacker hook enabled: " + stacker.getClass().getSimpleName());
        }
    }
}
