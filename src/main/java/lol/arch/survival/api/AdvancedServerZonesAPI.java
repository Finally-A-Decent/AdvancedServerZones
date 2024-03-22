package lol.arch.survival.api;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;

@SuppressWarnings("unused")
public interface AdvancedServerZonesAPI {
    boolean isChunkInBorder(Chunk chunk);
    boolean isBlockInBorder(Block block);
    boolean isLocationInBorder(Location loc);
}
