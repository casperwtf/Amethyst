package wtf.casper.amethyst.paper.utils;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldedit.world.World;
import org.jetbrains.annotations.Nullable;
import wtf.casper.amethyst.paper.AmethystFolia;

public class WorldEditUtils {

    @Nullable
    public static Region getSelection(org.bukkit.entity.Player player) {

        if (!AmethystFolia.getInstance().getServer().getPluginManager().isPluginEnabled("WorldEdit")) {
            throw new RuntimeException("WorldEdit is not installed! Cannot use WorldEditUtils#getSelection(Player)");
        }

        Player actor = BukkitAdapter.adapt(player);
        SessionManager manager = WorldEdit.getInstance().getSessionManager();
        LocalSession localSession = manager.get(actor);

        Region region;

        World selectionWorld = localSession.getSelectionWorld();
        try {
            if (selectionWorld == null) return null;
            region = localSession.getSelection(selectionWorld);
        } catch (IncompleteRegionException ex) {
            return null;
        }

        return region;
    }
}
