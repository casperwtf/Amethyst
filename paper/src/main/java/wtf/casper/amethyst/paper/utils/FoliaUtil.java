package wtf.casper.amethyst.paper.utils;

public class FoliaUtil {
    private static Boolean FOLIA = null;

    /**
     * Check if the server is running Folia
     * @return true if the server is running Folia
     */
    public static boolean isFolia() {
        if (FOLIA != null) return FOLIA;
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            FOLIA = true;
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
