package wtf.casper.amethyst.paper.items.serializers;

import com.google.auto.service.AutoService;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import wtf.casper.amethyst.core.utils.Lazy;
import wtf.casper.amethyst.paper.items.ItemSerializer;

import java.util.Optional;

@AutoService(ItemSerializer.class)
public class ItemBridgeSerializer implements ItemSerializer {

    private final Lazy<Boolean> hasItembridge = new Lazy<>(() -> Bukkit.getPluginManager().isPluginEnabled("ItemBridge"));

    @Override
    public Optional<ItemStack> serializeType(String first) {
        if (!hasItembridge.get()) {
            return Optional.empty();
        }

        if (!first.startsWith("itembridge:")) {
            return Optional.empty();
        }

        first = first.substring(11);
        return Optional.ofNullable(getItemBridgeItem(first));
    }

    @Override
    public boolean serializeMeta(ItemStack itemStack, String next) {
        return false;
    }

    @Override
    public Optional<String> deserializeType(ItemStack item) {
        if (!hasItembridge.get()) {
            return Optional.empty();
        }

        com.jojodmo.itembridge.ItemBridgeKey id = com.jojodmo.itembridge.ItemBridge.getItemKey(item);
        if (id == null) {
            return Optional.empty();
        }

        return Optional.of("itembridge:" + id);
    }

    @Override
    public Optional<String> deserializeMeta(ItemStack item) {
        return Optional.empty();
    }

    private ItemStack getItemBridgeItem(String id) {
        return com.jojodmo.itembridge.ItemBridge.getItemStack(id);
    }
}
