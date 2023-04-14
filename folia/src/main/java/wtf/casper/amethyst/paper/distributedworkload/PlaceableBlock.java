package wtf.casper.amethyst.paper.distributedworkload;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import wtf.casper.amethyst.core.distributedworkload.Workload;

import java.util.UUID;

@AllArgsConstructor
public class PlaceableBlock implements Workload {

    private final UUID worldID;
    private final int blockX;
    private final int blockY;
    private final int blockZ;
    private final Material material;

    @Override
    public void compute() {
        World world = Bukkit.getWorld(this.worldID);
        Preconditions.checkState(world != null);
        world.getBlockAt(this.blockX, this.blockY, this.blockZ).setType(this.material, false);
    }

}