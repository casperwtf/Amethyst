package wtf.casper.amethyst.paper.hooks.combat;

import com.google.auto.service.AutoService;
import wtf.casper.amethyst.core.utils.ServiceUtil;
import wtf.casper.amethyst.paper.hooks.IHookController;
import wtf.casper.amethyst.paper.hooks.SingleHookController;
import wtf.casper.amethyst.paper.utils.ServerUtils;

import java.util.logging.Level;

@AutoService(IHookController.class)
public class CombatController extends SingleHookController<ICombat> {

    @Override
    public void enable() {
        ServiceUtil.getServices(ICombat.class).forEach(this::registerHook);
        ServerUtils.getCallingPlugin().getLogger().log(Level.FINE, "Registered Combat Hook w. " + hook.getClass().getName());
    }

    @Override
    public void disable() {
    }
}
