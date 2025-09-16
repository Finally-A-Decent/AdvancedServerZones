package info.preva1l.advancedserverzones.borders;

import com.destroystokyo.paper.ParticleBuilder;
import info.preva1l.advancedserverzones.config.Config;
import info.preva1l.advancedserverzones.config.Servers;
import info.preva1l.advancedserverzones.util.Cuboid;
import lombok.experimental.UtilityClass;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@UtilityClass
public class BorderParticles {
    @NotNull
    private Color getColor(int[] point) {
        Color color;
        if (Config.i().getBorder().getRainbow().isEnabled()) {
            float hue = Math.abs(point[1]) / 100f;
            hue -= (float) Math.floor(hue);
            int rgb = java.awt.Color.HSBtoRGB(hue * Config.i().getBorder().getRainbow().getHarshness(), 1f, 1f);
            int r = (rgb >>> 16) & 0xFF;
            int g = (rgb >>> 8) & 0xFF;
            int b = rgb & 0xFF;
            color = Color.fromRGB(r, g, b);
        } else {
            color = Color.fromRGB(Config.i().getBorder().getColor().getFirst(),
                    Config.i().getBorder().getColor().get(1),
                    Config.i().getBorder().getColor().getLast());
        }
        return color;
    }

    public void sendBorderParticles(Player p) {
        Vector from = p.getLocation().toVector();
        for (BorderDirection direction : BorderDirection.values()) {
            Cuboid visible = direction.getVisibleBorder(from);
            if (visible == null) continue;

            for (int[] point : visible.getAllPoints()) {
                new ParticleBuilder(Objects.requireNonNull(Registry.PARTICLE_TYPE.get(NamespacedKey.minecraft("dust"))))
                        .color(getColor(point), 2)
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
                int size = Config.i().getBorder().getSize();
                if (Math.abs(new Vector(Servers.i().getBorder().centerX(), 0, Servers.i().getBorder().centerZ()).getBlockZ() - size - from.getBlockZ()) > 16)
                    return null;

                return new Cuboid(
                        Math.max(from.getBlockX() - 10, new Vector(Servers.i().getBorder().centerX(), 0, Servers.i().getBorder().centerZ()).getBlockX() - size),
                        from.getBlockY() - 1,
                        new Vector(Servers.i().getBorder().centerX(), 0, Servers.i().getBorder().centerZ()).getBlockZ() - size,
                        Math.min(from.getBlockX() + 10, new Vector(Servers.i().getBorder().centerX(), 0, Servers.i().getBorder().centerZ()).getBlockX() + size),
                        from.getBlockY() + 5,
                        new Vector(Servers.i().getBorder().centerX(), 0, Servers.i().getBorder().centerZ()).getBlockZ() - size
                );
            }
        },
        EAST {
            @Override
            public Cuboid getVisibleBorder(Vector from) {
                int size = Config.i().getBorder().getSize();
                if (Math.abs(new Vector(Servers.i().getBorder().centerX(), 0, Servers.i().getBorder().centerZ()).getBlockX() + size - from.getBlockX()) > 16)
                    return null;

                return new Cuboid(
                        new Vector(Servers.i().getBorder().centerX(), 0, Servers.i().getBorder().centerZ()).getBlockX() + size,
                        from.getBlockY() - 1,
                        Math.max(from.getBlockZ() - 10, new Vector(Servers.i().getBorder().centerX(), 0, Servers.i().getBorder().centerZ()).getBlockZ() - size),
                        new Vector(Servers.i().getBorder().centerX(), 0, Servers.i().getBorder().centerZ()).getBlockX() + size,
                        from.getBlockY() + 5,
                        Math.min(from.getBlockZ() + 10, new Vector(Servers.i().getBorder().centerX(), 0, Servers.i().getBorder().centerZ()).getBlockZ() + size)
                );
            }
        },
        SOUTH {
            @Override
            public Cuboid getVisibleBorder(Vector from) {
                int size = Config.i().getBorder().getSize();
                if (Math.abs(new Vector(Servers.i().getBorder().centerX(), 0, Servers.i().getBorder().centerZ()).getBlockZ() + size - from.getBlockZ()) > 16)
                    return null;

                return new Cuboid(
                        Math.max(from.getBlockX() - 10, new Vector(Servers.i().getBorder().centerX(), 0, Servers.i().getBorder().centerZ()).getBlockX() - size),
                        from.getBlockY() - 1,
                        new Vector(Servers.i().getBorder().centerX(), 0, Servers.i().getBorder().centerZ()).getBlockZ() + size,
                        Math.min(from.getBlockX() + 10, new Vector(Servers.i().getBorder().centerX(), 0, Servers.i().getBorder().centerZ()).getBlockX() + size),
                        from.getBlockY() + 5,
                        new Vector(Servers.i().getBorder().centerX(), 0, Servers.i().getBorder().centerZ()).getBlockZ() + size
                );
            }
        },
        WEST {
            @Override
            public Cuboid getVisibleBorder(Vector from) {
                int size = Config.i().getBorder().getSize();
                if (Math.abs(new Vector(Servers.i().getBorder().centerX(), 0, Servers.i().getBorder().centerZ()).getBlockX() - size - from.getBlockX()) > 16)
                    return null;

                return new Cuboid(
                        new Vector(Servers.i().getBorder().centerX(), 0, Servers.i().getBorder().centerZ()).getBlockX() - size,
                        from.getBlockY() - 1,
                        Math.max(from.getBlockZ() - 10, new Vector(Servers.i().getBorder().centerX(), 0, Servers.i().getBorder().centerZ()).getBlockZ() - size),
                        new Vector(Servers.i().getBorder().centerX(), 0, Servers.i().getBorder().centerZ()).getBlockX() - size,
                        from.getBlockY() + 5,
                        Math.min(from.getBlockZ() + 10, new Vector(Servers.i().getBorder().centerX(), 0, Servers.i().getBorder().centerZ()).getBlockZ() + size)
                );
            }
        };

        public abstract Cuboid getVisibleBorder(Vector from);
    }
}