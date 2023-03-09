package wtf.casper.amethyst.paper.hooks.combat.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import wtf.casper.amethyst.paper.AmethystPaper;
import wtf.casper.amethyst.paper.hooks.combat.ICombat;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ICombatDefault implements ICombat, Listener {

    // Hit : Hitter
    private final Cache<UUID, UUID> combatCache = Caffeine.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build();

    @Override
    public Player getAttacker(Player player) {
        UUID present = combatCache.getIfPresent(player.getUniqueId());
        if (present == null) return null;
        return AmethystPaper.getInstance().getServer().getPlayer(present);
    }

    @Override
    public boolean canEnable() {
        return true;
    }

    @Override
    public void enable() {
        AmethystPaper.getInstance().getServer().getPluginManager().registerEvents(this, AmethystPaper.getInstance());
    }

    @Override
    public void disable() {
        this.combatCache.invalidateAll();
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!(event.getDamager() instanceof Player damager)) return;
        this.combatCache.put(player.getUniqueId(), damager.getUniqueId());
    }

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        this.combatCache.invalidate(event.getEntity().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.combatCache.invalidate(event.getPlayer().getUniqueId());
    }
}
