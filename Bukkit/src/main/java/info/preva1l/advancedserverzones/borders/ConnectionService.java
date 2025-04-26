package info.preva1l.advancedserverzones.borders;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import info.preva1l.advancedserverzones.AdvancedServerZones;
import info.preva1l.advancedserverzones.network.RedisBroker;
import info.preva1l.advancedserverzones.network.types.TransferData;
import info.preva1l.advancedserverzones.util.TaskManager;
import info.preva1l.trashcan.flavor.annotations.Configure;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.options.LocalCachedMapOptions;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.time.Duration;
import java.util.*;

public class ConnectionService implements Listener {
    public static final ConnectionService instance = new ConnectionService();

    private RLocalCachedMap<UUID, TransferData> transferData;

    @Configure
    public void configure() {
        transferData = RedisBroker.getRedisson().getLocalCachedMap(
                LocalCachedMapOptions.<UUID, TransferData>name("transfer-data")
                        .cacheSize(100000)
                        .maxIdle(Duration.ofSeconds(60))
                        .timeToLive(Duration.ofSeconds(60))
                        .evictionPolicy(LocalCachedMapOptions.EvictionPolicy.WEAK)
                        .syncStrategy(LocalCachedMapOptions.SyncStrategy.INVALIDATE)
                        .expirationEventPolicy(LocalCachedMapOptions.ExpirationEventPolicy.SUBSCRIBE_WITH_KEYSPACE_CHANNEL)
        );
    }

    /**
     * Send a player to a different server
     *
     * @param data the data of the transfer
     */
    public void transferServer(TransferData data) {
        transferData.fastPutAsync(data.player(), data);

        /* Seamless to be reworked
        double viewRadius = player.getClientViewDistance() * 16;
        for (Entity entity : player.getNearbyEntities(viewRadius, viewRadius, viewRadius)) {
            if (entity.getUniqueId().equals(player.getUniqueId())) continue;
            player.hideEntity(AdvancedServerZones.i(), entity);
        }

        List<BossBar> bars = new ArrayList<>();
        player.boss().forEach(bars::add);
        bars.forEach(player::hideBossBar);
        */

        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("Connect");
        output.writeUTF(data.targetServer());
        Objects.requireNonNull(Bukkit.getPlayer(data.player())).sendPluginMessage(AdvancedServerZones.i(), "BungeeCord", output.toByteArray());
    }

    @Getter private final Map<UUID, Integer> ids = new HashMap<>();

    @EventHandler
    public void playJoinEvent(PlayerSpawnLocationEvent e) {
        /* Seamless to be reworked
        CraftPlayer player = (CraftPlayer) e.getPlayer();
        ServerPlayer p = player.getHandle();
        p.setId(ids.get(p.getUUID()));
        e.getPlayer().showEntity(AdvancedServerZones.i(), e.getPlayer());
        e.getPlayer().showPlayer(AdvancedServerZones.i(), e.getPlayer());
        */
        transferData.removeAsync(e.getPlayer().getUniqueId()).thenAccept(transferData -> {
            if (transferData == null) return;
            TaskManager.Sync.run(AdvancedServerZones.i(),
                    () -> e.setSpawnLocation(transferData.position().predictedLocation(transferData.lastPing())));
        });
    }
}