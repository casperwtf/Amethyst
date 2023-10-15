package wtf.casper.amethyst.paper.utils;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;

public abstract class PacketListener extends SimplePacketListenerAbstract {

    public void register() {
        PacketEvents.getAPI().getEventManager().registerListener(this);
    }
}
