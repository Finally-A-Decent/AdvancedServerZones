package info.preva1l.advancedserverzones;

import lombok.Getter;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.ApiStatus;

import java.util.UUID;

@SuppressWarnings("unused")
public abstract class AdvancedServerZonesAPI {
    @Getter private static AdvancedServerZonesAPI instance;

    /**
     * Check if a chunk is inside the region border
     * Does full scan on X and Z, Y is locked to 0
     * @param chunk the chunk to check
     * @return true or false if there is at least one block in the chunk that is in the border
     */
    public abstract boolean isChunkInBorder(Chunk chunk);

    /**
     * Check if a block is inside the region border
     * @param block the block to check
     * @return true or false whether it is the border or not
     */
    public abstract boolean isBlockInBorder(Block block);

    /**
     * Check if a location is inside the region border
     * @param loc the location to check
     * @return true or false whether it is the border or not
     */
    public abstract boolean isLocationInBorder(Location loc);

    /**
     * Send a chat message using chat sync, works no matter what mode is selected.
     * You are required to use this if the API mode is selected in the AdvancedServerZones config.
     * @param sender uuid of the sender of the message {@link ChatSyncSendEvent#getSender()}
     * @param message string of message {@link ChatSyncSendEvent#getMessage()}
     */
    public abstract void sendChatSyncMessage(UUID sender, String message);

    @ApiStatus.Internal
    public static void setInstance(AdvancedServerZonesAPI newInstance) {
        if (instance != null) {
            throw new IllegalStateException("Instance has already been set!");
        }
        instance = newInstance;
    }
}
