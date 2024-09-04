package wtf.casper.amethyst.paper.utils;

/**
 * Utility class for WorldEdit.
 */
public class WorldEditUtils {

    private WorldEditUtils() {
        throw new UnsupportedOperationException("Cannot instantiate utility class.");
    }

    /**
     * Get the WorldEdit selection of a player.
     * @param player The player to get the selection of.
     * @return The WorldEdit selection of the player, or null if the player has no selection.
     * @throws RuntimeException If WorldEdit is not installed.
     */
//    @Nullable
//    public static Region getSelection(org.bukkit.entity.Player player) {
//
//        if (!Bukkit.getPluginManager().isPluginEnabled("WorldEdit")) {
//            throw new RuntimeException("WorldEdit is not installed! Cannot use WorldEditUtils#getSelection(Player)");
//        }
//
//        Player actor = BukkitAdapter.adapt(player);
//        SessionManager manager = WorldEdit.getInstance().getSessionManager();
//        LocalSession localSession = manager.get(actor);
//
//        Region region;
//
//        World selectionWorld = localSession.getSelectionWorld();
//        try {
//            if (selectionWorld == null) return null;
//            region = localSession.getSelection(selectionWorld);
//        } catch (IncompleteRegionException ex) {
//            return null;
//        }
//
//        return region;
//    }
}
