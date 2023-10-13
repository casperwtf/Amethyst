package wtf.casper.amethyst.paper.utils;

import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import wtf.casper.storageapi.libs.boostedyaml.block.implementation.Section;

public class SoundUtils {

    public static void playSoundWithinRange(Location location, String sound, int range) {
        for (Player nearbyPlayer : location.getNearbyPlayers(range)) {
            nearbyPlayer.playSound(nearbyPlayer.getLocation(), sound, convertForSound((float) nearbyPlayer.getLocation().distance(location), range), 1);
        }
    }

    public static void playSound(Player player, Section section) {
        if (section != null) {
            player.playSound(player.getLocation(), section.getString("id", "minecraft:block.amethyst_block.hit"),
                    SoundCategory.RECORDS,
                    section.getFloat("pitch", 1.0f),
                    section.getFloat("volume", 1.0f)
            );
        }
    }

    public static void playSound(Player player, String name, float pitch, float volume) {
        player.playSound(player.getLocation(), name, SoundCategory.RECORDS, pitch, volume);
    }

    private static float convertForSound(float x, int range) {
        return Math.max(0, 1 - (x / range));
    }
}
