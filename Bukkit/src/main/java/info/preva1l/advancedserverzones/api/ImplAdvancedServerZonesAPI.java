package info.preva1l.advancedserverzones.api;

import info.preva1l.advancedserverzones.AdvancedServerZonesAPI;
import info.preva1l.advancedserverzones.config.Config;
import info.preva1l.advancedserverzones.chat.ChatSyncMode;
import info.preva1l.advancedserverzones.config.Servers;
import info.preva1l.advancedserverzones.network.Broker;
import info.preva1l.advancedserverzones.network.Message;
import info.preva1l.advancedserverzones.network.Payload;
import net.kyori.adventure.text.Component;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.UUID;

@SuppressWarnings("unused")
public class ImplAdvancedServerZonesAPI extends AdvancedServerZonesAPI {
    @Override
    public boolean isChunkInBorder(Chunk chunk) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                Block block = chunk.getBlock(x, 0, z);
                if (isBlockInBorder(block)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isBlockInBorder(Block block) {
        return isLocationInBorder(block.getLocation());
    }

    @Override
    public boolean isLocationInBorder(Location loc) {
        Vector from = loc.toVector();
        if (Math.abs(new Vector(Servers.i().getBorder().centerX(), 0, Servers.i().getBorder().centerZ()).getBlockZ() - Config.i().getBorder().getSize() - from.getBlockZ()) < 48) {
            return true;
        }
        if (Math.abs(new Vector(Servers.i().getBorder().centerX(), 0, Servers.i().getBorder().centerZ()).getBlockZ() + Config.i().getBorder().getSize() - from.getBlockZ()) < 48) {
            return true;
        }
        if (Math.abs(new Vector(Servers.i().getBorder().centerX(), 0, Servers.i().getBorder().centerZ()).getBlockX() + Config.i().getBorder().getSize() - from.getBlockX()) < 48) {
            return true;
        }
        return Math.abs(new Vector(Servers.i().getBorder().centerX(), 0, Servers.i().getBorder().centerZ()).getBlockX() - Config.i().getBorder().getSize() - from.getBlockX()) < 48;
    }

    @Override
    public void sendChatSyncMessage(UUID sender, Component message) {
        if (!Config.i().getChatsync().isEnabled() || Config.i().getChatsync().getMode() != ChatSyncMode.API) {
            return;
        }

        Message.builder()
                .type(Message.Type.CHAT_MESSAGE)
                .payload(Payload.withChatMessage(sender, message))
                .build().send(Broker.i());
    }
}
