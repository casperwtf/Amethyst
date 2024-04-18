package wtf.casper.amethyst.paper.utils;

import lombok.Getter;
import org.bukkit.Bukkit;

@Getter
public class ServerVersion {

    static {
        setVersion();
    }

    public static ServerVersion CURRENT = null;
    public static final ServerVersion v1_0 = new ServerVersion(1, 0, 0);
    public static final ServerVersion v1_0_1 = new ServerVersion(1, 0, 1);
    public static final ServerVersion v1_1 = new ServerVersion(1, 1, 0);
    public static final ServerVersion v1_2_0 = new ServerVersion(1, 2, 0);
    public static final ServerVersion v1_2_1 = new ServerVersion(1, 2, 1);
    public static final ServerVersion v1_2_2 = new ServerVersion(1, 2, 2);
    public static final ServerVersion v1_2_3 = new ServerVersion(1, 2, 3);
    public static final ServerVersion v1_2_4 = new ServerVersion(1, 2, 4);
    public static final ServerVersion v1_2_5 = new ServerVersion(1, 2, 5);
    public static final ServerVersion v1_3_0 = new ServerVersion(1, 3, 0);
    public static final ServerVersion v1_3_1 = new ServerVersion(1, 3, 1);
    public static final ServerVersion v1_3_2 = new ServerVersion(1, 3, 2);
    public static final ServerVersion v1_4_0 = new ServerVersion(1, 4, 0);
    public static final ServerVersion v1_4_1 = new ServerVersion(1, 4, 1);
    public static final ServerVersion v1_4_2 = new ServerVersion(1, 4, 2);
    public static final ServerVersion v1_4_3 = new ServerVersion(1, 4, 3);
    public static final ServerVersion v1_4_4 = new ServerVersion(1, 4, 4);
    public static final ServerVersion v1_4_5 = new ServerVersion(1, 4, 5);
    public static final ServerVersion v1_4_6 = new ServerVersion(1, 4, 6);
    public static final ServerVersion v1_4_7 = new ServerVersion(1, 4, 7);
    public static final ServerVersion v1_5_0 = new ServerVersion(1, 5, 0);
    public static final ServerVersion v1_5_1 = new ServerVersion(1, 5, 1);
    public static final ServerVersion v1_5_2 = new ServerVersion(1, 5, 2);
    public static final ServerVersion v1_6_0 = new ServerVersion(1, 6, 0);
    public static final ServerVersion v1_6_1 = new ServerVersion(1, 6, 1);
    public static final ServerVersion v1_6_2 = new ServerVersion(1, 6, 2);
    public static final ServerVersion v1_6_3 = new ServerVersion(1, 6, 3);
    public static final ServerVersion v1_6_4 = new ServerVersion(1, 6, 4);
    public static final ServerVersion v1_7_0 = new ServerVersion(1, 7, 0);
    public static final ServerVersion v1_7_1 = new ServerVersion(1, 7, 1);
    public static final ServerVersion v1_7_2 = new ServerVersion(1, 7, 2);
    public static final ServerVersion v1_7_3 = new ServerVersion(1, 7, 3);
    public static final ServerVersion v1_7_4 = new ServerVersion(1, 7, 4);
    public static final ServerVersion v1_7_5 = new ServerVersion(1, 7, 5);
    public static final ServerVersion v1_7_6 = new ServerVersion(1, 7, 6);
    public static final ServerVersion v1_7_7 = new ServerVersion(1, 7, 7);
    public static final ServerVersion v1_7_8 = new ServerVersion(1, 7, 8);
    public static final ServerVersion v1_7_9 = new ServerVersion(1, 7, 9);
    public static final ServerVersion v1_7_10 = new ServerVersion(1, 7, 10);
    public static final ServerVersion v1_8_0 = new ServerVersion(1, 8, 0);
    public static final ServerVersion v1_8_1 = new ServerVersion(1, 8, 1);
    public static final ServerVersion v1_8_2 = new ServerVersion(1, 8, 2);
    public static final ServerVersion v1_8_3 = new ServerVersion(1, 8, 3);
    public static final ServerVersion v1_8_4 = new ServerVersion(1, 8, 4);
    public static final ServerVersion v1_8_5 = new ServerVersion(1, 8, 5);
    public static final ServerVersion v1_8_6 = new ServerVersion(1, 8, 6);
    public static final ServerVersion v1_8_7 = new ServerVersion(1, 8, 7);
    public static final ServerVersion v1_8_8 = new ServerVersion(1, 8, 8);
    public static final ServerVersion v1_8_9 = new ServerVersion(1, 8, 9);
    public static final ServerVersion v1_9_0 = new ServerVersion(1, 9, 0);
    public static final ServerVersion v1_9_1 = new ServerVersion(1, 9, 1);
    public static final ServerVersion v1_9_2 = new ServerVersion(1, 9, 2);
    public static final ServerVersion v1_9_3 = new ServerVersion(1, 9, 3);
    public static final ServerVersion v1_9_4 = new ServerVersion(1, 9, 4);
    public static final ServerVersion v1_10_0 = new ServerVersion(1, 10, 0);
    public static final ServerVersion v1_10_1 = new ServerVersion(1, 10, 1);
    public static final ServerVersion v1_10_2 = new ServerVersion(1, 10, 2);
    public static final ServerVersion v1_11_0 = new ServerVersion(1, 11, 0);
    public static final ServerVersion v1_11_1 = new ServerVersion(1, 11, 1);
    public static final ServerVersion v1_11_2 = new ServerVersion(1, 11, 2);
    public static final ServerVersion v1_12_0 = new ServerVersion(1, 12, 0);
    public static final ServerVersion v1_12_1 = new ServerVersion(1, 12, 1);
    public static final ServerVersion v1_12_2 = new ServerVersion(1, 12, 2);
    public static final ServerVersion v1_13_0 = new ServerVersion(1, 13, 0);
    public static final ServerVersion v1_13_1 = new ServerVersion(1, 13, 1);
    public static final ServerVersion v1_13_2 = new ServerVersion(1, 13, 2);
    public static final ServerVersion v1_14_0 = new ServerVersion(1, 14, 0);
    public static final ServerVersion v1_14_1 = new ServerVersion(1, 14, 1);
    public static final ServerVersion v1_14_2 = new ServerVersion(1, 14, 2);
    public static final ServerVersion v1_14_3 = new ServerVersion(1, 14, 3);
    public static final ServerVersion v1_14_4 = new ServerVersion(1, 14, 4);
    public static final ServerVersion v1_15_0 = new ServerVersion(1, 15, 0);
    public static final ServerVersion v1_15_1 = new ServerVersion(1, 15, 1);
    public static final ServerVersion v1_15_2 = new ServerVersion(1, 15, 2);
    public static final ServerVersion v1_16_0 = new ServerVersion(1, 16, 0);
    public static final ServerVersion v1_16_1 = new ServerVersion(1, 16, 1);
    public static final ServerVersion v1_16_2 = new ServerVersion(1, 16, 2);
    public static final ServerVersion v1_16_3 = new ServerVersion(1, 16, 3);
    public static final ServerVersion v1_16_4 = new ServerVersion(1, 16, 4);
    public static final ServerVersion v1_16_5 = new ServerVersion(1, 16, 5);
    public static final ServerVersion v1_17_0 = new ServerVersion(1, 17, 0);
    public static final ServerVersion v1_17_1 = new ServerVersion(1, 17, 1);
    public static final ServerVersion v1_18_0 = new ServerVersion(1, 18, 0);
    public static final ServerVersion v1_18_1 = new ServerVersion(1, 18, 1);
    public static final ServerVersion v1_18_2 = new ServerVersion(1, 18, 2);
    public static final ServerVersion v1_19_0 = new ServerVersion(1, 19, 0);
    public static final ServerVersion v1_19_1 = new ServerVersion(1, 19, 1);
    public static final ServerVersion v1_19_2 = new ServerVersion(1, 19, 2);
    public static final ServerVersion v1_19_3 = new ServerVersion(1, 19, 3);
    public static final ServerVersion v1_19_4 = new ServerVersion(1, 19, 4);
    public static final ServerVersion v1_20_0 = new ServerVersion(1, 20, 0);
    public static final ServerVersion v1_20_1 = new ServerVersion(1, 20, 1);
    public static final ServerVersion v1_20_2 = new ServerVersion(1, 20, 2);
    public static final ServerVersion v1_20_3 = new ServerVersion(1, 20, 3);
    public static final ServerVersion v1_20_4 = new ServerVersion(1, 20, 4);
    public static final ServerVersion v1_20_5 = new ServerVersion(1, 20, 5);

    private final int major;
    private final int minor;
    private final int patch;

    public ServerVersion(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public boolean isAtLeast(int major, int minor, int patch) {
        return this.major > major || (this.major == major && (this.minor > minor || (this.minor == minor && this.patch >= patch)));
    }

    public boolean isAtLeast(int major, int minor) {
        return isAtLeast(major, minor, 0);
    }

    public boolean isAtLeast(int major) {
        return isAtLeast(major, 0, 0);
    }

    public boolean isAtLeast() {
        return isAtLeast(CURRENT.major, CURRENT.minor, CURRENT.patch);
    }

    public boolean isBefore(int major, int minor, int patch) {
        return this.major < major || (this.major == major && (this.minor < minor || (this.minor == minor && this.patch < patch)));
    }

    public boolean isBefore(int major, int minor) {
        return isBefore(major, minor, 0);
    }

    public boolean isBefore(int major) {
        return isBefore(major, 0, 0);
    }

    public boolean isBefore() {
        return isBefore(CURRENT.major, CURRENT.minor, CURRENT.patch);
    }

    public boolean is(int major, int minor, int patch) {
        return this.major == major && this.minor == minor && this.patch == patch;
    }

    public boolean is(int major, int minor) {
        return is(major, minor, 0);
    }

    public boolean is(int major) {
        return is(major, 0, 0);
    }

    public boolean is() {
        return is(CURRENT.major, CURRENT.minor, CURRENT.patch);
    }

    private static void setVersion() {
        try {
            String version = Bukkit.getServer().getMinecraftVersion(); // 1.20.1
            String[] split = version.split("\\.");
            CURRENT = new ServerVersion(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
            return;
        } catch (Exception ignored) {}

        try {
            String version = Bukkit.getServer().getBukkitVersion(); // 1.20.1-R0.1-SNAPSHOT
            String[] split = version.split("\\.");

            String[] split2 = split[0].split("-");
            String[] split3 = split2[0].split("\\.");
            CURRENT = new ServerVersion(Integer.parseInt(split3[0]), Integer.parseInt(split3[1]), Integer.parseInt(split3[2]));
            return;
        } catch (Exception ignored) {}
    }
}
