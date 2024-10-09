package info.preva1l.advancedserverzones.transfer;

import com.google.gson.Gson;
import info.preva1l.advancedserverzones.AdvancedServerZones;
import info.preva1l.advancedserverzones.config.Config;
import info.preva1l.advancedserverzones.util.BungeeMessenger;
import info.preva1l.advancedserverzones.util.TaskManager;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.util.Vector;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class ConnectionHandler implements Listener {

    /**
     * Send a player to a different server
     *
     * @param player   player to teleport
     * @param server   server to teleport to
     * @param location location
     */
    public static void transferServer(@NotNull Player player, @NotNull String server, Location location, Vector vector) {
        try (Jedis jedis = AdvancedServerZones.getInstance().getPool().getResource()) {
            jedis.set(getTeleportationToLocationKey(player), locationToString(location, vector, player));
        }

        TaskManager.Sync.run(AdvancedServerZones.getInstance(), () -> {
            double viewRadius = player.getClientViewDistance() * 16;
            for (Entity entity : player.getNearbyEntities(viewRadius, viewRadius, viewRadius)) {
                if (entity.getUniqueId().equals(player.getUniqueId())) continue;
                player.hideEntity(AdvancedServerZones.getInstance(), entity);
            }

            List<BossBar> bars = new ArrayList<>();
            player.activeBossBars().forEach(bars::add);
            bars.forEach(player::hideBossBar);

            BungeeMessenger.connect(player, server);
        });
    }

    public static String getTeleportationToLocationKey(Player player) {
        return "teleportation:location:" + player.getName();
    }

    public static String locationToString(Location location, Vector vector, Player player) {
        String worldName = location.getWorld().getName();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        float yaw = location.getYaw();
        float pitch = location.getPitch();
        double vX = vector.getX();
        double vY = vector.getY();
        double vZ = vector.getZ();

        return new Gson().toJson(new String[]{worldName, String.valueOf(x), String.valueOf(y), String.valueOf(z),
                String.valueOf(yaw), String.valueOf(pitch), String.valueOf(vX), String.valueOf(vY), String.valueOf(vZ)});
    }

    public static Location fromLocationString(String locationString) {
        String[] parts = new Gson().fromJson(locationString, String[].class);

        String worldName = parts[0];
        double x = Double.parseDouble(parts[1]);
        double y = Double.parseDouble(parts[2]);
        double z = Double.parseDouble(parts[3]);
        float yaw = Float.parseFloat(parts[4]);
        float pitch = Float.parseFloat(parts[5]);

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            throw new IllegalArgumentException("World with name " + worldName + " not found");
        }

        return new Location(world, x, y, z, yaw, pitch);
    }

    public static Vector getVector(String locationString) {
        String[] parts = new Gson().fromJson(locationString, String[].class);
        double vX = Float.parseFloat(parts[6]);
        double vY = Float.parseFloat(parts[7]);
        double vZ = Float.parseFloat(parts[8]);

        return new Vector(vX, vY, vZ);
    }

    public static String getPlayerToLocation(Player player) {
        String toLocation;
        try (Jedis jedis = AdvancedServerZones.getInstance().getPool().getResource()) {
            toLocation = jedis.get(getTeleportationToLocationKey(player));
        }
        return toLocation;
    }

    public static void clearTeleportKeyFromRedis(String key) {
        try (Jedis jedis = AdvancedServerZones.getInstance().getPool().getResource()) {
            jedis.del(key);
        }
    }

    @EventHandler
    public void playJoinEvent(PlayerJoinEvent e) {
        e.getPlayer().showEntity(AdvancedServerZones.getInstance(), e.getPlayer());
        e.getPlayer().showPlayer(AdvancedServerZones.getInstance(), e.getPlayer());
        if (ConnectionHandler.getPlayerToLocation(e.getPlayer()) == null) return;
        String locStr = ConnectionHandler.getPlayerToLocation(e.getPlayer());
        Location loc = ConnectionHandler.fromLocationString(locStr);

        TaskManager.Sync.run(AdvancedServerZones.getInstance(), () -> {
            e.getPlayer().teleport(loc);
            e.getPlayer().setVelocity(getVector(locStr));
        });

        TaskManager.Sync.runLater(AdvancedServerZones.getInstance(), () -> {
            e.getPlayer().showEntity(AdvancedServerZones.getInstance(), e.getPlayer());
            e.getPlayer().showPlayer(AdvancedServerZones.getInstance(), e.getPlayer());
        }, 20L);

        ConnectionHandler.clearTeleportKeyFromRedis(ConnectionHandler.getTeleportationToLocationKey(e.getPlayer()));
    }
}