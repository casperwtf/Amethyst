package wtf.casper.amethyst.paper.hooks.vanish;

import com.google.auto.service.AutoService;
import wtf.casper.amethyst.core.utils.ServiceUtil;
import wtf.casper.amethyst.paper.hooks.IHookController;
import wtf.casper.amethyst.paper.hooks.SingleHookController;
import wtf.casper.amethyst.paper.utils.ServerUtils;

@AutoService(IHookController.class)
public class VanishController extends SingleHookController<IVanish> {
    @Override
    public void enable() {
        ServiceUtil.getServices(IVanish.class).forEach(this::registerHook);
        ServerUtils.getCallingPlugin().getLogger().info("Registered " + hooks.size() + " vanish handlers. Using " + hook.getClass().getName() + " as the primary vanish handler.");
    }

    @Override
    public void disable() {

    }
}