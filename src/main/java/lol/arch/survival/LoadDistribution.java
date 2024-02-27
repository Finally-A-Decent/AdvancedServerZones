package lol.arch.survival;

import lol.arch.survival.commands.ReloadCommand;
import lol.arch.survival.config.Config;
import lol.arch.survival.sync.ChatSync;
import lol.arch.survival.listeners.PreventInteractionsNearBorder;
import lol.arch.survival.transfer.BorderHandler;
import lol.arch.survival.transfer.ConnectionHandler;
import lol.arch.survival.util.TaskManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisAccessControlException;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.logging.Logger;

public final class LoadDistribution extends JavaPlugin {
    @Getter private static LoadDistribution instance;
    @Getter private static Logger console;
    @Getter private static JedisPool pool;
    private ChatSync chatSync;

    @Override
    public void onEnable() {
        instance = this;
        console = getLogger();

        saveDefaultConfig();
        reloadConfig();
        Config.reload();

        getServer().getPluginManager().registerEvents(new ConnectionHandler(), this);
        getServer().getPluginManager().registerEvents(new ChatSync(), this);
        getServer().getPluginManager().registerEvents(new PreventInteractionsNearBorder(), this);

        getCommand("zones-reload-config").setExecutor(new ReloadCommand());

        console.info("Setting Outgoing Plugin Channel to [BungeeCord]...");
        try {
            getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
            console.info("Outgoing Plugin Channel Set!");
        } catch (Exception e) {
            console.severe("Error Setting Plugin Channel: " + e);
        }

        TaskManager.Async.run(this, () -> {
            getConsole().info("Connecting to Redis Pool...");
            pool = new JedisPool(Config.Redis.getHost(), Config.Redis.getPort());
            pool.setMaxTotal(2);
            chatSync = new ChatSync();
            try (Jedis jedis = LoadDistribution.getPool().getResource()) {
                jedis.auth(Config.Redis.getPassword());
                jedis.ping();
                jedis.subscribe(chatSync, "survival.chat-sync");
            } catch (JedisConnectionException | JedisAccessControlException e) {
                getConsole().severe("REDIS DID NOT CONNECT: " + e.getMessage());
                getConsole().severe("Now stopping the server!");
                getPluginLoader().disablePlugin(this);
                return;
            }
            getConsole().info("Redis Connected Successfully!");
        });
        new BorderHandler();
        getConsole().info("Server-Core Loaded");
    }

    @Override
    public void onDisable() {
        chatSync.unsubscribe("survival.chat-sync");
        Bukkit.getScheduler().cancelTasks(this);
        getConsole().info("Server-Core Disabled");
    }
}
