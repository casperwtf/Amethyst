package wtf.casper.amethyst.paper.serialized;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@Getter
public class SerializedLocation {
    private final double x;
    private final double y;
    private final double z;
    private final double yaw;
    private final double pitch;
    private @Setter String world;
    private transient Location location;

    public SerializedLocation(Location location) {
        this.world = location.getWorld() == null ? null : location.getWorld().getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();

        this.location = location;
    }

    @Override
    public int hashCode() {
        int result = 17;
        return (int) (31 * result + this.x + this.y + this.z);
    }

    public Location toLocation() {
        if (this.location == null) {
            this.location = new Location(Bukkit.getWorld(this.world), x, y, z, (float) yaw, (float) pitch);
        }
        return this.location;
    }

    public boolean softEquals(Location location) {
        if (location == null) return false;

        return (world == null || this.world.equals(location.getWorld().getName())) && toLocation().getBlockX() == location.getBlockX() && toLocation().getBlockY() == location.getBlockY() && toLocation().getBlockZ() == location.getBlockZ();
    }

    public boolean softEquals(SerializedLocation serializedLocation) {
        return softEquals(serializedLocation.toLocation());
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;

        if (!(o instanceof SerializedLocation)) {
            if (o instanceof Location) {
                return softEquals((Location) o); // Allows for limited support to equal Location
            } else {
                return false;
            }
        }
        SerializedLocation location = (SerializedLocation) o;

        return this.x == location.x && this.y == location.y && this.z == location.z;
    }

    @Override
    public String toString() {
        return String.format("world: %s, x: %s, y: %s, z: %s", this.world, this.x, this.y, this.z);
    }
}
