package info.preva1l.advancedserverzones.borders;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import info.preva1l.advancedserverzones.AdvancedServerZones;
import info.preva1l.advancedserverzones.network.Broker;
import info.preva1l.advancedserverzones.network.Message;
import info.preva1l.advancedserverzones.network.Payload;
import info.preva1l.advancedserverzones.network.types.TransferData;
import info.preva1l.trashcan.flavor.annotations.Configure;
import info.preva1l.trashcan.flavor.annotations.Service;
import io.papermc.paper.event.connection.configuration.AsyncPlayerConnectionConfigureEvent;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class ConnectionService implements Listener {
    public static final ConnectionService instance = new ConnectionService();
    private static final NamespacedKey COOKIE_KEY = new NamespacedKey("advancedserverzones", "transferring");

    private final Cache<UUID, TransferData> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(Duration.ofSeconds(30))
            .build();

    private final Cache<UUID, Boolean> transferred = CacheBuilder.newBuilder()
            .expireAfterWrite(Duration.ofSeconds(5))
            .build();

    @Configure
    public void configure() {
        Bukkit.getPluginManager().registerEvents(this, AdvancedServerZones.instance);
    }

    public void addData(TransferData data) {
        cache.put(data.player(), data);
    }

    /**
     * Send a player to a different server
     *
     * @param data the data of the transfer
     */
    @SneakyThrows
    public void transferServer(Player player, TransferData data) {
        if (transferred.asMap().containsKey(data.player())) return;
        transferred.put(data.player(), true);
        Message.builder()
                .type(Message.Type.TRANSFER)
                .payload(Payload.withTransferData(data))
                .build()
                .send(Broker.instance);

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

        if (player.getProtocolVersion() >= 766) {
            player.storeCookie(COOKIE_KEY, data.getNonce());
        }

        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("Connect");
        output.writeUTF(data.targetServer());
        Objects.requireNonNull(Bukkit.getPlayer(data.player())).sendPluginMessage(AdvancedServerZones.instance, "BungeeCord", output.toByteArray());
    }

    @Getter private final Map<UUID, Integer> ids = new HashMap<>();

    @EventHandler
    public void preConnect(AsyncPlayerConnectionConfigureEvent event) {
        byte[] data = null;
        try {
             data = event.getConnection().retrieveCookie(COOKIE_KEY).get(2, TimeUnit.SECONDS);
        } catch (Exception e) {
            // do nothing
        }
        if (data == null) return;

        int i = 0;
        while (!cache.asMap().containsKey(event.getConnection().getProfile().getId())) {
            if (i++ >= 100) break;
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @EventHandler
    public void playJoinEvent(PlayerSpawnLocationEvent e) {
        /* Seamless to be reworked
        CraftPlayer player = (CraftPlayer) e.getPlayer();
        ServerPlayer p = player.getHandle();
        p.setId(ids.get(p.getUUID()));
        e.getPlayer().showEntity(AdvancedServerZones.i(), e.getPlayer());
        e.getPlayer().showPlayer(AdvancedServerZones.i(), e.getPlayer());
        */
        TransferData data = cache.asMap().remove(e.getPlayer().getUniqueId());
        if (data == null) return;

        e.setSpawnLocation(data.position().predictedLocation(data.lastPing()));
    }
}