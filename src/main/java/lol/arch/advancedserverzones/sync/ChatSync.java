package lol.arch.advancedserverzones.sync;

import io.papermc.paper.event.player.AsyncChatEvent;
import lol.arch.advancedserverzones.AdvancedServerZones;
import lol.arch.advancedserverzones.ChatSyncReceiveEvent;
import lol.arch.advancedserverzones.ChatSyncSendEvent;
import lol.arch.advancedserverzones.config.Config;
import lol.arch.advancedserverzones.config.Servers;
import lol.arch.advancedserverzones.util.StringUtils;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.json.JSONException;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.UUID;

public final class ChatSync extends JedisPubSub implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    private void chatMessageListener(AsyncChatEvent event) {
        event.setCancelled(true);
        Config.CHAT_SYNC_MODE.toSyncMode().execute(event);
    }

    @Override
    public void onMessage(String channel, String json) {
        if (!channel.equals("asz.chat-sync")) return;
        try {
            JSONObject jsonObject = new JSONObject(json);
            Config.CHAT_SYNC_MODE.toSyncMode().accept(jsonObject);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onSubscribe(String channel, int subscribedTo) {
        AdvancedServerZones.getInstance().getConsole().info("PubSub subscribed to: " + channel);
    }

    public enum ChatSyncMode {
        VANILLA {
            @Override
            public void execute(AsyncChatEvent event) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("uuid", event.getPlayer().getUniqueId().toString());
                jsonObject.put("message", MiniMessage.miniMessage().serialize(event.message()));

                try (Jedis jedis = AdvancedServerZones.getInstance().getPool().getResource()) {
                    jedis.auth(Config.REDIS_PASSWORD.toString());
                    jedis.publish("asz.chat-sync", jsonObject.toString());
                }
            }

            @SuppressWarnings("deprecation")
            @Override
            public void accept(JSONObject jsonObject) {
                Component component = Component.text(jsonObject.get("message").toString());
                Identity player = Identity.identity(UUID.fromString(jsonObject.get("uuid").toString()));

                Bukkit.getServer().sendMessage(player, component);
            }
        },
        CUSTOM {
            @Override
            public void execute(AsyncChatEvent event) {
                String formattedString = Config.CHAT_SYNC_CUSTOM.toString()
                        .replace("{server}", Servers.CURRENT_SERVER.toString())
                        .replace("{prefix}", PlaceholderManager.getPrefix(event.getPlayer()))
                        .replace("{suffix}", PlaceholderManager.getSuffix(event.getPlayer()))
                        .replace("{player_name}", event.getPlayer().getName())
                        .replace("{message}", MiniMessage.miniMessage().serialize(event.message()));

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("uuid", event.getPlayer().getUniqueId().toString());
                jsonObject.put("message", PlaceholderManager.formatWithPAPI(event.getPlayer(), formattedString));

                try (Jedis jedis = AdvancedServerZones.getInstance().getPool().getResource()) {
                    jedis.auth(Config.REDIS_PASSWORD.toString());
                    jedis.publish("asz.chat-sync", jsonObject.toString());
                }
            }

            @SuppressWarnings("deprecation")
            @Override
            public void accept(JSONObject jsonObject) {
                Component component = StringUtils.message(jsonObject.get("message").toString());
                Identity player = Identity.identity(UUID.fromString(jsonObject.get("uuid").toString()));

                Bukkit.getServer().sendMessage(player, component);
            }
        },
        API {
            @Override
            public void execute(AsyncChatEvent event) {
                new ChatSyncSendEvent(event.getPlayer().getUniqueId(), MiniMessage.miniMessage().serialize(event.message()));
            }

            @Override
            public void accept(JSONObject jsonObject) {
                new ChatSyncReceiveEvent(UUID.fromString(jsonObject.get("uuid").toString()), jsonObject.get("message").toString());
            }
        };

        public abstract void execute(AsyncChatEvent event);
        public abstract void accept(JSONObject jsonObject);
    }
}
