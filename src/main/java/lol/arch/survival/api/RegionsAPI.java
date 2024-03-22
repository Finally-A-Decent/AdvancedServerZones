package lol.arch.survival.api;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;

public abstract class RegionsAPI {
    public abstract boolean isChunkInBorder(Chunk chunk);
    public abstract boolean isBlockInBorder(Block block);
    public abstract boolean isLocationInBorder(Location loc);
}
