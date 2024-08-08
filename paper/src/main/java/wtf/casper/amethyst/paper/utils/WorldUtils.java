package wtf.casper.amethyst.paper.utils;

import org.bukkit.Location;

public class WorldUtils {

    private WorldUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Check if two locations are equal. Takes Y into account & uses integer values.
     * @param location1 The first location to compare.
     * @param location2 The second location to compare.
     * @return True if the locations are equal, false otherwise.
     */
    public static boolean locationSoftEquals(Location location1, Location location2) {
        return locationSoftEquals(location1, location2, false, false);
    }

    /**
     * Check if two locations are equal. Optionally ignore Y and uses integer values.
     * @param location1 The first location to compare.
     * @param location2 The second location to compare.
     * @param ignoreY Whether or not to ignore the Y value.
     * @return True if the locations are equal, false otherwise.
     */
    public static boolean locationSoftEquals(Location location1, Location location2, boolean ignoreY) {
        return locationSoftEquals(location1, location2, ignoreY, false);
    }

    /**
     * Check if two locations are equal. Optionally ignore Y and use double values.
     * @param location1 The first location to compare.
     * @param location2 The second location to compare.
     * @param ignoreY Whether or not to ignore the Y value.
     * @param useDouble Whether or not to use double values.
     * @return True if the locations are equal, false otherwise.
     */
    public static boolean locationSoftEquals(Location location1, Location location2, boolean ignoreY, boolean useDouble) {
        if (ignoreY) {
            if (useDouble) {
                return location1.getX() == location2.getX() &&
                        location1.getZ() == location2.getZ() &&
                        location1.getWorld().getUID().equals(location2.getWorld().getUID());
            }

            return location1.getBlockX() == location2.getBlockX() &&
                    location1.getBlockZ() == location2.getBlockZ() &&
                    location1.getWorld().getUID().equals(location2.getWorld().getUID());
        }

        if (useDouble) {
            return location1.getX() == location2.getX() &&
                    location1.getY() == location2.getY() &&
                    location1.getZ() == location2.getZ() &&
                    location1.getWorld().getUID().equals(location2.getWorld().getUID());
        }

        return location1.getBlockX() == location2.getBlockX() &&
                location1.getBlockY() == location2.getBlockY() &&
                location1.getBlockZ() == location2.getBlockZ() &&
                location1.getWorld().getUID().equals(location2.getWorld().getUID());
    }

}
