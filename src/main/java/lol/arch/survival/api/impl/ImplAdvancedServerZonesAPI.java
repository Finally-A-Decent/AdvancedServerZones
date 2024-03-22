package lol.arch.survival.api.impl;

import lol.arch.survival.api.AdvancedServerZonesAPI;
import lol.arch.survival.config.Config;
import lol.arch.survival.config.Servers;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Blocking;

@SuppressWarnings("unused")
public class ImplAdvancedServerZonesAPI implements AdvancedServerZonesAPI {
    @Blocking
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
        if (Math.abs(new Vector(Servers.BORDER_CENTER_X.toDouble(), 0, Servers.BORDER_CENTER_Z.toDouble()).getBlockZ() - Config.BORDER_SIZE.toInteger() - from.getBlockZ()) < 48) {
            return true;
        }
        if (Math.abs(new Vector(Servers.BORDER_CENTER_X.toDouble(), 0, Servers.BORDER_CENTER_Z.toDouble()).getBlockZ() + Config.BORDER_SIZE.toInteger() - from.getBlockZ()) < 48) {
            return true;
        }
        if (Math.abs(new Vector(Servers.BORDER_CENTER_X.toDouble(), 0, Servers.BORDER_CENTER_Z.toDouble()).getBlockX() + Config.BORDER_SIZE.toInteger() - from.getBlockX()) < 48) {
            return true;
        }
        return Math.abs(new Vector(Servers.BORDER_CENTER_X.toDouble(), 0, Servers.BORDER_CENTER_Z.toDouble()).getBlockX() - Config.BORDER_SIZE.toInteger() - from.getBlockX()) < 48;
    }
}
