package wtf.casper.amethyst.paper.hooks.protection;

import com.google.auto.service.AutoService;
import lombok.extern.java.Log;
import wtf.casper.amethyst.core.utils.ServiceUtil;
import wtf.casper.amethyst.paper.hooks.IHookController;
import wtf.casper.amethyst.paper.hooks.SingleHookController;
import wtf.casper.amethyst.paper.utils.ServerUtils;

import java.util.List;

@AutoService(IHookController.class)
@Log
public class ProtectionController extends SingleHookController<IProtection> {

    @Override
    public void enable() {
        List<IProtection> protections = ServiceUtil.getServices(IProtection.class);
        for (IProtection protection : protections) {
            registerHook(protection);
        }
        ServerUtils.getCallingPlugin().getLogger().info("Registered " + protections.size() + " protections. Using " + hook.getClass().getName() + " as the primary protection.");
    }

    @Override
    public void disable() {

    }
}
