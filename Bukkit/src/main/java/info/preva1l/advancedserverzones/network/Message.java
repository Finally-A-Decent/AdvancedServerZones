package info.preva1l.advancedserverzones.network;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.concurrent.ThreadLocalRandom;

@Getter
@AllArgsConstructor
@Builder
public class Message {
    @Expose private Type type;
    @Expose private Payload payload;

    public void send(Broker broker) {
        if (broker == null) return;
        broker.send(this);
    }

    public enum Type {
        TRANSFER,
        CHAT_MESSAGE,
        RELOAD,
    }
}
