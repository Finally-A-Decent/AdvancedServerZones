package info.preva1l.advancedserverzones.transfer;

import info.preva1l.advancedserverzones.AdvancedServerZones;
import info.preva1l.advancedserverzones.config.Config;
import info.preva1l.advancedserverzones.config.Lang;
import info.preva1l.advancedserverzones.config.Servers;
import info.preva1l.advancedserverzones.util.TaskManager;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class BorderHandler {
    public BorderHandler() {
        TaskManager.Async.runTask(AdvancedServerZones.getInstance(), () -> {
            int size = Config.BORDER_SIZE.toInteger();
            double centerX = Servers.BORDER_CENTER_X.toDouble();
            double centerZ = Servers.BORDER_CENTER_Z.toDouble();
            double north = centerZ - size;
            double south = centerZ + size;
            double east = centerX + size;
            double west = centerX - size;

            for (Player p : Bukkit.getOnlinePlayers()) {
                new BorderParticles().sendBorderParticles(p);

                if (p.getLocation().getBlockZ() <= north - 0.5) {
                    if (Servers.NORTH_SERVER.toString() == null || Servers.NORTH_SERVER.toString().isEmpty()) {
                        BorderDirection.NORTH.worldBorder(p);
                        continue;
                    }
                    BorderDirection.NORTH.initTransfer(p);
                }

                if (p.getLocation().getBlockZ() >= south) {
                    if (Servers.SOUTH_SERVER.toString() == null || Servers.SOUTH_SERVER.toString().isEmpty()) {
                        BorderDirection.SOUTH.worldBorder(p);
                        continue;
                    }
                    BorderDirection.SOUTH.initTransfer(p);
                }
                if (p.getLocation().getBlockX() >= east) {
                    if (Servers.EAST_SERVER.toString() == null || Servers.EAST_SERVER.toString().isEmpty()) {
                        BorderDirection.EAST.worldBorder(p);
                        continue;
                    }
                    BorderDirection.EAST.initTransfer(p);
                }

                if (p.getLocation().getBlockX() <= west - .5) {
                    if (Servers.WEST_SERVER.toString() == null || Servers.WEST_SERVER.toString().isEmpty()) {
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
                ConnectionHandler.transferServer(p, Servers.NORTH_SERVER.toString(), loc, p.getVelocity());
            }

            @Override
            public void worldBorder(Player p) {
                Location loc = p.getLocation();
                loc.setZ((Servers.BORDER_CENTER_Z.toDouble() - Config.BORDER_SIZE.toInteger()) + 2);
                handleBorder(p, loc);
            }
        },
        SOUTH {
            @Override
            public void initTransfer(Player p) {
                Location loc = p.getLocation();
                ConnectionHandler.transferServer(p, Servers.SOUTH_SERVER.toString(), loc, p.getVelocity());
            }

            @Override
            public void worldBorder(Player p) {
                Location loc = p.getLocation();
                loc.setZ((Servers.BORDER_CENTER_Z.toDouble() + Config.BORDER_SIZE.toInteger()) - 2);
                handleBorder(p, loc);
            }
        },
        EAST {
            @Override
            public void initTransfer(Player p) {
                Location loc = p.getLocation();
                ConnectionHandler.transferServer(p, Servers.EAST_SERVER.toString(), loc, p.getVelocity());
            }

            @Override
            public void worldBorder(Player p) {
                Location loc = p.getLocation();
                loc.setX((Servers.BORDER_CENTER_X.toDouble() + Config.BORDER_SIZE.toInteger()) - 2);
                handleBorder(p, loc);
            }
        },
        WEST {
            @Override
            public void initTransfer(Player p) {
                Location loc = p.getLocation();
                ConnectionHandler.transferServer(p, Servers.WEST_SERVER.toString(), loc, p.getVelocity());
            }

            @Override
            public void worldBorder(Player p) {
                Location loc = p.getLocation();
                loc.setX((Servers.BORDER_CENTER_X.toDouble() - Config.BORDER_SIZE.toInteger()) + 2);
                handleBorder(p, loc);
            }
        };

        public abstract void initTransfer(Player p);
        public abstract void worldBorder(Player p);

        protected void handleBorder(Player p, Location tpLoc) {
            p.teleportAsync(tpLoc).thenRun(() -> {
                Title title = Title.title(Lang.BORDER_TITLE.toFormattedComponent(), Lang.BORDER_SUBTITLE.toFormattedComponent(),
                        Title.Times.times(
                                Duration.of(50, ChronoUnit.MILLIS),
                                Duration.of(2, ChronoUnit.SECONDS),
                                Duration.of(50, ChronoUnit.MILLIS)));
                p.showTitle(title);
            });
        }
    }
}
