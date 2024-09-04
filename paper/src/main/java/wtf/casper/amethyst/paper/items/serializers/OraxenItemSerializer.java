package wtf.casper.amethyst.paper.items.serializers;

//import io.th0rgal.oraxen.api.OraxenItems;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import wtf.casper.amethyst.core.utils.Lazy;
import wtf.casper.amethyst.core.utils.MathUtils;
import wtf.casper.amethyst.paper.items.ItemSerializer;

import java.util.Optional;

public class OraxenItemSerializer implements ItemSerializer {

    private final Lazy<Boolean> hasOraxen = new Lazy<>(() -> Bukkit.getPluginManager().isPluginEnabled("Oraxen"));

    @Override
    public Optional<ItemStack> serializeType(String s) {
//        if (!hasOraxen.get()) {
//            return Optional.empty();
//        }
//
//        if (!s.startsWith("oraxen:")) {
//            return Optional.empty();
//        }
//
//        String id = s.substring(7);
//
//        if (!OraxenItems.exists(id)) {
//            return Optional.empty();
//        }
//
//        return Optional.of(OraxenItems.getItemById(id).build());
        return Optional.empty();
    }

    @Override
    public boolean serializeMeta(ItemStack itemStack, String next) {

        if (next.startsWith("amount:")) {
            if (MathUtils.validateInt(next.substring(7))) {
                itemStack.setAmount(Integer.parseInt(next.substring(7)));
                return true;
            }
        }

        if (next.startsWith("enchant:")) {
            String[] enchantSplit = next.substring(8).split(":");
            if (enchantSplit.length != 3) {
                throw new IllegalArgumentException("Invalid enchantment format: " + next);
            }

            Enchantment enchantment = Enchantment.getByKey(new NamespacedKey(enchantSplit[0], enchantSplit[1]));
            if (enchantment == null) {
                throw new IllegalArgumentException("Invalid enchantment: " + next);
            }

            if (!MathUtils.validateInt(enchantSplit[2])) {
                throw new IllegalArgumentException("Invalid enchantment level: " + next);
            }

            int level = Integer.parseInt(enchantSplit[2]);
            ItemMeta meta = itemStack.getItemMeta();
            meta.addEnchant(enchantment, level, true);
            itemStack.setItemMeta(meta);
        }

        return false;
    }

    @Override
    public Optional<String> deserializeType(ItemStack itemStack) {
//        if (!hasOraxen.get()) {
//            return Optional.empty();
//        }
//
//        String id = OraxenItems.getIdByItem(itemStack);
//        if (id == null) {
//            return Optional.empty();
//        }
//
//        return Optional.of("oraxen:" + id);
        return Optional.empty();
    }

    @Override
    public Optional<String> deserializeMeta(ItemStack item) {
        StringBuilder builder = new StringBuilder();

        if (item.getAmount() != 1) {
            builder.append("amount:").append(item.getAmount()).append(" ");
        }

        if (item.getItemMeta().hasEnchants()) {
            item.getItemMeta().getEnchants().forEach((enchantment, integer) -> {
                builder.append("enchant:").append(enchantment.getKey().getNamespace()).append(":")
                        .append(enchantment.getKey().getKey()).append(":").append(integer).append(" ");
            });
        }

        return Optional.of(builder.toString().trim());
    }
}
