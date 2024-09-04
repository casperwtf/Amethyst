package wtf.casper.amethyst.paper.items.serializers;

//import com.mysticalkingdoms.missionpouches.MissionPouches;
//import com.mysticalkingdoms.missionpouches.pouches.Pouch;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import wtf.casper.amethyst.core.utils.Lazy;
import wtf.casper.amethyst.paper.items.ItemSerializer;

import java.util.Optional;

public class MissionPouchesItemSerializer implements ItemSerializer {

    private final Lazy<Boolean> hasMissionPouches = new Lazy<>(() -> Bukkit.getPluginManager().isPluginEnabled("MissionPouches"));

    @Override
    public Optional<ItemStack> serializeType(String s) {
//        if (!hasMissionPouches.get()) {
//            return Optional.empty();
//        }
//
//        if (!s.startsWith("missionpouches:")) {
//            return Optional.empty();
//        }
//
//        String id = s.substring(15);
//        boolean locked = id.startsWith("locked:"); // or unlocked:
//        id = locked ? id.substring(7) : id.substring(9);
//
//        Pouch name = MissionPouches.getInstance().getPouchManager().getPouchFromName(id);
//        if (name == null) {
//            return Optional.empty();
//        }
//
//        return Optional.of(locked ? name.buildLocked(MissionPouches.getInstance().getDateTimeFormatter()) : name.buildUnlocked());
        return Optional.empty();
    }

    @Override
    public boolean serializeMeta(ItemStack itemStack, String s) {
        return false;
    }

    @Override
    public Optional<String> deserializeType(ItemStack itemStack) {
//        if (!hasMissionPouches.get()) {
//            return Optional.empty();
//        }
//
//        try {
//            String s = NBT.get(itemStack, readableNBT -> readableNBT.getCompound("MissionPouches").getString("Pouch"));
//            return Optional.of("missionpouches:" + s);
//        } catch (Exception e) {
//            return Optional.empty();
//        }
        return Optional.empty();
    }

    @Override
    public Optional<String> deserializeMeta(ItemStack itemStack) {
        return Optional.empty();
    }
}
