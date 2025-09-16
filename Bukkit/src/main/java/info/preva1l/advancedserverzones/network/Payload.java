package info.preva1l.advancedserverzones.network;

import com.google.gson.annotations.Expose;
import info.preva1l.advancedserverzones.chat.ChatMessage;
import info.preva1l.advancedserverzones.borders.transfer.TransferData;
import info.preva1l.advancedserverzones.world.WorldState;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public final class Payload {
    @Nullable
    @Expose
    private WorldState worldState;

    @Nullable
    @Expose
    private ChatMessage chatMessage;

    @Nullable
    @Expose
    private TransferData transferData;

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
     * Returns a payload containing a {@link WorldState}.
     *
     * @param worldState the world state to send
     * @return a payload containing world state
     */
    @NotNull
    public static Payload withWorldState(@NotNull WorldState worldState) {
        final Payload payload = new Payload();
        payload.worldState = worldState;
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

    @NotNull
    public static Payload withTransferData(@NotNull TransferData transferData) {
        final Payload payload = new Payload();
        payload.transferData = transferData;
        return payload;
    }

    public Optional<WorldState> getWorldState() {
        return Optional.ofNullable(worldState);
    }

    public Optional<ChatMessage> getChatMessage() {
        return Optional.ofNullable(chatMessage);
    }

    public Optional<TransferData> getTransferData() {
        return Optional.ofNullable(transferData);
    }
}
