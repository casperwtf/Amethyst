package wtf.casper.amethyst.minecraft.mq.messages;

import lombok.Getter;
import wtf.casper.amethyst.core.mq.Message;

import java.util.UUID;

@Getter
public class ProxyMoveServerRequest implements Message {
    private final UUID target;
    private final String server;

    public ProxyMoveServerRequest(UUID target, String server) {
        this.target = target;
        this.server = server;
    }
}
