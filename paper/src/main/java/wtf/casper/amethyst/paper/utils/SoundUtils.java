package wtf.casper.amethyst.paper.utils;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public class SoundUtils {

    private SoundUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void playSoundWithinRange(Location location, String sound, int range) {
        for (Player nearbyPlayer : location.getNearbyPlayers(range)) {
            nearbyPlayer.playSound(nearbyPlayer.getLocation(), sound, convertForSound((float) nearbyPlayer.getLocation().distance(location), range), 1);
        }
    }

    public static void playSound(Player player, Section section) {
        if (section == null) {
            return;
        }

        player.playSound(player.getLocation(),
                section.getString("id", "minecraft:block.amethyst_block.hit"),
                SoundCategory.valueOf(section.getString("category", "record").toUpperCase()),
                section.getFloat("pitch", 1.0f),
                section.getFloat("volume", 1.0f)
        );
    }

    private static float convertForSound(float x, int range) {
        return Math.max(0, 1 - (x / range));
    }
}
