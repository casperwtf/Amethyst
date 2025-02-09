package wtf.casper.amethyst.paper.hooks.stacker.impl;

import com.google.auto.service.AutoService;
import dev.rosewood.rosestacker.api.RoseStackerAPI;
import dev.rosewood.rosestacker.stack.StackedBlock;
import dev.rosewood.rosestacker.stack.StackedEntity;
import dev.rosewood.rosestacker.stack.StackedItem;
import dev.rosewood.rosestacker.stack.StackedSpawner;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import wtf.casper.amethyst.paper.hooks.stacker.IStacker;

@AutoService(IStacker.class)
public class RoseStackStacker implements IStacker {

    @Override
    public boolean canEnable() {
        return Bukkit.getPluginManager().isPluginEnabled("RoseStacker");
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }

    @Override
    public long getStackAmount(LivingEntity entity) {
        StackedEntity stackedEntity = RoseStackerAPI.getInstance().getStackedEntity(entity);
        return stackedEntity == null ? 1 : stackedEntity.getStackSize();
    }

    @Override
    public void setStackAmount(LivingEntity entity, long amount) {
        StackedEntity stackedEntity = RoseStackerAPI.getInstance().getStackedEntity(entity);
        if (stackedEntity == null) {
            return;
        }

        if (amount == 0) {
            stackedEntity.killEntireStack();
            return;
        }

        if (amount > stackedEntity.getStackSize()) {
            stackedEntity.increaseStackSize((int) (amount - stackedEntity.getStackSize()), true);
        } else {
            stackedEntity.killPartialStack(null, (int) (stackedEntity.getStackSize() - amount));
        }

    }

    @Nullable
    @Override
    public ItemStack getStackItem(Item entity) {
        StackedItem stackedItem = RoseStackerAPI.getInstance().getStackedItem(entity);
        ItemStack itemStack = entity.getItemStack();
        if (stackedItem != null) {
            itemStack.setAmount(stackedItem.getStackSize());
        }

        return itemStack;
    }

    @Override
    public void setStackItem(Item entity, ItemStack itemStack) {
        entity.setItemStack(itemStack);
    }

    @Override
    public long getStackAmount(Item entity) {
        StackedItem stackedItem = RoseStackerAPI.getInstance().getStackedItem(entity);
        return stackedItem == null ? 1 : stackedItem.getStackSize();
    }

    @Override
    public void setStackAmount(Item entity, long amount) {
        StackedItem stackedItem = RoseStackerAPI.getInstance().getStackedItem(entity);
        if (stackedItem == null) {
            return;
        }

        if (amount <= 0) {
            stackedItem.getItem().remove();
            return;
        }

        stackedItem.setStackSize((int) amount);
    }

    @Override
    public long getStackAmount(Block block) {
        StackedBlock stackedBlock = RoseStackerAPI.getInstance().getStackedBlock(block);
        return stackedBlock == null ? 1 : stackedBlock.getStackSize();
    }

    @Override
    public void setStackAmount(Block block, long amount) {
        StackedBlock stackedBlock = RoseStackerAPI.getInstance().getStackedBlock(block);
        if (stackedBlock == null) {
            return;
        }

        if (amount <= 0) {
            block.setType(Material.AIR);
            return;
        }

        stackedBlock.setStackSize((int) amount);
    }

    @Override
    public long getStackedSpawners(Block block) {
        StackedSpawner spawner = RoseStackerAPI.getInstance().getStackedSpawner(block);
        return spawner == null ? 1 : spawner.getStackSize();
    }

    @Override
    public void setStackedSpawners(Block block, long amount) {
        StackedSpawner spawner = RoseStackerAPI.getInstance().getStackedSpawner(block);
        if (spawner == null) {
            return;
        }

        if (amount <= 0) {
            block.setType(Material.AIR);
            return;
        }

        spawner.setStackSize((int) amount);
    }

    @Override
    public int priority() {
        return 1;
    }
}
