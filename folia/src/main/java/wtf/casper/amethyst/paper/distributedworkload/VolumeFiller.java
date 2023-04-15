package wtf.casper.amethyst.paper.distributedworkload;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.List;

public interface VolumeFiller {

    void fill(Location cornerA, Location cornerB, Material material);

    void fill(Location cornerA, Location cornerB, List<Material> material);

    void replace(Location cornerA, Location cornerB, Material replaced, Material replacer);

    void replace(Location cornerA, Location cornerB, List<Material> replaced, List<Material> replacer);

}
