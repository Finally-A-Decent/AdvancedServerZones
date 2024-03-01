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
            double size = Config.getBorderSize();
            double centerX = Config.getBorderCenterX();
            double centerZ = Config.getBorderCenterZ();
            double north = centerZ - size;
            double south = centerZ + size;
            double east = centerX + size;
            double west = centerX - size;
            for (Player p : Bukkit.getOnlinePlayers()) {
                // Particles
                new BorderParticles().sendBorderParticles(p);

                if (p.getLocation().getBlockZ() <= north - 0.5) {
                    if (Config.Servers.getNorth() == null || Config.Servers.getNorth().isEmpty()) {
                        BorderDirection.NORTH.worldBorder(p);
                        continue;
                    }
                    BorderDirection.NORTH.initTransfer(p);
                }

                if (p.getLocation().getBlockZ() >= south) {
                    if (Config.Servers.getSouth() == null || Config.Servers.getSouth().isEmpty()) {
                        BorderDirection.SOUTH.worldBorder(p);
                        continue;
                    }
                    BorderDirection.SOUTH.initTransfer(p);
                }
                if (p.getLocation().getBlockX() >= east) {
                    if (Config.Servers.getEast() == null || Config.Servers.getEast().isEmpty()) {
                        BorderDirection.EAST.worldBorder(p);
                        continue;
                    }
                    BorderDirection.EAST.initTransfer(p);
                }

                if (p.getLocation().getBlockX() <= west - .5) {
                    if (Config.Servers.getWest() == null || Config.Servers.getWest().isEmpty()) {
                        BorderDirection.WEST.worldBorder(p);
                        continue;
                    }
                    BorderDirection.WEST.initTransfer(p);
                }
            }
        }, 5L);
    }

    public enum BorderDirection {
        NORTH {
            @Override
            public void initTransfer(Player p) {
                Location loc = p.getLocation();
                ConnectionHandler.transferServer(p, Config.Servers.getNorth(), loc);
            }

            @Override
            public void worldBorder(Player p) {
                Location loc = p.getLocation();
                loc.setZ((Config.getBorderCenterZ() - Config.getBorderSize()) + 2);
                TaskManager.Sync.run(LoadDistribution.getInstance(), () -> p.teleport(loc));
                p.sendTitle(StringUtils.colorize(Config.Messages.getTitle()),
                        StringUtils.colorize(Config.Messages.getSubtitle()),
                        1, 40, 1);
            }
        },
        SOUTH {
            @Override
            public void initTransfer(Player p) {
                Location loc = p.getLocation();
                ConnectionHandler.transferServer(p, Config.Servers.getSouth(), loc);
            }

            @Override
            public void worldBorder(Player p) {
                Location loc = p.getLocation();
                loc.setZ((Config.getBorderCenterZ() + Config.getBorderSize()) - 2);
                TaskManager.Sync.run(LoadDistribution.getInstance(), () -> p.teleport(loc));
                p.sendTitle(StringUtils.colorize(Config.Messages.getTitle()),
                        StringUtils.colorize(Config.Messages.getSubtitle()),
                        1, 40, 1);
            }
        },
        EAST {
            @Override
            public void initTransfer(Player p) {
                Location loc = p.getLocation();
                ConnectionHandler.transferServer(p, Config.Servers.getEast(), loc);
            }

            @Override
            public void worldBorder(Player p) {
                Location loc = p.getLocation();
                loc.setX((Config.getBorderCenterX() + Config.getBorderSize()) - 2);
                TaskManager.Sync.run(LoadDistribution.getInstance(), () -> p.teleport(loc));
                p.sendTitle(StringUtils.colorize(Config.Messages.getTitle()),
                        StringUtils.colorize(Config.Messages.getSubtitle()),
                        1, 40, 1);
            }
        },
        WEST {
            @Override
            public void initTransfer(Player p) {
                Location loc = p.getLocation();
                ConnectionHandler.transferServer(p, Config.Servers.getWest(), loc);
            }

            @Override
            public void worldBorder(Player p) {
                Location loc = p.getLocation();
                loc.setX((Config.getBorderCenterX() - Config.getBorderSize()) + 2);
                TaskManager.Sync.run(LoadDistribution.getInstance(), () -> p.teleport(loc));
                p.sendTitle(StringUtils.colorize(Config.Messages.getTitle()),
                        StringUtils.colorize(Config.Messages.getSubtitle()),
                        1, 40, 1);
            }
        };

        public abstract void initTransfer(Player p);

        public abstract void worldBorder(Player p);
    }

}
