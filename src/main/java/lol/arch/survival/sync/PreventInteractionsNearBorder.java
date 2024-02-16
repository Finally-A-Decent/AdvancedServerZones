package lol.arch.survival.sync;

import lol.arch.survival.config.Config;
import lol.arch.survival.util.StringUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.util.Vector;

public class PreventInteractionsNearBorder implements Listener {

    public boolean checkIfShouldPrevent(Player p, Location loc) {
        Vector from = loc.toVector();

        if (Math.abs(new Vector(Config.getBorderCenterX(), 0, Config.getBorderCenterZ()).getBlockZ() - Config.getBorderSize() - from.getBlockZ()) < 48) {
            p.sendMessage(StringUtils.colorize("&7[&4!&7] &cYou cannot interact within &f3 chunks &cof the region border!"));
            return true;
        }

        if (Math.abs(new Vector(Config.getBorderCenterX(), 0, Config.getBorderCenterZ()).getBlockX() + Config.getBorderSize() - from.getBlockX()) < 48) {
            p.sendMessage(StringUtils.colorize("&7[&4!&7] &cYou cannot interact within &f3 chunks &cof the region border!"));
            return true;
        }

        if (Math.abs(new Vector(Config.getBorderCenterX(), 0, Config.getBorderCenterZ()).getBlockZ() + Config.getBorderSize() - from.getBlockZ()) < 48) {
            p.sendMessage(StringUtils.colorize("&7[&4!&7] &cYou cannot interact within &f3 chunks &cof the region border!"));
            return true;
        }

        if (Math.abs(new Vector(Config.getBorderCenterX(), 0, Config.getBorderCenterZ()).getBlockX() - Config.getBorderSize() - from.getBlockX()) < 48) {
            p.sendMessage(StringUtils.colorize("&7[&4!&7] &cYou cannot interact within &f3 chunks &cof the region border!"));
            return true;
        }

        return false;
    }

    @EventHandler
    public void blockBreakListener(BlockBreakEvent e) {
        e.setCancelled(checkIfShouldPrevent(e.getPlayer(), e.getBlock().getLocation()));
    }

    @EventHandler
    public void blockPlaceListener(BlockPlaceEvent e) {
        e.setCancelled(checkIfShouldPrevent(e.getPlayer(), e.getBlock().getLocation()));
    }

    @EventHandler
    public void blockInteractListener(BlockBreakEvent e) {
        e.setCancelled(checkIfShouldPrevent(e.getPlayer(), e.getBlock().getLocation()));
    }
}
