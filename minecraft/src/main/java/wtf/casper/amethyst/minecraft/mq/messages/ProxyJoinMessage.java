package wtf.casper.amethyst.minecraft.mq.messages;

import lombok.Getter;
import wtf.casper.amethyst.core.mq.Message;

import java.util.UUID;

@Getter
public class ProxyJoinMessage implements Message {
    private final UUID uniqueId;
    private final String name;
    private final String server;

    public ProxyJoinMessage(UUID uniqueId, String name, String server) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.server = server;
    }
}
