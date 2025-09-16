package info.preva1l.advancedserverzones.chat;

import info.preva1l.advancedserverzones.ChatSyncReceiveEvent;
import info.preva1l.advancedserverzones.ChatSyncSendEvent;
import info.preva1l.advancedserverzones.config.Config;
import info.preva1l.advancedserverzones.config.Servers;
import info.preva1l.advancedserverzones.network.Broker;
import info.preva1l.advancedserverzones.network.Message;
import info.preva1l.advancedserverzones.network.Payload;
import info.preva1l.advancedserverzones.network.types.ChatMessage;
import info.preva1l.advancedserverzones.util.Text;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;

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
                    .replace("%prefix%", PlaceholderManager.getPrefix(event.getPlayer()))
                    .replace("%suffix%", PlaceholderManager.getSuffix(event.getPlayer()))
                    .replace("%player%", event.getPlayer().getName())
                    .replace("%message%", MiniMessage.miniMessage().serialize(event.message()));

            Message.builder()
                    .type(Message.Type.CHAT_MESSAGE)
                    .payload(Payload.withChatMessage(
                            event.getPlayer().getUniqueId(),
                            Text.text(PlaceholderManager.formatWithPAPI(event.getPlayer(), formattedString)))
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