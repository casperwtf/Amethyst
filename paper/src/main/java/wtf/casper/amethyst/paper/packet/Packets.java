package wtf.casper.amethyst.paper.packet;

import com.github.retrooper.packetevents.event.PacketEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import wtf.casper.amethyst.core.collections.SortedList;

import java.util.function.BiFunction;

public class Packets {

    // Priority is higher numbers first
    private final static SortedList<SinglePacketHandler<? extends PacketWrapper<?>>> packetHandlers = new SortedList<>((o1, o2) -> {
        if (o1.priority() == o2.priority()) return 0;
        return o1.priority() > o2.priority() ? -1 : 1;
    });

    public static <T extends PacketWrapper<T>> void listen(PacketTypeCommon packetType, int priority, BiFunction<User, PacketEvent, PacketWrapper<T>> callback) {
        listen(SinglePacketHandler.of(packetType, priority, callback));
    }

    public static <T extends PacketWrapper<T>> void listen(SinglePacketHandler<T> handler) {
        packetHandlers.add(handler);
    }

    public record SinglePacketHandler<T extends PacketWrapper<?>>(PacketTypeCommon packetType, int priority, BiFunction<User, PacketEvent, PacketWrapper<T>> callback) {
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
//        PacketEvents.getAPI().getEventManager().registerListener(new PacketListener() {
//            @Override
//            public void onPacketHandshakeReceive(PacketHandshakeReceiveEvent event) {
//                for (SinglePacketHandler<? extends PacketWrapper<?>> handler : packetHandlers) {
//                    if (handler.packetType != event.getPacketType()) {
//                        continue;
//                    }
//
//                    if (event.getLastUsedWrapper() != null) {
//                        handler.callback.apply(event.getUser(), event.getLastUsedWrapper());
//                        return;
//                    }
//
//                    try {
//                        PacketWrapper<?> wrapper = handler.wrapperClass.getConstructor(PacketHandshakeReceiveEvent.class).newInstance(event);
//                        handler.callback.apply(event.getUser(), wrapper);
//                    } catch (InstantiationException | IllegalAccessException |
//                             InvocationTargetException | NoSuchMethodException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            }
//
//            @Override
//            public void onPacketStatusReceive(PacketStatusReceiveEvent event) {
//
//            }
//
//            @Override
//            public void onPacketStatusSend(PacketStatusSendEvent event) {
//
//            }
//
//            @Override
//            public void onPacketLoginReceive(PacketLoginReceiveEvent event) {
//
//            }
//
//            @Override
//            public void onPacketLoginSend(PacketLoginSendEvent event) {
//
//            }
//
//            @Override
//            public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
//
//            }
//
//            @Override
//            public void onPacketPlaySend(PacketPlaySendEvent event) {
//
//            }
//        });
    }
}
