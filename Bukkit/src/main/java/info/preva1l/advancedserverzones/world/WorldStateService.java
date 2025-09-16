package info.preva1l.advancedserverzones.world;

import info.preva1l.advancedserverzones.AdvancedServerZones;
import info.preva1l.advancedserverzones.network.Broker;
import info.preva1l.advancedserverzones.network.Message;
import info.preva1l.advancedserverzones.network.Payload;
import info.preva1l.trashcan.flavor.annotations.Configure;
import info.preva1l.trashcan.flavor.annotations.Service;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * This is a class.
 *
 * @author Preva1l
 * @since 16/09/2025
 */
@Service
public final class WorldStateService {
    @Configure
    public void configure() {
        Thread.ofPlatform()
                .name("ASZ World State Updater")
                .daemon(true)
                .start(() -> {
                    while (AdvancedServerZones.instance.isEnabled()) {
                        try {
                            for (World world : Bukkit.getWorlds()) {
                                Message.builder()
                                        .type(Message.Type.WORLD_STATE)
                                        .payload(Payload.withWorldState(new WorldState(
                                                world.getName(),
                                                world.getFullTime(),
                                                world.hasStorm(),
                                                world.getWeatherDuration(),
                                                world.isThundering(),
                                                world.getThunderDuration(),
                                                world.getClearWeatherDuration())))
                                        .build().send(Broker.instance);
                            }
                        } catch (Exception ex) {
                            AdvancedServerZones.instance.getLogger()
                                    .log(Level.WARNING, "ASZ World State Updater threw an exception", ex);
                        }

                        try {
                            TimeUnit.SECONDS.sleep(5);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                });
    }
}
