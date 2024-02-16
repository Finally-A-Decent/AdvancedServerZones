package lol.arch.survival.transfer;

import com.google.gson.Gson;
import lol.arch.survival.LoadDistribution;
import lol.arch.survival.util.BungeeMessenger;
import lol.arch.survival.util.TaskManager;
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

    @EventHandler
    public void playJoinEvent(PlayerJoinEvent e) {
        if (ConnectionHandler.getPlayerToLocation(e.getPlayer()) == null) return;
        String locStr = ConnectionHandler.getPlayerToLocation(e.getPlayer());
        Location loc = ConnectionHandler.fromLocationString(locStr);

        // Only adjust the Y if specified
        if (ConnectionHandler.checkIfAdjustY(locStr)) {
            loc.setY(loc.getWorld().getHighestBlockYAt(loc) + 2);
        }

        // Teleport the player
        TaskManager.Sync.run(LoadDistribution.getInstance(), ()-> e.getPlayer().teleport(loc));

        ConnectionHandler.clearTeleportKeyFromRedis(ConnectionHandler.getTeleportationToLocationKey(e.getPlayer()));
    }

    /**
     * Send a player to a different server
     * @param player player to teleport
     * @param server server to teleport to
     * @param location location
     */
    public static void transferServer(@NotNull Player player, @NotNull String server, Location location) {
        // Adding location to redis
        try (Jedis jedis = LoadDistribution.getPool().getResource()) {
            jedis.auth(LoadDistribution.getInstance().getConfig().getString("redis.password"));
            jedis.set(getTeleportationToLocationKey(player), locationToString(location, player));
        }

        BungeeMessenger.connect(player, server);
    }

    public static String getTeleportationToLocationKey(Player player) {
        return "teleportation:location:" + player.getName();
    }

    public static String locationToString(Location location, Player player) {
        // Create an intermediary object to hold the location's data
        String worldName = location.getWorld().getName();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        float yaw = location.getYaw();
        float pitch = location.getPitch();
        boolean adjustY = !player.isFlying() && !player.isGliding() && !(player.getLocation().getY() <= 63);

        // Create a simple JSON string from the intermediary object
        return new Gson().toJson(new String[]{worldName, String.valueOf(x), String.valueOf(y), String.valueOf(z), String.valueOf(yaw), String.valueOf(pitch), String.valueOf(adjustY)});
    }

    // Deserializes the JSON string back to a Location object
    public static Location fromLocationString(String locationString) {
        // Parse the JSON string back into an array of strings
        String[] parts = new Gson().fromJson(locationString, String[].class);

        // Extract the data from the parsed array
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

        // Create a new Location object with the extracted data
        return new Location(world, x, y, z, yaw, pitch);
    }

    public static boolean checkIfAdjustY(String locationString) {
        String[] parts = new Gson().fromJson(locationString, String[].class);
        return Boolean.parseBoolean(parts[6]);
    }

    public static String getPlayerToLocation(Player player) {
        String toLocation;
        try (Jedis jedis = LoadDistribution.getPool().getResource()) {
            jedis.auth(LoadDistribution.getInstance().getConfig().getString("redis.password"));
            toLocation = jedis.get(getTeleportationToLocationKey(player));
        }
        return toLocation;
    }

    public static void clearTeleportKeyFromRedis(String key) {
        try (Jedis jedis = LoadDistribution.getPool().getResource()) {
            jedis.auth(LoadDistribution.getInstance().getConfig().getString("redis.password"));
            jedis.del(key);
        }
    }
}