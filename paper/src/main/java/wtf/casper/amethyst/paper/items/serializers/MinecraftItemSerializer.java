package wtf.casper.amethyst.paper.items.serializers;

import com.google.auto.service.AutoService;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import wtf.casper.amethyst.core.utils.MathUtils;
import wtf.casper.amethyst.paper.items.ItemSerializer;
import wtf.casper.amethyst.paper.utils.HexUtils;
import wtf.casper.amethyst.paper.utils.StringUtilsPaper;

import java.util.Optional;

@AutoService(ItemSerializer.class)
public class MinecraftItemSerializer implements ItemSerializer {

    @Override
    public Optional<ItemStack> serializeType(String first) {
        try {
            Material material = Material.valueOf(first.toUpperCase());
            return Optional.of(new ItemStack(material));
        } catch (IllegalArgumentException ignored) {
        }

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
            return true;
        }

        if (next.startsWith("durability:")) {
            if (MathUtils.validateInt(next.substring(11))) {
                itemStack.setDurability(Short.parseShort(next.substring(11)));
                return true;
            }
        }

        if (next.startsWith("name:")) {
            ItemMeta meta = itemStack.getItemMeta();
            String substring = next.substring(5);
            if (substring.startsWith("\"")) {
                substring = substring.substring(1);
            }
            if (substring.endsWith("\"")) {
                substring = substring.substring(0, substring.length() - 1);
            }

            meta.setDisplayName(HexUtils.colorify(substring));
            itemStack.setItemMeta(meta);
            return true;
        }

        //TODO: figure out how to serialize with spaces in lore lines while keeping support for all chars
//        if (next.startsWith("lore:")) {
//            ItemMeta meta = itemStack.getItemMeta();
//            meta.setLore(HexUtils.colorify(Arrays.asList(next.substring(5).split("\n"))));
//            itemStack.setItemMeta(meta);
//            return true;
//        }

        if (next.equalsIgnoreCase("unbreakable")) {
            ItemMeta meta = itemStack.getItemMeta();
            meta.setUnbreakable(true);
            itemStack.setItemMeta(meta);
            return true;
        }

        if (next.startsWith("custommodeldata:")) {
            ItemMeta meta = itemStack.getItemMeta();
            meta.setCustomModelData(Integer.parseInt(next.substring("custommodeldata:".length())));
            itemStack.setItemMeta(meta);
            return true;
        }

        // TODO: add item attributes

        return false;
    }

    @Override
    public Optional<String> deserializeType(ItemStack item) {
        return Optional.of(item.getType().toString());
    }

    @Override
    public Optional<String> deserializeMeta(ItemStack item) {

        StringBuilder builder = new StringBuilder();

        if (item.getAmount() != 1) {
            builder.append("amount:").append(item.getAmount()).append(" ");
        }

        if (item.getDurability() != 0) {
            builder.append("durability:").append(item.getDurability()).append(" ");
        }

        if (!item.hasItemMeta()) {
            return Optional.of(builder.toString());
        }

        if (item.getItemMeta().hasDisplayName()) {
            builder.append("name:\"").append(item.getItemMeta().getDisplayName()).append("\" ");
        }

//        if (item.getItemMeta().hasLore()) {
//            builder.append("lore:").append(String.join("\n", item.getItemMeta().getLore())).append(" ");
//        }

        if (item.getItemMeta().isUnbreakable()) {
            builder.append("unbreakable ");
        }

        if (item.getItemMeta().hasCustomModelData()) {
            builder.append("custommodeldata:").append(item.getItemMeta().getCustomModelData()).append(" ");
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
