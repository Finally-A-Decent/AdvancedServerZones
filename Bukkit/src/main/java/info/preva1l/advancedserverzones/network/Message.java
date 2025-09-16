package info.preva1l.advancedserverzones.network;

import com.google.gson.annotations.Expose;
import info.preva1l.advancedserverzones.config.Servers;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public final class Message {
    @Expose @Builder.Default private String sender = Servers.i().getCurrent();
    @Expose private Type type;
    @Expose private Payload payload;

    public void send(Broker broker) {
        if (broker == null) return;
        broker.send(this);
    }

    public enum Type {
        TRANSFER,
        CHAT_MESSAGE,
        WORLD_STATE,

        ZONE_HEARTBEAT,
        ZONE_STOP,

        LEADER_ELECTION_START,
        LEADER_ELECTION_VOTE,
        LEADER_ELECTION_CHOSEN,
    }
}
