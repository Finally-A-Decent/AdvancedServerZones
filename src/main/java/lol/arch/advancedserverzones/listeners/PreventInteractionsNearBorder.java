package lol.arch.advancedserverzones.listeners;

import lol.arch.advancedserverzones.config.Config;
import lol.arch.advancedserverzones.config.Lang;
import lol.arch.advancedserverzones.config.Servers;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class PreventInteractionsNearBorder implements Listener {

    public boolean checkIfShouldPrevent(Player p, Location loc) {
        Vector from = loc.toVector();

        if (Math.abs(new Vector(Servers.BORDER_CENTER_X.toDouble(), 0, Servers.BORDER_CENTER_Z.toDouble()).getBlockZ() - Config.BORDER_SIZE.toInteger() - from.getBlockZ()) < 48) {
            p.sendMessage(Lang.CANNOT_INTERACT.toFormattedComponent());
            return true;
        }

        if (Math.abs(new Vector(Servers.BORDER_CENTER_X.toDouble(), 0, Servers.BORDER_CENTER_Z.toDouble()).getBlockX() + Config.BORDER_SIZE.toInteger() - from.getBlockX()) < 48) {
            p.sendMessage(Lang.CANNOT_INTERACT.toFormattedComponent());
            return true;
        }

        if (Math.abs(new Vector(Servers.BORDER_CENTER_X.toDouble(), 0, Servers.BORDER_CENTER_Z.toDouble()).getBlockZ() + Config.BORDER_SIZE.toInteger() - from.getBlockZ()) < 48) {
            p.sendMessage(Lang.CANNOT_INTERACT.toFormattedComponent());
            return true;
        }

        if (Math.abs(new Vector(Servers.BORDER_CENTER_X.toDouble(), 0, Servers.BORDER_CENTER_Z.toDouble()).getBlockX() - Config.BORDER_SIZE.toInteger() - from.getBlockX()) < 48) {
            p.sendMessage(Lang.CANNOT_INTERACT.toFormattedComponent());
            return true;
        }

        return false;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void blockBreakListener(BlockBreakEvent e) {
        e.setCancelled(checkIfShouldPrevent(e.getPlayer(), e.getBlock().getLocation()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void blockPlaceListener(BlockPlaceEvent e) {
        e.setCancelled(checkIfShouldPrevent(e.getPlayer(), e.getBlock().getLocation()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void interactListener(PlayerInteractEvent e) {
        if (e.getInteractionPoint() == null) {
            return;
        }
        e.setCancelled(checkIfShouldPrevent(e.getPlayer(), e.getInteractionPoint()));
    }
}
