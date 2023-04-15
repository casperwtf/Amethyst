package wtf.casper.amethyst.paper.hooks.stacker.impl;

import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import wtf.casper.amethyst.paper.hooks.stacker.IStacker;

public class DefaultStacker implements IStacker {
    @Override
    public boolean canEnable() {
        return true;
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }


    @Override
    public long getStackAmount(LivingEntity entity) {
        return 1;
    }

    @Override
    public void setStackAmount(LivingEntity entity, long amount) {

    }

    @Nullable
    @Override
    public ItemStack getStackItem(Item entity) {
        return entity.getItemStack();
    }

    @Override
    public void setStackItem(Item entity, ItemStack itemStack) {
        entity.setItemStack(itemStack);
    }

    @Override
    public long getStackAmount(Item entity) {
        return 1;
    }

    @Override
    public void setStackAmount(Item entity, long amount) {
        ItemStack stack = entity.getItemStack();
        stack.setAmount((int) amount);
        entity.setItemStack(stack);
    }

    @Override
    public long getStackAmount(Block block) {
        return 1;
    }

    @Override
    public void setStackAmount(Block block, long amount) {

    }

    @Override
    public long getStackedSpawners(Block block) {
        return 1;
    }

    @Override
    public void setStackedSpawners(Block block, long amount) {

    }
}
