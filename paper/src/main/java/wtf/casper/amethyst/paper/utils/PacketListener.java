package wtf.casper.amethyst.paper.utils;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;

public abstract class PacketListener extends SimplePacketListenerAbstract {

    public void register() {
        PacketEvents.getAPI().getEventManager().registerListener(this);
    }
}