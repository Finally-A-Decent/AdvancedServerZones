package lol.arch.survival.sync;

import lol.arch.survival.LoadDistribution;
import lol.arch.survival.config.Config;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.UUID;

public class ChatSync extends JedisPubSub implements Listener {
    @EventHandler()
    private void chatMessageListener(AsyncPlayerChatEvent e) {
        e.setCancelled(true);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("uuid", e.getPlayer().getUniqueId().toString());
        jsonObject.put("message", e.getFormat().replace("%1$s", e.getPlayer().getName()).replace("%2$s", e.getMessage()));

        try (Jedis jedis = LoadDistribution.getPool().getResource()) {
            jedis.auth(Config.Redis.getPassword());
            jedis.publish("survival.chat-sync", jsonObject.toJSONString());
        }
    }

    @Override
    public void onMessage(String channel, String json) {
        if (!channel.equals("survival.chat-sync")) return;
        try {
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(json);

            Component component = Component.text(jsonObject.get("message").toString());
            Identity player = Identity.identity(UUID.fromString(jsonObject.get("uuid").toString()));

            Bukkit.getServer().sendMessage(player, component);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onSubscribe(String channel, int subscribedTo) {
        LoadDistribution.getConsole().info("PubSub subscribed to: " + channel);
    }
}
