package wtf.casper.amethyst.paper.distributedworkload;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import wtf.casper.amethyst.core.distributedworkload.WorkloadRunnable;
import wtf.casper.amethyst.core.collections.RandomCollection;

import java.util.List;

@AllArgsConstructor
public class DistributedFiller implements VolumeFiller {

    private final WorkloadRunnable workloadRunnable;

    @Override
    public void fillMat(Location cornerA, Location cornerB, Material material) {
        Preconditions.checkArgument(cornerA.getWorld() == cornerB.getWorld() && cornerA.getWorld() != null);
        BoundingBox box = BoundingBox.of(cornerA.getBlock(), cornerB.getBlock());
        Vector max = box.getMax();
        Vector min = box.getMin();

        World world = cornerA.getWorld();

        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    PlaceableBlock placableBlock = new PlaceableBlock(world.getUID(), x, y, z, material);
                    this.workloadRunnable.addWorkload(placableBlock);
                }
            }
        }
    }

    @Override
    public void fillMat(Location cornerA, Location cornerB, List<Material> material) {
        Preconditions.checkArgument(cornerA.getWorld() == cornerB.getWorld() && cornerA.getWorld() != null);
        BoundingBox box = BoundingBox.of(cornerA.getBlock(), cornerB.getBlock());
        Vector max = box.getMax();
        Vector min = box.getMin();
        RandomCollection<Material> randomCollection = new RandomCollection<>();
        for (Material material1 : material) {
            randomCollection.add(1.0, material1);
        }

        World world = cornerA.getWorld();

        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    PlaceableBlock placableBlock = new PlaceableBlock(world.getUID(), x, y, z, randomCollection.next());
                    this.workloadRunnable.addWorkload(placableBlock);
                }
            }
        }
    }

    @Override
    public void replaceMat(Location cornerA, Location cornerB, Material replaced, Material replacer) {
        Preconditions.checkArgument(cornerA.getWorld() == cornerB.getWorld() && cornerA.getWorld() != null);
        BoundingBox box = BoundingBox.of(cornerA.getBlock(), cornerB.getBlock());
        Vector max = box.getMax();
        Vector min = box.getMin();

        World world = cornerA.getWorld();

        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    if (!world.getBlockAt(x, y, z).getType().equals(replaced)) {
                        continue;
                    }
                    PlaceableBlock placableBlock = new PlaceableBlock(world.getUID(), x, y, z, replacer);
                    this.workloadRunnable.addWorkload(placableBlock);
                }
            }
        }
    }

    @Override
    public void replaceMat(Location cornerA, Location cornerB, List<Material> replaced, List<Material> replacer) {
        Preconditions.checkArgument(cornerA.getWorld() == cornerB.getWorld() && cornerA.getWorld() != null);
        BoundingBox box = BoundingBox.of(cornerA.getBlock(), cornerB.getBlock());
        Vector max = box.getMax();
        Vector min = box.getMin();
        RandomCollection<Material> randomCollection = new RandomCollection<>();
        for (Material material1 : replacer) {
            randomCollection.add(1.0, material1);
        }

        World world = cornerA.getWorld();

        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    if (!replaced.contains(world.getBlockAt(x, y, z).getType())) {
                        continue;
                    }
                    PlaceableBlock placableBlock = new PlaceableBlock(world.getUID(), x, y, z, randomCollection.next());
                    this.workloadRunnable.addWorkload(placableBlock);
                }
            }
        }
    }

    @Override
    public void fillData(Location cornerA, Location cornerB, List<BlockData> blockData) {
        Preconditions.checkArgument(cornerA.getWorld() == cornerB.getWorld() && cornerA.getWorld() != null);
        BoundingBox box = BoundingBox.of(cornerA.getBlock(), cornerB.getBlock());
        Vector max = box.getMax();
        Vector min = box.getMin();
        RandomCollection<BlockData> randomCollection = new RandomCollection<>();
        for (BlockData blockData1 : blockData) {
            randomCollection.add(1.0, blockData1);
        }

        World world = cornerA.getWorld();

        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    PlaceableBlock placableBlock = new PlaceableBlock(world.getUID(), x, y, z, randomCollection.next());
                    this.workloadRunnable.addWorkload(placableBlock);
                }
            }
        }
    }

    @Override
    public void fillData(Location cornerA, Location cornerB, BlockData blockData) {
        Preconditions.checkArgument(cornerA.getWorld() == cornerB.getWorld() && cornerA.getWorld() != null);
        BoundingBox box = BoundingBox.of(cornerA.getBlock(), cornerB.getBlock());
        Vector max = box.getMax();
        Vector min = box.getMin();

        World world = cornerA.getWorld();

        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    PlaceableBlock placableBlock = new PlaceableBlock(world.getUID(), x, y, z, blockData);
                    this.workloadRunnable.addWorkload(placableBlock);
                }
            }
        }
    }

    @Override
    public void replaceData(Location cornerA, Location cornerB, List<BlockData> replaced, List<BlockData> replacer) {
        Preconditions.checkArgument(cornerA.getWorld() == cornerB.getWorld() && cornerA.getWorld() != null);
        BoundingBox box = BoundingBox.of(cornerA.getBlock(), cornerB.getBlock());
        Vector max = box.getMax();
        Vector min = box.getMin();
        RandomCollection<BlockData> randomCollection = new RandomCollection<>();
        for (BlockData blockData1 : replacer) {
            randomCollection.add(1.0, blockData1);
        }

        World world = cornerA.getWorld();

        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    if (!replaced.contains(world.getBlockAt(x, y, z).getBlockData())) {
                        continue;
                    }
                    PlaceableBlock placableBlock = new PlaceableBlock(world.getUID(), x, y, z, randomCollection.next());
                    this.workloadRunnable.addWorkload(placableBlock);
                }
            }
        }
    }

    @Override
    public void replaceData(Location cornerA, Location cornerB, BlockData replaced, BlockData replacer) {
        Preconditions.checkArgument(cornerA.getWorld() == cornerB.getWorld() && cornerA.getWorld() != null);
        BoundingBox box = BoundingBox.of(cornerA.getBlock(), cornerB.getBlock());
        Vector max = box.getMax();
        Vector min = box.getMin();

        World world = cornerA.getWorld();

        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    if (!world.getBlockAt(x, y, z).getBlockData().equals(replaced)) {
                        continue;
                    }
                    PlaceableBlock placableBlock = new PlaceableBlock(world.getUID(), x, y, z, replacer);
                    this.workloadRunnable.addWorkload(placableBlock);
                }
            }
        }
    }
}