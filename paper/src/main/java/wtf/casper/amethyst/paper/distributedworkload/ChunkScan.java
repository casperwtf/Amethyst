package wtf.casper.amethyst.paper.distributedworkload;

import lombok.AllArgsConstructor;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import wtf.casper.amethyst.core.distributedworkload.WorkloadRunnable;

import java.util.function.Consumer;

@AllArgsConstructor
public class ChunkScan {
    private final WorkloadRunnable workloadRunnable;

    public void scanChunk(Chunk chunk, Consumer<Block> blockConsumer) {
        for (int x = 0; x < 16; x++) {
            for (int y = chunk.getWorld().getMaxHeight(); y >= chunk.getWorld().getMinHeight(); y--) {
                for (int z = 0; z < 16; z++) {
                    BlockScanWorkload workload = new BlockScanWorkload(chunk.getWorld().getUID(), x, y, z, blockConsumer);
                    workloadRunnable.addWorkload(workload);
                }
            }
        }
    }
}
