package wtf.casper.amethyst.paper.providers;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import wtf.casper.amethyst.paper.scheduler.SchedulerUtil;
import wtf.casper.amethyst.paper.vault.EventEconomyWrapper;
import wtf.casper.amethyst.paper.vault.EventPermissionWrapper;

public class VaultProvider {
    public VaultProvider(JavaPlugin plugin) {
        RegisteredServiceProvider<Economy> registration = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (registration != null) {
            Economy provider = registration.getProvider();
            if (!(provider instanceof EventEconomyWrapper)) {
                plugin.getServer().getServicesManager().register(Economy.class, new EventEconomyWrapper(provider), plugin, ServicePriority.Highest);
            }
        }

        RegisteredServiceProvider<Permission> registration1 = plugin.getServer().getServicesManager().getRegistration(Permission.class);
        if (registration1 != null) {
            Permission permissionProvider = registration1.getProvider();
            if (permissionProvider != null && !(permissionProvider instanceof EventPermissionWrapper)) {
                plugin.getServer().getServicesManager().register(Permission.class, new EventPermissionWrapper(permissionProvider), plugin, ServicePriority.Highest);
            }
        }

        SchedulerUtil.run(() -> {
            RegisteredServiceProvider<Economy> registration2 = plugin.getServer().getServicesManager().getRegistration(Economy.class);
            if (registration2 != null) {
                Economy provider = registration2.getProvider();
                if (!(provider instanceof EventEconomyWrapper)) {
                    plugin.getServer().getServicesManager().register(Economy.class, new EventEconomyWrapper(provider), plugin, ServicePriority.Highest);
                }
            }

            RegisteredServiceProvider<Permission> registration3 = plugin.getServer().getServicesManager().getRegistration(Permission.class);
            if (registration3 != null) {
                Permission permissionProvider = registration3.getProvider();
                if (permissionProvider != null && !(permissionProvider instanceof EventPermissionWrapper)) {
                    plugin.getServer().getServicesManager().register(Permission.class, new EventPermissionWrapper(permissionProvider), plugin, ServicePriority.Highest);
                }
            }
        });
    }
}
