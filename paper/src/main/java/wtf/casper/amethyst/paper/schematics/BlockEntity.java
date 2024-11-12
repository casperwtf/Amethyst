package wtf.casper.amethyst.paper.schematics;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
// TODO: make data class for every tile entity type to support more than just the basic ones
public class BlockEntity {
    private final int x;
    private final int y;
    private final int z;
    private final String id;
    private final String rawData;

    public BlockEntity(int x, int y, int z, String id, String rawData) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.id = id;
        this.rawData = rawData;
    }
}
