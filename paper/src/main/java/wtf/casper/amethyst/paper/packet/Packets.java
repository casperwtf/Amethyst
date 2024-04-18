package wtf.casper.amethyst.paper.packet;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketEvent;
import com.github.retrooper.packetevents.event.ProtocolPacketEvent;
import com.github.retrooper.packetevents.event.simple.*;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import org.bukkit.entity.Player;
import wtf.casper.amethyst.core.collections.SortedList;
import wtf.casper.amethyst.core.obj.Pair;
import wtf.casper.amethyst.paper.utils.PacketListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

class Packets {

    // Priority is higher numbers first
    private final static Map<PacketTypeCommon, SortedList<SinglePacketHandler<? extends PacketWrapper<?>>>> packetHandlers = new HashMap<>();
    private final static Set<PacketWrapper<?>> sentPackets = new HashSet<>();

    public static <T extends PacketWrapper<T>> void listen(PacketTypeCommon packetType, int priority, BiFunction<User, PacketEvent, PacketWrapper<T>> callback) {
        listen(SinglePacketHandler.of(packetType, priority, callback));
    }

    public static <T extends PacketWrapper<T>> void listen(SinglePacketHandler<T> handler) {
        packetHandlers.putIfAbsent(handler.packetType, new SortedList<>((o1, o2) -> {
            if (o1.priority() == o2.priority()) return 0;
            return o1.priority() > o2.priority() ? -1 : 1;
        })).add(handler);
    }

    public static void send(Player player, PacketWrapper<?> wrapper) {

    }

    public record SinglePacketHandler<T extends PacketWrapper<?>>(
            PacketTypeCommon packetType, int priority,
            BiFunction<User, PacketEvent, PacketWrapper<T>> callback) {
        public SinglePacketHandler(PacketTypeCommon packetType, int priority, BiFunction<User, PacketEvent, PacketWrapper<T>> callback) {
            this.packetType = packetType;
            this.priority = priority;
            this.callback = callback;
        }

        public static <T extends PacketWrapper<T>> SinglePacketHandler<T> of(PacketTypeCommon packetType, int priority, BiFunction<User, PacketEvent, PacketWrapper<T>> callback) {
            return new SinglePacketHandler<>(packetType, priority, callback);
        }
    }

    public void init() {
        PacketEvents.getAPI().getEventManager().registerListener(new PacketListener() {
            @Override
            public void onPacketHandshakeReceive(PacketHandshakeReceiveEvent event) {

            }

            @Override
            public void onPacketStatusReceive(PacketStatusReceiveEvent event) {

            }

            @Override
            public void onPacketStatusSend(PacketStatusSendEvent event) {

            }

            @Override
            public void onPacketLoginReceive(PacketLoginReceiveEvent event) {

            }

            @Override
            public void onPacketLoginSend(PacketLoginSendEvent event) {

            }

            @Override
            public void onPacketPlayReceive(PacketPlayReceiveEvent event) {

            }

            @Override
            public void onPacketPlaySend(PacketPlaySendEvent event) {

            }
        });
    }

    private Pair<PacketWrapper<?>, Boolean> handlePacket(ProtocolPacketEvent<?> event) {
        SortedList<SinglePacketHandler<? extends PacketWrapper<?>>> list = packetHandlers.get(event.getPacketType());
        if (list == null) return Pair.of(null, false);
        if (list.isEmpty()) return Pair.of(null, false);

        for (SinglePacketHandler<? extends PacketWrapper<?>> handler : list) {
            PacketWrapper<?> wrapper = handler.callback.apply(event.getUser(), event);
            if (wrapper == null) continue;
            return Pair.of(wrapper, true);
        }

        return Pair.of(null, false);
    }
}
