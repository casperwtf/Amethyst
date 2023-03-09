package wtf.casper.amethyst.paper.hooks.stacker;

import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import wtf.casper.amethyst.paper.hooks.IHook;

public interface IStacker extends IHook {

    long getStackAmount(LivingEntity entity);

    void setStackAmount(LivingEntity entity, long amount);

    @Nullable
    ItemStack getStackItem(Item entity);

    void setStackItem(Item entity, ItemStack itemStack);

    long getStackAmount(Item entity);

    void setStackAmount(Item entity, long amount);

    default void addStackAmount(LivingEntity entity, long amount) {
        setStackAmount(entity, getStackAmount(entity) + amount);
    }

    default void removeStackAmount(LivingEntity entity, long amount) {
        setStackAmount(entity, getStackAmount(entity) - amount);
    }

    default void multiplyStackAmount(LivingEntity entity, long amount) {
        setStackAmount(entity, getStackAmount(entity) * amount);
    }

    default void divideStackAmount(LivingEntity entity, long amount) {
        setStackAmount(entity, getStackAmount(entity) / amount);
    }

    default void addStackAmount(Item entity, long amount) {
        setStackAmount(entity, getStackAmount(entity) + amount);
    }

    default void removeStackAmount(Item entity, long amount) {
        setStackAmount(entity, getStackAmount(entity) - amount);
    }

    default void multiplyStackAmount(Item entity, long amount) {
        setStackAmount(entity, getStackAmount(entity) * amount);
    }

    default void divideStackAmount(Item entity, long amount) {
        setStackAmount(entity, getStackAmount(entity) / amount);
    }

    long getStackAmount(Block block);

    void setStackAmount(Block block, long amount);

    long getStackedSpawners(Block block);

    void setStackedSpawners(Block block, long amount);
}
