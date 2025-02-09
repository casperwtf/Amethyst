package wtf.casper.amethyst.paper.hooks.stacker;

import com.google.auto.service.AutoService;
import lombok.extern.java.Log;
import wtf.casper.amethyst.core.utils.ServiceUtil;
import wtf.casper.amethyst.paper.hooks.IHookController;
import wtf.casper.amethyst.paper.hooks.SingleHookController;
import wtf.casper.amethyst.paper.utils.ServerUtils;

@AutoService(IHookController.class)
@Log
public class StackerController extends SingleHookController<IStacker> {

    @Override
    public void enable() {
        ServiceUtil.getServices(IStacker.class).forEach(this::registerHook);
        ServerUtils.getCallingPlugin().getLogger().fine("Registered " + hooks.size() + " stacker hooks. Using " + hook.getClass().getName() + " as the primary stacker.");
    }

    @Override
    public void disable() {

    }
}
