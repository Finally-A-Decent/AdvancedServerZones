package info.preva1l.advancedserverzones.network;

import com.google.gson.Gson;
import info.preva1l.advancedserverzones.AdvancedServerZones;
import info.preva1l.advancedserverzones.borders.ConnectionService;
import info.preva1l.advancedserverzones.config.Config;
import info.preva1l.advancedserverzones.config.Servers;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public abstract class Broker {
    public static Broker instance;
    protected final Gson gson = new Gson();

    protected void handle(@NotNull Message message) {
        switch (message.getType()) {
            case TRANSFER -> message.getPayload().getTransferData().ifPresentOrElse(transferData -> {
                if (!transferData.targetServer().equals(Servers.i().getCurrent())) return;

                ConnectionService.instance.addData(transferData);
            }, () -> {
                throw new IllegalStateException("transfer packet received with no transfer data");
            });

            case CHAT_MESSAGE -> message.getPayload().getChatMessage().ifPresent(chatMessage -> {
                if (!Config.i().getChatsync().isEnabled()) return;
                Config.i().getChatsync().getMode().accept(chatMessage);
            });

            case RELOAD -> AdvancedServerZones.instance.reload();

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
