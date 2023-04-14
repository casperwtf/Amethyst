package wtf.casper.amethyst.paper.distributedworkload;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import wtf.casper.amethyst.core.distributedworkload.WorkloadRunnable;
import wtf.casper.amethyst.core.utils.RandomCollection;

import java.util.List;

@AllArgsConstructor
public class DistributedFiller implements VolumeFiller {

    private final WorkloadRunnable workloadRunnable;

    @Override
    public void fill(Location cornerA, Location cornerB, Material material) {
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
    public void fill(Location cornerA, Location cornerB, List<Material> material) {
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
    public void replace(Location cornerA, Location cornerB, Material replaced, Material replacer) {
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
    public void replace(Location cornerA, Location cornerB, List<Material> replaced, List<Material> replacer) {
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

}