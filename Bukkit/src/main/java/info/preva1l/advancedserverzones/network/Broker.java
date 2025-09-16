package info.preva1l.advancedserverzones.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import info.preva1l.advancedserverzones.AdvancedServerZones;
import info.preva1l.advancedserverzones.borders.transfer.TransferService;
import info.preva1l.advancedserverzones.config.Config;
import info.preva1l.advancedserverzones.config.Servers;
import info.preva1l.advancedserverzones.world.WorldState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;

public abstract class Broker {
    public static Broker instance;
    public static String leader;
    protected final Gson gson = GsonComponentSerializer.gson().populator().apply(new GsonBuilder()).create();

    protected void handle(@NotNull Message message) {
        if (message.getSender().equals(Servers.i().getCurrent())) return;

        switch (message.getType()) {
            case TRANSFER -> message.getPayload().getTransferData().ifPresentOrElse(transferData -> {
                if (!transferData.targetServer().equals(Servers.i().getCurrent())) return;

                TransferService.instance.addData(transferData);
            }, () -> {
                throw new IllegalStateException("transfer packet received with no transfer data");
            });

            case WORLD_STATE -> message.getPayload().getWorldState().ifPresent(WorldState::apply);

            case CHAT_MESSAGE -> message.getPayload().getChatMessage().ifPresent(chatMessage -> {
                if (!Config.i().getChatsync().isEnabled()) return;
                Config.i().getChatsync().getMode().accept(chatMessage);
            });

            default -> throw new IllegalStateException("Unexpected value: " + message.getType());
        }
    }

    public abstract void connect();

    protected abstract void send(@NotNull Message message);

    public abstract void destroy();

    @Getter
    @AllArgsConstructor
    public enum Type {
        REDIS("Redis"),
        ;
        private final String displayName;
    }
}
