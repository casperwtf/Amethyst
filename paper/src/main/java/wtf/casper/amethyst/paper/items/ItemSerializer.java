package wtf.casper.amethyst.paper.items;

import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public interface ItemSerializer {

    /**
     * Stage One of the deserialization process, this method should generate the initial item stack that the metadata is appended to
     * @param first
     * @return the initial item stack or empty if the item is not supported by this serializer
     */
    Optional<ItemStack> initialStack(String first);

    /**
     * Stage Two of the deserialization process, this method should append the metadata to the itemStack
     * @return true if the itemStack was modified
     */
    boolean tryAppend(ItemStack itemStack, String next);

    /**
     * Stage one of the serialization process, this method should return the type of the item
     * @param item
     * @return the type of the item or empty if the item is not supported by this serializer
     */
    Optional<String> deserializeType(ItemStack item);

    /**
     * Stage two of the serialization process, this method should return the metadata of the item
     * @param item
     * @return the metadata of the item or empty if the item is not supported by this serializer
     */
    Optional<String> deserializeMeta(ItemStack item);

}
