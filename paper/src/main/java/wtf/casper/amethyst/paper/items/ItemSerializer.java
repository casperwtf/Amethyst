package wtf.casper.amethyst.paper.items;

import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public interface ItemSerializer {

    Optional<ItemStack> initialStack(String first);

    /**
    * @return true if the itemStack was modified
    * */
    boolean tryAppend(ItemStack itemStack, String next);

    Optional<String> deserializeType(ItemStack item);

    Optional<String> deserializeMeta(ItemStack item);

}
