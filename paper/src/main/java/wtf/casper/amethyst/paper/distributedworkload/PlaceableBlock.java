package wtf.casper.amethyst.paper.distributedworkload;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import wtf.casper.amethyst.core.distributedworkload.Workload;

import java.util.UUID;

public class PlaceableBlock implements Workload {

    private final UUID worldID;
    private final int blockX;
    private final int blockY;
    private final int blockZ;
    private final Material material;
    private final BlockData blockData;

    public PlaceableBlock(UUID worldID, int blockX, int blockY, int blockZ, Material material) {
        this.worldID = worldID;
        this.blockX = blockX;
        this.blockY = blockY;
        this.blockZ = blockZ;
        this.material = material;
        this.blockData = null;
    }

    public PlaceableBlock(UUID worldID, int blockX, int blockY, int blockZ, BlockData blockData) {
        this.worldID = worldID;
        this.blockX = blockX;
        this.blockY = blockY;
        this.blockZ = blockZ;
        this.material = null;
        this.blockData = blockData;
    }

    @Override
    public void compute() {
        World world = Bukkit.getWorld(this.worldID);
        Preconditions.checkState(world != null);
        if (blockData != null) {
            world.getBlockAt(this.blockX, this.blockY, this.blockZ).setBlockData(this.blockData, false);
            return;
        }
        if (material != null) {
            world.getBlockAt(this.blockX, this.blockY, this.blockZ).setType(this.material, false);
        }
    }

}