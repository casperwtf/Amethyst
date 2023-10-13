package wtf.casper.amethyst.paper.distributedworkload;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import java.util.List;

public interface VolumeFiller {

    void fillMat(Location cornerA, Location cornerB, Material material);

    void fillMat(Location cornerA, Location cornerB, List<Material> material);

    void replaceMat(Location cornerA, Location cornerB, Material replaced, Material replacer);

    void replaceMat(Location cornerA, Location cornerB, List<Material> replaced, List<Material> replacer);

    void fillData(Location cornerA, Location cornerB, BlockData blockData);

    void fillData(Location cornerA, Location cornerB, List<BlockData> blockData);

    void replaceData(Location cornerA, Location cornerB, BlockData replaced, BlockData replacer);

    void replaceData(Location cornerA, Location cornerB, List<BlockData> replaced, List<BlockData> replacer);
}
