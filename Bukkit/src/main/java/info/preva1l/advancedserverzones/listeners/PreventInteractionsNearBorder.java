package info.preva1l.advancedserverzones.listeners;

import info.preva1l.advancedserverzones.AdvancedServerZonesAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class PreventInteractionsNearBorder implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void blockBreakListener(BlockBreakEvent e) {
        e.setCancelled(AdvancedServerZonesAPI.getInstance().isBlockInBorder(e.getBlock()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void blockPlaceListener(BlockPlaceEvent e) {
        e.setCancelled(AdvancedServerZonesAPI.getInstance().isBlockInBorder(e.getBlock()));
    }
}
