package lol.arch.survival.api;

import lol.arch.survival.config.Config;
import lombok.experimental.UtilityClass;
import org.bukkit.Chunk;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Blocking;

@UtilityClass
public class RegionsAPI {

    @Blocking
    public boolean isChunkProtected(Chunk chunk) {
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        int chunkMinX = chunkX << 4;
        int chunkMinZ = chunkZ << 4;
        int chunkMaxX = (chunkX << 4) + 15;
        int chunkMaxZ = (chunkZ << 4) + 15;
        for (int x = chunkMinX; x <= chunkMaxX; x++) {
            for (int z = chunkMinZ; z <= chunkMaxZ; z++) {
                Vector from = chunk.getBlock(x, 0, z).getLocation().toVector();
                if (Math.abs(new Vector(Config.getBorderCenterX(), 0, Config.getBorderCenterZ()).getBlockZ() - Config.getBorderSize() - from.getBlockZ()) < 48) return true;
                if (Math.abs(new Vector(Config.getBorderCenterX(), 0, Config.getBorderCenterZ()).getBlockX() + Config.getBorderSize() - from.getBlockX()) < 48) return true;
                if (Math.abs(new Vector(Config.getBorderCenterX(), 0, Config.getBorderCenterZ()).getBlockZ() + Config.getBorderSize() - from.getBlockZ()) < 48) return true;
                if (Math.abs(new Vector(Config.getBorderCenterX(), 0, Config.getBorderCenterZ()).getBlockX() - Config.getBorderSize() - from.getBlockX()) < 48) return true;
            }
        }
        return false;
    }
}
