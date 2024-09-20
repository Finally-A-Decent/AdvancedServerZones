package info.preva1l.advancedserverzones.transfer;

import com.google.gson.Gson;
import info.preva1l.advancedserverzones.AdvancedServerZones;
import info.preva1l.advancedserverzones.config.Config;
import info.preva1l.advancedserverzones.util.BungeeMessenger;
import info.preva1l.advancedserverzones.util.TaskManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;


public class ConnectionHandler implements Listener {

    /**
     * Send a player to a different server
     *
     * @param player   player to teleport
     * @param server   server to teleport to
     * @param location location
     */
    public static void transferServer(@NotNull Player player, @NotNull String server, Location location) {
        try (Jedis jedis = AdvancedServerZones.getInstance().getPool().getResource()) {
            jedis.set(getTeleportationToLocationKey(player), locationToString(location, player));
        }

        BungeeMessenger.connect(player, server);
    }

    public static String getTeleportationToLocationKey(Player player) {
        return "teleportation:location:" + player.getName();
    }

    public static String locationToString(Location location, Player player) {
        String worldName = location.getWorld().getName();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        float yaw = location.getYaw();
        float pitch = location.getPitch();
        boolean adjustY = !player.isFlying() && !player.isGliding() && !(player.getLocation().getY() <= 63);

        return new Gson().toJson(new String[]{worldName, String.valueOf(x), String.valueOf(y), String.valueOf(z), String.valueOf(yaw), String.valueOf(pitch), String.valueOf(adjustY)});
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

    public static boolean checkIfAdjustY(String locationString) {
        String[] parts = new Gson().fromJson(locationString, String[].class);
        return Boolean.parseBoolean(parts[6]);
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
        if (ConnectionHandler.getPlayerToLocation(e.getPlayer()) == null) return;
        String locStr = ConnectionHandler.getPlayerToLocation(e.getPlayer());
        Location loc = ConnectionHandler.fromLocationString(locStr);

        if (ConnectionHandler.checkIfAdjustY(locStr)) {
            loc.setY(loc.getWorld().getHighestBlockYAt(loc) + 2);
        }

        TaskManager.Sync.run(AdvancedServerZones.getInstance(), () -> e.getPlayer().teleport(loc));

        ConnectionHandler.clearTeleportKeyFromRedis(ConnectionHandler.getTeleportationToLocationKey(e.getPlayer()));
    }
}