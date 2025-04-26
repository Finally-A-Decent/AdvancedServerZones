package info.preva1l.advancedserverzones.network;

import com.google.gson.Gson;
import info.preva1l.advancedserverzones.AdvancedServerZones;
import info.preva1l.advancedserverzones.config.Config;
import info.preva1l.advancedserverzones.config.Servers;
import info.preva1l.trashcan.plugin.annotations.PluginDisable;
import info.preva1l.trashcan.plugin.annotations.PluginEnable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public abstract class Broker {
    private static Broker instance;
    protected final AdvancedServerZones plugin;
    protected final Gson gson;

    protected Broker(@NotNull AdvancedServerZones plugin) {
        this.plugin = plugin;
        this.gson = new Gson();
    }

    protected void handle(@NotNull Message message) {
        switch (message.getType()) {
            case CHAT_MESSAGE -> message.getPayload().getChatMessage().ifPresent(chatMessage -> {
                if (!Config.i().getChatsync().isEnabled()) return;
                Config.i().getChatsync().getMode().accept(chatMessage);
            });

            default -> throw new IllegalStateException("Unexpected value: " + message.getType());
        }
    }

    public abstract void connect();

    protected abstract void send(@NotNull Message message);

    @PluginDisable
    public abstract void destroy();

    @Getter
    @AllArgsConstructor
    public enum Type {
        REDIS("Redis"),
        ;
        private final String displayName;
    }

    @PluginEnable
    public static Broker i() {
        return instance == null ? instance = new RedisBroker(AdvancedServerZones.i()) : instance;
    }
}
