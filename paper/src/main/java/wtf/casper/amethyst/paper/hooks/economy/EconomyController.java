package wtf.casper.amethyst.paper.hooks.economy;

import com.google.auto.service.AutoService;
import wtf.casper.amethyst.core.utils.ServiceUtil;
import wtf.casper.amethyst.paper.hooks.IHookController;
import wtf.casper.amethyst.paper.hooks.MultiHookController;
import wtf.casper.amethyst.paper.utils.ServerUtils;

import java.util.List;

@AutoService(IHookController.class)
public class EconomyController extends MultiHookController<String, IEconomyType> {

    @Override
    public void enable() {
        List<IEconomyType> economyTypes = ServiceUtil.getServices(IEconomyType.class);
        for (IEconomyType economyType : economyTypes) {
            registerHook(economyType.getName(), economyType);
        }
        ServerUtils.getCallingPlugin().getLogger().info("Registered " + economyTypes.size() + " economy types.");
    }

    @Override
    public void disable() {

    }
}
