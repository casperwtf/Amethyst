package wtf.casper.amethyst.paper.utils;

import wtf.casper.amethyst.core.utils.Lazy;

public class FoliaUtil {
    private static Lazy<Boolean> FOLIA = new Lazy<>(() -> {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    });

    /**
     * Check if the server is running Folia
     *
     * @return true if the server is running Folia
     */
    public static boolean isFolia() {
        return FOLIA.get();
    }
}
