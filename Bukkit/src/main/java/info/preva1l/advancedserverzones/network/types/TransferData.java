package info.preva1l.advancedserverzones.network.types;

import com.google.gson.annotations.Expose;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.UUID;

public record TransferData(@Expose UUID player,
                           @Expose String targetServer,
                           @Expose TransferData.Position position,
                           @Expose int lastPing
) {
    public record Direction(
            @Expose double x,
            @Expose double y,
            @Expose double z
    ) {
        public static Direction of(Vector v) {
            return new Direction(v.getX(), v.getY(), v.getZ());
        }

        public Vector toVector() {
            return new Vector(x, y, z);
        }
    }

    public record Position(@Expose double x,
                           @Expose double y,
                           @Expose double z,
                           @Expose float pitch,
                           @Expose float yaw,
                           @Expose Direction direction,
                           @Expose String world
    ) {
        public static Position from(Location location) {
            return new Position(
                    location.getX(),
                    location.getY(),
                    location.getZ(),
                    location.getPitch(),
                    location.getYaw(),
                    Direction.of(location.getDirection()),
                    location.getWorld().getName()
            );
        }

        public Location predictedLocation(int ping) {
            double pingSeconds = ping / 1000.0 / 2.0;

            Vector movement = direction.toVector().multiply(pingSeconds);

            double adjustedX = x() + movement.getX();
            double adjustedY = y() + movement.getY();
            double adjustedZ = z() + movement.getZ();

            return new Location(
                    Bukkit.getWorld(world),
                    adjustedX,
                    adjustedY,
                    adjustedZ,
                    yaw,
                    pitch
            );
        }
    }
}
