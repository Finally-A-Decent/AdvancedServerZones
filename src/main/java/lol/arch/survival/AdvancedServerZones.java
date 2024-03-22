package lol.arch.survival;

import lol.arch.survival.api.AdvancedServerZonesAPI;
import lol.arch.survival.api.impl.ImplAdvancedServerZonesAPI;
import lol.arch.survival.commands.ReloadCommand;
import lol.arch.survival.config.Config;
import lol.arch.survival.config.Lang;
import lol.arch.survival.config.Servers;
import lol.arch.survival.sync.ChatSync;
import lol.arch.survival.listeners.PreventInteractionsNearBorder;
import lol.arch.survival.sync.PlaceholderManager;
import lol.arch.survival.transfer.BorderHandler;
import lol.arch.survival.transfer.ConnectionHandler;
import lol.arch.survival.util.BasicConfig;
import lol.arch.survival.util.TaskManager;
import lombok.Getter;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisAccessControlException;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.Objects;
import java.util.logging.Logger;

public final class AdvancedServerZones extends JavaPlugin {
    @Getter private static AdvancedServerZones instance;

    @Getter private Logger console;
    @Getter private JedisPool pool;
    @Getter private ChatSync chatSync;

    @Getter private BasicConfig configFile;
    @Getter private BasicConfig serversFile;
    @Getter private BasicConfig langFile;

    @Getter private static AdvancedServerZonesAPI API;

    @Override
    public void onEnable() {
        instance = this;
        console = getLogger();

        configFile = new BasicConfig(this, "config.yml");
        serversFile = new BasicConfig(this, "servers.yml");
        langFile = new BasicConfig(this, "lang.yml");
        Config.loadDefault();
        Servers.loadDefault();
        Lang.loadDefault();

        getServer().getPluginManager().registerEvents(new ConnectionHandler(), this);
        getServer().getPluginManager().registerEvents(new PreventInteractionsNearBorder(), this);

        Objects.requireNonNull(getCommand("asz-reload-config")).setExecutor(new ReloadCommand());

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        if (Config.CHAT_SYNC.toBoolean()) {
            chatSync = new ChatSync();
            getServer().getPluginManager().registerEvents(chatSync, this);

            if (getServer().getPluginManager().getPlugin("Vault") != null) {
                RegisteredServiceProvider<Chat> registration = getServer().getServicesManager().getRegistration(Chat.class);
                if (registration != null) PlaceholderManager.setChat(registration.getProvider());
            }
        }

        TaskManager.Async.run(this, () -> {
            getConsole().info("Connecting to Redis Pool...");
            pool = new JedisPool(Config.REDIS_HOST.toString(), Config.REDIS_PORT.toInteger());
            pool.setMaxTotal(2);
            try (Jedis jedis = AdvancedServerZones.getInstance().getPool().getResource()) {
                jedis.auth(Config.REDIS_PASSWORD.toString());
                jedis.ping();
                if (Config.CHAT_SYNC.toBoolean()) jedis.subscribe(chatSync, "asz.chat-sync");
            } catch (JedisConnectionException | JedisAccessControlException e) {
                getConsole().severe("REDIS DID NOT CONNECT: " + e.getMessage());
                getConsole().severe("Now stopping the server!");
                Bukkit.shutdown();
                return;
            }
            getConsole().info("Redis Connected Successfully!");
        });
        new BorderHandler();

        API = new ImplAdvancedServerZonesAPI();
        getConsole().info("Advanced Server Zones Loaded");
    }

    @Override
    public void onDisable() {
        if (chatSync != null && chatSync.isSubscribed()) chatSync.unsubscribe("asz.chat-sync");
        Bukkit.getScheduler().cancelTasks(this);
        getConsole().info("Advanced Server Zones Disabled");
    }
}
