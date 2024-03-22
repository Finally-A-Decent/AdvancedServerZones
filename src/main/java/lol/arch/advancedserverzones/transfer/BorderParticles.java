package lol.arch.advancedserverzones.transfer;

import com.destroystokyo.paper.ParticleBuilder;
import lol.arch.advancedserverzones.config.Config;
import lol.arch.advancedserverzones.config.Servers;
import lol.arch.advancedserverzones.util.Cuboid;
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

    @NotNull
    private static Color getColor(int[] point) {
        Color color;
        if (Config.RAINBOW_PARTICLES.toBoolean()) {
            float hue = Math.abs(point[1]) / 100f;
            hue -= (float) Math.floor(hue);
            int rgb = java.awt.Color.HSBtoRGB(hue * Config.RAINBOW_HARSHNESS.toInteger(), 1f, 1f);
            int r = (rgb >>> 16) & 0xFF;
            int g = (rgb >>> 8) & 0xFF;
            int b = rgb & 0xFF;
            color = Color.fromRGB(r, g, b);
        } else {
            color = Color.fromRGB(Config.PARTICLE_COLOR.toIntegerList().get(0),
                    Config.PARTICLE_COLOR.toIntegerList().get(1),
                    Config.PARTICLE_COLOR.toIntegerList().get(2));
        }
        return color;
    }

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
                if (Math.abs(new Vector(Servers.BORDER_CENTER_X.toDouble(), 0, Servers.BORDER_CENTER_Z.toDouble()).getBlockZ() - Config.BORDER_SIZE.toInteger() - from.getBlockZ()) > 16)
                    return null;

                return new Cuboid(
                        Math.max(from.getBlockX() - 10, new Vector(Servers.BORDER_CENTER_X.toDouble(), 0, Servers.BORDER_CENTER_Z.toDouble()).getBlockX() - Config.BORDER_SIZE.toInteger()),
                        from.getBlockY() - 1,
                        new Vector(Servers.BORDER_CENTER_X.toDouble(), 0, Servers.BORDER_CENTER_Z.toDouble()).getBlockZ() - Config.BORDER_SIZE.toInteger(),
                        Math.min(from.getBlockX() + 10, new Vector(Servers.BORDER_CENTER_X.toDouble(), 0, Servers.BORDER_CENTER_Z.toDouble()).getBlockX() + Config.BORDER_SIZE.toInteger()),
                        from.getBlockY() + 5,
                        new Vector(Servers.BORDER_CENTER_X.toDouble(), 0, Servers.BORDER_CENTER_Z.toDouble()).getBlockZ() - Config.BORDER_SIZE.toInteger()
                );
            }
        },
        EAST {
            @Override
            public Cuboid getVisibleBorder(Vector from) {
                if (Math.abs(new Vector(Servers.BORDER_CENTER_X.toDouble(), 0, Servers.BORDER_CENTER_Z.toDouble()).getBlockX() + Config.BORDER_SIZE.toInteger() - from.getBlockX()) > 16)
                    return null;

                return new Cuboid(
                        new Vector(Servers.BORDER_CENTER_X.toDouble(), 0, Servers.BORDER_CENTER_Z.toDouble()).getBlockX() + Config.BORDER_SIZE.toInteger(),
                        from.getBlockY() - 1,
                        Math.max(from.getBlockZ() - 10, new Vector(Servers.BORDER_CENTER_X.toDouble(), 0, Servers.BORDER_CENTER_Z.toDouble()).getBlockZ() - Config.BORDER_SIZE.toInteger()),
                        new Vector(Servers.BORDER_CENTER_X.toDouble(), 0, Servers.BORDER_CENTER_Z.toDouble()).getBlockX() + Config.BORDER_SIZE.toInteger(),
                        from.getBlockY() + 5,
                        Math.min(from.getBlockZ() + 10, new Vector(Servers.BORDER_CENTER_X.toDouble(), 0, Servers.BORDER_CENTER_Z.toDouble()).getBlockZ() + Config.BORDER_SIZE.toInteger())
                );
            }
        },
        SOUTH {
            @Override
            public Cuboid getVisibleBorder(Vector from) {
                if (Math.abs(new Vector(Servers.BORDER_CENTER_X.toDouble(), 0, Servers.BORDER_CENTER_Z.toDouble()).getBlockZ() + Config.BORDER_SIZE.toInteger() - from.getBlockZ()) > 16)
                    return null;

                return new Cuboid(
                        Math.max(from.getBlockX() - 10, new Vector(Servers.BORDER_CENTER_X.toDouble(), 0, Servers.BORDER_CENTER_Z.toDouble()).getBlockX() - Config.BORDER_SIZE.toInteger()),
                        from.getBlockY() - 1,
                        new Vector(Servers.BORDER_CENTER_X.toDouble(), 0, Servers.BORDER_CENTER_Z.toDouble()).getBlockZ() + Config.BORDER_SIZE.toInteger(),
                        Math.min(from.getBlockX() + 10, new Vector(Servers.BORDER_CENTER_X.toDouble(), 0, Servers.BORDER_CENTER_Z.toDouble()).getBlockX() + Config.BORDER_SIZE.toInteger()),
                        from.getBlockY() + 5,
                        new Vector(Servers.BORDER_CENTER_X.toDouble(), 0, Servers.BORDER_CENTER_Z.toDouble()).getBlockZ() + Config.BORDER_SIZE.toInteger()
                );
            }
        },
        WEST {
            @Override
            public Cuboid getVisibleBorder(Vector from) {
                if (Math.abs(new Vector(Servers.BORDER_CENTER_X.toDouble(), 0, Servers.BORDER_CENTER_Z.toDouble()).getBlockX() - Config.BORDER_SIZE.toInteger() - from.getBlockX()) > 16)
                    return null;

                return new Cuboid(
                        new Vector(Servers.BORDER_CENTER_X.toDouble(), 0, Servers.BORDER_CENTER_Z.toDouble()).getBlockX() - Config.BORDER_SIZE.toInteger(),
                        from.getBlockY() - 1,
                        Math.max(from.getBlockZ() - 10, new Vector(Servers.BORDER_CENTER_X.toDouble(), 0, Servers.BORDER_CENTER_Z.toDouble()).getBlockZ() - Config.BORDER_SIZE.toInteger()),
                        new Vector(Servers.BORDER_CENTER_X.toDouble(), 0, Servers.BORDER_CENTER_Z.toDouble()).getBlockX() - Config.BORDER_SIZE.toInteger(),
                        from.getBlockY() + 5,
                        Math.min(from.getBlockZ() + 10, new Vector(Servers.BORDER_CENTER_X.toDouble(), 0, Servers.BORDER_CENTER_Z.toDouble()).getBlockZ() + Config.BORDER_SIZE.toInteger())
                );
            }
        };

        public abstract Cuboid getVisibleBorder(Vector from);
    }
}