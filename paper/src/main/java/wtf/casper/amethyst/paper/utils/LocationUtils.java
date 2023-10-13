package wtf.casper.amethyst.paper.utils;

import org.bukkit.Location;

public class LocationUtils {

    public static boolean locationSoftEquals(Location location1, Location location2) {
        return locationSoftEquals(location1, location2, false, false);
    }

    public static boolean locationSoftEquals(Location location1, Location location2, boolean ignoreY) {
        return locationSoftEquals(location1, location2, ignoreY, false);
    }

    public static boolean locationSoftEquals(Location location1, Location location2, boolean ignoreY, boolean useDouble) {
        if (ignoreY) {
            if (useDouble) {
                return location1.getX() == location2.getX() &&
                        location1.getZ() == location2.getZ() &&
                        location1.getWorld().getName().equals(location2.getWorld().getName());
            } else {
                return location1.getBlockX() == location2.getBlockX() &&
                        location1.getBlockZ() == location2.getBlockZ() &&
                        location1.getWorld().getName().equals(location2.getWorld().getName());
            }
        }

        if (useDouble) {
            return location1.getX() == location2.getX() &&
                    location1.getY() == location2.getY() &&
                    location1.getZ() == location2.getZ() &&
                    location1.getWorld().getName().equals(location2.getWorld().getName());
        }

        return location1.getBlockX() == location2.getBlockX() &&
                location1.getBlockY() == location2.getBlockY() &&
                location1.getBlockZ() == location2.getBlockZ() &&
                location1.getWorld().getName().equals(location2.getWorld().getName());
    }

}
