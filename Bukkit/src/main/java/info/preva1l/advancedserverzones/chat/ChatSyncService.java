package info.preva1l.advancedserverzones.chat;

import info.preva1l.advancedserverzones.ChatSyncReceiveEvent;
import info.preva1l.advancedserverzones.ChatSyncSendEvent;
import info.preva1l.advancedserverzones.config.Config;
import info.preva1l.advancedserverzones.config.Servers;
import info.preva1l.advancedserverzones.network.Broker;
import info.preva1l.advancedserverzones.network.Message;
import info.preva1l.advancedserverzones.network.Payload;
import info.preva1l.advancedserverzones.util.Text;
import info.preva1l.trashcan.flavor.annotations.Configure;
import info.preva1l.trashcan.flavor.annotations.Service;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;

@Service
public final class ChatSyncService implements Listener {
    public static final ChatSyncService instance = new ChatSyncService();

    private Chat chat;

    @Configure
    public void configure() {
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            RegisteredServiceProvider<Chat> registration = Bukkit.getServicesManager().getRegistration(Chat.class);
            if (registration != null) chat = registration.getProvider();
        }
    }

    private String getPrefix(Player player) {
        if (chat != null) return chat.getPlayerPrefix(player);

        return "";
    }

    private String getSuffix(Player player) {
        if (chat != null) return chat.getPlayerSuffix(player);

        return "";
    }

    private String formatWithPAPI(Player player, String toFormat) {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            return PlaceholderAPI.setPlaceholders(player, toFormat);

        return toFormat;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void chatMessageListener(AsyncChatEvent event) {
        event.setCancelled(true);
        Config.i().getChatsync().getMode().execute(event);
    }

    public enum ChatSyncMode {
        VANILLA {
            @Override
            public void execute(AsyncChatEvent event) {
                Message.builder()
                        .type(Message.Type.CHAT_MESSAGE)
                        .payload(Payload.withChatMessage(event.getPlayer().getUniqueId(), event.message()))
                        .build().send(Broker.instance);
            }

            @Override
            public void accept(ChatMessage message) {
                Identity player = Identity.identity(message.player());

                Bukkit.getServer().sendMessage(player, message.message());
            }
        },
        CUSTOM {
            @Override
            public void execute(AsyncChatEvent event) {
                String formattedString = Config.i().getChatsync().getCustomFormat()
                        .replace("%server%", Servers.i().getCurrent())
                        .replace("%prefix%", instance.getPrefix(event.getPlayer()))
                        .replace("%suffix%", instance.getSuffix(event.getPlayer()))
                        .replace("%player%", event.getPlayer().getName())
                        .replace("%message%", MiniMessage.miniMessage().serialize(event.message()));

                Message.builder()
                        .type(Message.Type.CHAT_MESSAGE)
                        .payload(Payload.withChatMessage(
                                event.getPlayer().getUniqueId(),
                                Text.text(instance.formatWithPAPI(event.getPlayer(), formattedString)))
                        )
                        .build().send(Broker.instance);
            }

            @Override
            public void accept(ChatMessage message) {
                Identity player = Identity.identity(message.player());

                Bukkit.getServer().sendMessage(player, message.message());
            }
        },
        API {
            @Override
            public void execute(AsyncChatEvent event) {
                new ChatSyncSendEvent(event.getPlayer().getUniqueId(), MiniMessage.miniMessage().serialize(event.message()));
            }

            @Override
            public void accept(ChatMessage message) {
                new ChatSyncReceiveEvent(message.player(), message.message());
            }
        };

        public abstract void execute(AsyncChatEvent event);

        public abstract void accept(ChatMessage jsonObject);
    }
}
