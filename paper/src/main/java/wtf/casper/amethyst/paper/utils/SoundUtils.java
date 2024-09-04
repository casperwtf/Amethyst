package wtf.casper.amethyst.paper.utils;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public class SoundUtils {

    private SoundUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Play a sound for a player based off of the distance between the player and the location.
     *
     * @param location The location to play the sound at.
     * @param sound    The sound to play.
     * @param range    The range to play the sound within.
     */
    public static void playSoundWithinRange(Location location, String sound, int range) {
        for (Player nearbyPlayer : location.getNearbyPlayers(range)) {
            nearbyPlayer.playSound(nearbyPlayer.getLocation(), sound, convertForSound((float) nearbyPlayer.getLocation().distance(location), range), 1);
        }
    }

    /**
     * Play a sound for a player from configuration section.
     * <p>
     * The section should contain the following
     *     <ul>
     *         <li>id: The sound id to play.</li>
     *         <li>category: The sound category to play the sound in.</li>
     *         <li>pitch: The pitch of the sound.</li>
     *         <li>volume: The volume of the sound.</li>
     *    </ul>
     *    If any of the above keys are missing, default values will be used.
     * </p>
     *
     * @param player  The player to play the sound for.
     * @param section The section containing the sound data.
     */
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
