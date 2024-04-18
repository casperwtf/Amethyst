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
        int maxHeight = chunk.getWorld().getMaxHeight();
        int minHeight = chunk.getWorld().getMinHeight();
        for (int x = 0; x < 16; x++) {
            for (int y = maxHeight; y >= minHeight; y--) {
                for (int z = 0; z < 16; z++) {
                    BlockScanWorkload workload = new BlockScanWorkload(chunk.getWorld().getUID(), x, y, z, blockConsumer);
                    workloadRunnable.addWorkload(workload);
                }
            }
        }
    }
}
