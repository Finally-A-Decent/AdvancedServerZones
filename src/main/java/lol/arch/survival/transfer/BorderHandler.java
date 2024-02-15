package lol.arch.survival.transfer;

import lol.arch.survival.LoadDistribution;
import lol.arch.survival.config.Config;
import lol.arch.survival.util.StringUtils;
import lol.arch.survival.util.TaskManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class BorderHandler {
    public BorderHandler() {

        TaskManager.Async.runTask(LoadDistribution.getInstance(), () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                // Particles
                new BorderParticles().sendBorderParticles(p);

                // Server Transfer
                double size = Config.getBorderSize();
                double centerX = Config.getBorderCenterX();
                double centerZ = Config.getBorderCenterZ();
                double north = centerZ - size;
                double south = centerZ + size;
                double east = centerX + size;
                double west = centerX - size;
                Location loc = p.getLocation();
                if (p.getLocation().getBlockX() >= east) {
                    if (Config.Servers.getEast() == null || Config.Servers.getEast().isEmpty()) {
                        loc.setX(east - 2);
                        TaskManager.Sync.run(LoadDistribution.getInstance(), () -> p.teleport(loc));
                        p.sendTitle(StringUtils.colorize("&cYou have reached the world border!"), StringUtils.colorize("&7Try exploring a different direction!"), 1, 40, 1);
                        continue;
                    }
                    loc.setX(east + 2);
                    ConnectionHandler.transferServer(p, Config.Servers.getEast(), loc);;
                }
                if (p.getLocation().getBlockX() <= west - .5) {
                    if (Config.Servers.getWest() == null || Config.Servers.getWest().isEmpty()) {
                        loc.setX(west + 2);
                        TaskManager.Sync.run(LoadDistribution.getInstance(), () -> p.teleport(loc));
                        p.sendTitle(StringUtils.colorize("&cYou have reached the world border!"), StringUtils.colorize("&7Try exploring a different direction!"), 1, 40, 1);
                        continue;
                    }
                    loc.setX(west - 2);
                    ConnectionHandler.transferServer(p, Config.Servers.getWest(), loc);;
                }
                if (p.getLocation().getBlockZ() >= south) {
                    if (Config.Servers.getSouth() == null || Config.Servers.getSouth().isEmpty()) {
                        loc.setZ(south - 2);
                        TaskManager.Sync.run(LoadDistribution.getInstance(), () -> p.teleport(loc));
                        p.sendTitle(StringUtils.colorize("&cYou have reached the world border!"), StringUtils.colorize("&7Try exploring a different direction!"), 1, 40, 1);
                        continue;
                    }
                    loc.setZ(south + 2);
                    ConnectionHandler.transferServer(p, Config.Servers.getSouth(), loc);;
                }
                if (p.getLocation().getBlockZ() <= north) {
                    if (Config.Servers.getNorth() == null || Config.Servers.getNorth().isEmpty()) {
                        loc.setZ(north + 2);
                        TaskManager.Sync.run(LoadDistribution.getInstance(), () -> p.teleport(loc));
                        p.sendTitle(StringUtils.colorize("&cYou have reached the world border!"), StringUtils.colorize("&7Try exploring a different direction!"), 1, 40, 1);
                        continue;
                    }
                    loc.setZ(north - 2);
                    ConnectionHandler.transferServer(p, Config.Servers.getNorth(), loc);;
                }
            }
        }, 5L);
    }
}
