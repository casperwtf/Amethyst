package wtf.casper.amethyst.paper.distributedworkload;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import wtf.casper.amethyst.core.distributedworkload.Workload;

import java.util.UUID;
import java.util.function.Consumer;

@AllArgsConstructor
public class BlockScanWorkload implements Workload {

    private final UUID worldID;
    private final int blockX;
    private final int blockY;
    private final int blockZ;
    private final Consumer<Block> action;

    @Override
    public void compute() {
        World world = Bukkit.getWorld(this.worldID);
        Preconditions.checkState(world != null);
        Block block = world.getBlockAt(this.blockX, this.blockY, this.blockZ);
        this.action.accept(block);
    }
}
