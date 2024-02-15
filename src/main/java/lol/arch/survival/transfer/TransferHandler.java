package lol.arch.survival.transfer;

import lol.arch.survival.LoadDistribution;
import lol.arch.survival.util.TaskManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class TransferHandler implements Listener {
    @EventHandler
    public void playJoinEvent(PlayerJoinEvent e) {
        TaskManager.Async.runLater(LoadDistribution.getInstance(), () -> {
            if(ConnectionHandler.getPlayerToLocation(e.getPlayer()) == null) Bukkit.broadcastMessage("womp womp");
            String locStr = ConnectionHandler.getPlayerToLocation(e.getPlayer());
            Location loc = ConnectionHandler.fromLocationString(locStr);

            // Only adjust the Y if specified
            if (ConnectionHandler.checkIfAdjustY(locStr)) {
                loc.setY(loc.getWorld().getHighestBlockYAt(loc) + 2);
            }

            // Teleport the player
            TaskManager.Sync.run(LoadDistribution.getInstance(), ()-> e.getPlayer().teleport(loc));

            ConnectionHandler.clearTeleportKeyFromRedis(ConnectionHandler.getTeleportationToLocationKey(e.getPlayer()));
        }, 20L);
    }
}
