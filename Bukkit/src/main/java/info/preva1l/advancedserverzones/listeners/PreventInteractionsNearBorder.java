package info.preva1l.advancedserverzones.listeners;

import info.preva1l.advancedserverzones.config.Config;
import info.preva1l.advancedserverzones.config.Lang;
import info.preva1l.advancedserverzones.config.Servers;
import info.preva1l.advancedserverzones.util.Text;
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

        if (Math.abs(new Vector(Servers.i().getBorder().centerX(),
                0, Servers.i().getBorder().centerZ()).getBlockZ() - Config.i().getBorder().getSize() - from.getBlockZ()) < 49) {
            p.sendMessage(Text.text(Lang.i().getCannotInteract()));
            return true;
        }

        if (Math.abs(new Vector(Servers.i().getBorder().centerX(),
                0, Servers.i().getBorder().centerZ()).getBlockX() + Config.i().getBorder().getSize() - from.getBlockX()) < 49) {
            p.sendMessage(Text.text(Lang.i().getCannotInteract()));
            return true;
        }

        if (Math.abs(new Vector(Servers.i().getBorder().centerX(),
                0, Servers.i().getBorder().centerZ()).getBlockZ() + Config.i().getBorder().getSize() - from.getBlockZ()) < 49) {
            p.sendMessage(Text.text(Lang.i().getCannotInteract()));
            return true;
        }

        if (Math.abs(new Vector(Servers.i().getBorder().centerX(),
                0, Servers.i().getBorder().centerZ()).getBlockX() - Config.i().getBorder().getSize() - from.getBlockX()) < 49) {
            p.sendMessage(Text.text(Lang.i().getCannotInteract()));
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
