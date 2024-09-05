package info.preva1l.advancedserverzones.util;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import info.preva1l.advancedserverzones.AdvancedServerZones;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;

/**
 * Send bungee messages
 *
 * @author Preva1l
 */
@UtilityClass
public class BungeeMessenger {
    /**
     * Connects a player to another server on their current proxy.
     *
     * @param player Player to transfer
     * @param server Server to transfer to
     */
    public void connect(Player player, String server) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("Connect");
        output.writeUTF(server);
        player.sendPluginMessage(AdvancedServerZones.getInstance(), "BungeeCord", output.toByteArray());
    }
}
