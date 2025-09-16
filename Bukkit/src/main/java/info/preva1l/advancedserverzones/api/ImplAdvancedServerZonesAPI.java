package info.preva1l.advancedserverzones.api;

import info.preva1l.advancedserverzones.AdvancedServerZonesAPI;
import info.preva1l.advancedserverzones.chat.ChatSyncService;
import info.preva1l.advancedserverzones.config.Config;
import info.preva1l.advancedserverzones.config.Servers;
import info.preva1l.advancedserverzones.network.Broker;
import info.preva1l.advancedserverzones.network.Message;
import info.preva1l.advancedserverzones.network.Payload;
import net.kyori.adventure.text.Component;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.UUID;

public final class ImplAdvancedServerZonesAPI extends AdvancedServerZonesAPI {
    @Override
    public boolean isChunkNearBorder(Chunk chunk) {
        int size = Config.i().getBorder().getSize();
        int interactionRadius = Config.i().getBorder().getInteractionRadius() + 1;
        int centerZ = Servers.i().getBorder().flooredCenterZ();
        int centerX = Servers.i().getBorder().flooredCenterX();
        int chunkWorldX = chunk.getX() >> 4;
        int chunkWorldZ = chunk.getZ() >> 4;

        for (int x = 0; x < 16; x++) {
            int worldX = chunkWorldX + x;
            if (Math.abs(centerX + size - worldX) < interactionRadius) return true;
            if (Math.abs(centerX - size - worldX) < interactionRadius) return true;
        }

        for (int z = 0; z < 16; z++) {
            int worldZ = chunkWorldZ + z;
            if (Math.abs(centerZ - size - worldZ) < interactionRadius) return true;
            if (Math.abs(centerZ + size - worldZ) < interactionRadius) return true;
        }

        return false;
    }

    @Override
    public boolean isBlockNearBorder(Block block) {
        return isLocationNearBorder(block.getLocation());
    }

    @Override
    public boolean isLocationNearBorder(Location loc) {
        int locZ = loc.getBlockZ();
        int locX = loc.getBlockX();
        int size = Config.i().getBorder().getSize();
        int interactionRadius = Config.i().getBorder().getInteractionRadius() + 1;
        int centerZ = Servers.i().getBorder().flooredCenterZ();
        int centerX = Servers.i().getBorder().flooredCenterX();

        if (Math.abs(centerZ - size - locZ) < interactionRadius) return true;
        if (Math.abs(centerZ + size - locZ) < interactionRadius) return true;
        if (Math.abs(centerX + size - locX) < interactionRadius) return true;
        return Math.abs(centerX - size - locX) < interactionRadius;
    }

    @Override
    public void sendChatSyncMessage(UUID sender, Component message) {
        if (!Config.i().getChatsync().isEnabled() ||
                Config.i().getChatsync().getMode() != ChatSyncService.ChatSyncMode.API) return;

        Message.builder()
                .type(Message.Type.CHAT_MESSAGE)
                .payload(Payload.withChatMessage(sender, message))
                .build().send(Broker.instance);
    }
}
