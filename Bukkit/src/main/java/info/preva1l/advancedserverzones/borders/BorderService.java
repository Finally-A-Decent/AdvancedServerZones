package info.preva1l.advancedserverzones.borders;

import info.preva1l.advancedserverzones.AdvancedServerZones;
import info.preva1l.advancedserverzones.config.Config;
import info.preva1l.advancedserverzones.config.Lang;
import info.preva1l.advancedserverzones.config.Servers;
import info.preva1l.advancedserverzones.network.types.TransferData;
import info.preva1l.advancedserverzones.util.Text;
import info.preva1l.trashcan.flavor.annotations.Configure;
import info.preva1l.trashcan.flavor.annotations.Service;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Service
public class BorderService {
    public static final BorderService instance = new BorderService();

    @Configure
    public void configure() {
        Thread.ofPlatform()
                .name("AdvancedServerZones Border Ticker")
                .daemon(true)
                .start(() -> {
                    while (AdvancedServerZones.instance.isEnabled()) {
                        int size = Config.i().getBorder().getSize();
                        double centerX = Servers.i().getBorder().centerX();
                        double centerZ = Servers.i().getBorder().centerZ();
                        double north = centerZ - size;
                        double south = centerZ + size;
                        double east = centerX + size;
                        double west = centerX - size;

                        for (Player p : Bukkit.getOnlinePlayers()) {
                            BorderParticles.sendBorderParticles(p);

                            if (p.getLocation().getBlockZ() <= north - 0.5) {
                                if (Servers.i().getNorth() == null || Servers.i().getNorth().isEmpty()) {
                                    BorderDirection.NORTH.worldBorder(p);
                                    continue;
                                }
                                BorderDirection.NORTH.initTransfer(p);
                            }

                            if (p.getLocation().getBlockZ() >= south) {
                                if (Servers.i().getSouth() == null || Servers.i().getSouth().isEmpty()) {
                                    BorderDirection.SOUTH.worldBorder(p);
                                    continue;
                                }
                                BorderDirection.SOUTH.initTransfer(p);
                            }

                            if (p.getLocation().getBlockX() >= east) {
                                if (Servers.i().getEast() == null || Servers.i().getEast().isEmpty()) {
                                    BorderDirection.EAST.worldBorder(p);
                                    continue;
                                }
                                BorderDirection.EAST.initTransfer(p);
                            }

                            if (p.getLocation().getBlockX() <= west - .5) {
                                if (Servers.i().getWest() == null || Servers.i().getWest().isEmpty()) {
                                    BorderDirection.WEST.worldBorder(p);
                                    continue;
                                }
                                BorderDirection.WEST.initTransfer(p);
                            }
                        }

                        try {
                            TimeUnit.MILLISECONDS.sleep(250);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                });
    }

    public enum BorderDirection {
        NORTH {
            @Override
            public void initTransfer(Player p) {
                ConnectionService.instance.transferServer(
                        p,
                        new TransferData(
                                p.getUniqueId(),
                                Servers.i().getNorth(),
                                TransferData.Position.from(p.getLocation()),
                                p.getPing()
                        )
                );
            }

            @Override
            public void worldBorder(Player p) {
                Location loc = p.getLocation();
                loc.setZ((Servers.i().getBorder().centerZ() - Config.i().getBorder().getSize()) + 2);
                handleBorder(p, loc);
            }
        },
        SOUTH {
            @Override
            public void initTransfer(Player p) {
                ConnectionService.instance.transferServer(
                        p,
                        new TransferData(
                                p.getUniqueId(),
                                Servers.i().getSouth(),
                                TransferData.Position.from(p.getLocation()),
                                p.getPing()
                        )
                );
            }

            @Override
            public void worldBorder(Player p) {
                Location loc = p.getLocation();
                loc.setZ((Servers.i().getBorder().centerZ() + Config.i().getBorder().getSize()) - 2);
                handleBorder(p, loc);
            }
        },
        EAST {
            @Override
            public void initTransfer(Player p) {
                ConnectionService.instance.transferServer(
                        p,
                        new TransferData(
                                p.getUniqueId(),
                                Servers.i().getEast(),
                                TransferData.Position.from(p.getLocation()),
                                p.getPing()
                        )
                );
            }

            @Override
            public void worldBorder(Player p) {
                Location loc = p.getLocation();
                loc.setX((Servers.i().getBorder().centerX() + Config.i().getBorder().getSize()) - 2);
                handleBorder(p, loc);
            }
        },
        WEST {
            @Override
            public void initTransfer(Player p) {
                ConnectionService.instance.transferServer(
                        p,
                        new TransferData(
                                p.getUniqueId(),
                                Servers.i().getWest(),
                                TransferData.Position.from(p.getLocation()),
                                p.getPing()
                        )
                );
            }

            @Override
            public void worldBorder(Player p) {
                Location loc = p.getLocation();
                loc.setX((Servers.i().getBorder().centerX() - Config.i().getBorder().getSize()) + 2);
                handleBorder(p, loc);
            }
        };

        public abstract void initTransfer(Player p);

        public abstract void worldBorder(Player p);

        protected void handleBorder(Player p, Location tpLoc) {
            p.teleportAsync(tpLoc).thenRun(() -> {
                Title title = Title.title(Text.text(Lang.i().getBorder().title()), Text.text(Lang.i().getBorder().title()),
                        Title.Times.times(
                                Duration.of(50, ChronoUnit.MILLIS),
                                Duration.of(2, ChronoUnit.SECONDS),
                                Duration.of(50, ChronoUnit.MILLIS)));
                p.showTitle(title);
            });
        }
    }
}
