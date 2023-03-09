package wtf.casper.amethyst.minecraft.mq.messages;

import lombok.Getter;
import wtf.casper.amethyst.core.mq.Message;

import java.util.UUID;

@Getter
public class ProxyLeaveMessage extends Message {
    private final UUID uniqueId;
    private final String name;
    private final String finalServer;

    public ProxyLeaveMessage(UUID uniqueId, String name, String finalServer) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.finalServer = finalServer;
    }
}
