package info.preva1l.advancedserverzones.network;

import com.google.gson.annotations.Expose;
import info.preva1l.advancedserverzones.network.types.ChatMessage;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class Payload {
    @Nullable
    @Expose
    private UUID uuid;

    @Nullable
    @Expose
    private ChatMessage chatMessage;

    /**
     * Returns an empty cross-server message payload.
     *
     * @return an empty payload
     */
    @NotNull
    public static Payload empty() {
        return new Payload();
    }

    /**
     * Returns a payload containing a {@link UUID}.
     *
     * @param uuid the uuid to send
     * @return a payload containing the uuid
     */
    @NotNull
    public static Payload withUUID(@NotNull UUID uuid) {
        final Payload payload = new Payload();
        payload.uuid = uuid;
        return payload;
    }

    /**
     * Returns a payload containing a message and a sender.
     *
     * @param playerUUID the player that sent the message.
     * @param message the message to send
     * @return a payload containing the message
     */
    @NotNull
    public static Payload withChatMessage(@NotNull UUID playerUUID, @NotNull Component message) {
        final Payload payload = new Payload();
        payload.chatMessage = new ChatMessage(playerUUID, message);
        return payload;
    }

    public Optional<UUID> getUUID() {
        return Optional.ofNullable(uuid);
    }

    public Optional<ChatMessage> getChatMessage() {
        return Optional.ofNullable(chatMessage);
    }
}
