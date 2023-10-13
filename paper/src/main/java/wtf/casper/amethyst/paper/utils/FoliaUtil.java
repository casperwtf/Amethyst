package wtf.casper.amethyst.paper.utils;

public class FoliaUtil {
    private static final Boolean FOLIA = isFolia();
    private static final Boolean BUKKIT = isBukkit();

    public static boolean isFolia() {
        if (FOLIA != null) return FOLIA;
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isBukkit() {
        if (BUKKIT != null) return BUKKIT;
        try {
            Class.forName("org.bukkit.Bukkit");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
