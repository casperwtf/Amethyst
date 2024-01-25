package wtf.casper.amethyst.paper.hooks.combat.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.auto.service.AutoService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import wtf.casper.amethyst.paper.AmethystPaper;
import wtf.casper.amethyst.paper.hooks.combat.ICombat;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@AutoService(ICombat.class)
public class ICombatDefault implements ICombat {

    @Override
    public boolean canEnable() {
        return true;
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }

    @Override
    public Optional<Player> getAttacker(Player player) {
        return Optional.empty();
    }
}
