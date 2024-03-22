package lol.arch.advancedserverzones;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.UUID;

@SuppressWarnings("unused")
public interface AdvancedServerZonesAPI {

    /**
     * Check if a chunk is inside the region border
     * Does full scan on X and Z, Y is locked to 0
     * @param chunk the chunk to check
     * @return true or false if there is at least one block in the chunk that is in the border
     */
    boolean isChunkInBorder(Chunk chunk);

    /**
     * Check if a block is inside the region border
     * @param block the block to check
     * @return true or false whether it is the border or not
     */
    boolean isBlockInBorder(Block block);

    /**
     * Check if a location is inside the region border
     * @param loc the location to check
     * @return true or false whether it is the border or not
     */
    boolean isLocationInBorder(Location loc);

    /**
     * Send a chat message using chat sync, works no matter what mode is selected.
     * You are required to use this if the API mode is selected in the AdvancedServerZones config.
     * @param sender uuid of the sender of the message {@link ChatSyncSendEvent#getSender()}
     * @param message string of message {@link ChatSyncSendEvent#getMessage()}
     */
    void sendChatSyncMessage(UUID sender, String message);
}
