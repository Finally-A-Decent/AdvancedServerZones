package lol.arch.survival.transfer;

import com.destroystokyo.paper.ParticleBuilder;
import lol.arch.survival.config.Config;
import lol.arch.survival.util.Cuboid;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

/**
 * @author elijuh
 * @author preva1l
 */
public class BorderParticles {

    public void sendBorderParticles(Player p) {
        Vector from = p.getLocation().toVector();
        for (BorderDirection direction : BorderDirection.values()) {
            Cuboid visible = direction.getVisibleBorder(from);
            if (visible == null) continue;

            for (int[] point : visible.getAllPoints()) {
                new ParticleBuilder(Particle.REDSTONE)
                        .color(getColor(point))
                        .location(new Location(p.getWorld(), point[0], point[1], point[2]))
                        .receivers(p)
                        .spawn();
            }
        }
    }

    private enum BorderDirection {
        NORTH {
            @Override
            public Cuboid getVisibleBorder(Vector from) {
                if (Math.abs(new Vector(Config.getBorderCenterX(), 0, Config.getBorderCenterZ()).getBlockZ() - Config.getBorderSize() - from.getBlockZ()) > 16) return null;

                return new Cuboid(
                        Math.max(from.getBlockX() - 10, new Vector(Config.getBorderCenterX(), 0, Config.getBorderCenterZ()).getBlockX() - Config.getBorderSize()),
                        from.getBlockY() - 1,
                        new Vector(Config.getBorderCenterX(), 0, Config.getBorderCenterZ()).getBlockZ() - Config.getBorderSize(),
                        Math.min(from.getBlockX() + 10, new Vector(Config.getBorderCenterX(), 0, Config.getBorderCenterZ()).getBlockX() + Config.getBorderSize()),
                        from.getBlockY() + 5,
                        new Vector(Config.getBorderCenterX(), 0, Config.getBorderCenterZ()).getBlockZ() - Config.getBorderSize()
                );
            }
        },
        EAST {
            @Override
            public Cuboid getVisibleBorder(Vector from) {
                if (Math.abs(new Vector(Config.getBorderCenterX(), 0, Config.getBorderCenterZ()).getBlockX() + Config.getBorderSize() - from.getBlockX()) > 16) return null;

                return new Cuboid(
                        new Vector(Config.getBorderCenterX(), 0, Config.getBorderCenterZ()).getBlockX() + Config.getBorderSize(),
                        from.getBlockY() - 1,
                        Math.max(from.getBlockZ() - 10, new Vector(Config.getBorderCenterX(), 0, Config.getBorderCenterZ()).getBlockZ() - Config.getBorderSize()),
                        new Vector(Config.getBorderCenterX(), 0, Config.getBorderCenterZ()).getBlockX() + Config.getBorderSize(),
                        from.getBlockY() + 5,
                        Math.min(from.getBlockZ() + 10, new Vector(Config.getBorderCenterX(), 0, Config.getBorderCenterZ()).getBlockZ() + Config.getBorderSize())
                );
            }
        },
        SOUTH {
            @Override
            public Cuboid getVisibleBorder(Vector from) {
                if (Math.abs(new Vector(Config.getBorderCenterX(), 0, Config.getBorderCenterZ()).getBlockZ() + Config.getBorderSize() - from.getBlockZ()) > 16) return null;

                return new Cuboid(
                        Math.max(from.getBlockX() - 10, new Vector(Config.getBorderCenterX(), 0, Config.getBorderCenterZ()).getBlockX() - Config.getBorderSize()),
                        from.getBlockY() - 1,
                        new Vector(Config.getBorderCenterX(), 0, Config.getBorderCenterZ()).getBlockZ() + Config.getBorderSize(),
                        Math.min(from.getBlockX() + 10, new Vector(Config.getBorderCenterX(), 0, Config.getBorderCenterZ()).getBlockX() + Config.getBorderSize()),
                        from.getBlockY() + 5,
                        new Vector(Config.getBorderCenterX(), 0, Config.getBorderCenterZ()).getBlockZ() + Config.getBorderSize()
                );
            }
        },
        WEST {
            @Override
            public Cuboid getVisibleBorder(Vector from) {
                if (Math.abs(new Vector(Config.getBorderCenterX(), 0, Config.getBorderCenterZ()).getBlockX() - Config.getBorderSize() - from.getBlockX()) > 16) return null;

                return new Cuboid(
                        new Vector(Config.getBorderCenterX(), 0, Config.getBorderCenterZ()).getBlockX() - Config.getBorderSize(),
                        from.getBlockY() - 1,
                        Math.max(from.getBlockZ() - 10, new Vector(Config.getBorderCenterX(), 0, Config.getBorderCenterZ()).getBlockZ() - Config.getBorderSize()),
                        new Vector(Config.getBorderCenterX(), 0, Config.getBorderCenterZ()).getBlockX() - Config.getBorderSize(),
                        from.getBlockY() + 5,
                        Math.min(from.getBlockZ() + 10, new Vector(Config.getBorderCenterX(), 0, Config.getBorderCenterZ()).getBlockZ() + Config.getBorderSize())
                );
            }
        };

        public abstract Cuboid getVisibleBorder(Vector from);
    }

    @NotNull
    private static Color getColor(int[] point) {
        Color color;
        if (Config.isRainbowParticle()) {
            float hue = Math.abs(point[1]) / 100f;
            hue -= (float) Math.floor(hue);
            int rgb = java.awt.Color.HSBtoRGB(hue * Config.getRainbowParticleHarshness(), 1f, 1f);
            int r = (rgb >>> 16) & 0xFF;
            int g = (rgb >>>  8) & 0xFF;
            int b = rgb & 0xFF;
            color = Color.fromRGB(r, g, b);
        } else {
            color = Color.fromRGB(Config.getParticleColor().get(0), Config.getParticleColor().get(1), Config.getParticleColor().get(2));
        }
        return color;
    }
}