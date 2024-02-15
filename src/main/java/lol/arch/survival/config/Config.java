package lol.arch.survival.config;

import lol.arch.survival.LoadDistribution;
import lombok.Getter;

import java.util.List;

public class Config {
    @Getter private static String serverName;
    @Getter private static int borderSize;
    @Getter private static double borderCenterX;
    @Getter private static double borderCenterZ;
    @Getter private static List<Integer> particleColor;
    @Getter private static boolean rainbowParticle;
    @Getter private static int rainbowParticleHarshness;

    public static void reload() {
        serverName = LoadDistribution.getInstance().getConfig().getString("server-name");
        borderSize = LoadDistribution.getInstance().getConfig().getInt("border.size");
        borderCenterX = LoadDistribution.getInstance().getConfig().getDouble("border.centerX");
        borderCenterZ = LoadDistribution.getInstance().getConfig().getDouble("border.centerZ");
        rainbowParticle = LoadDistribution.getInstance().getConfig().getBoolean("border.particle-rainbow");
        rainbowParticleHarshness = LoadDistribution.getInstance().getConfig().getInt("border.particle-rainbow-harshness");
        particleColor = LoadDistribution.getInstance().getConfig().getIntegerList("border.particle-color");
        Servers.reload();
        Redis.reload();
    }

    public static class Servers {
        @Getter private static String north;
        @Getter private static String south;
        @Getter private static String east;
        @Getter private static String west;

        public static void reload() {
            north = LoadDistribution.getInstance().getConfig().getString("servers.north");
            south = LoadDistribution.getInstance().getConfig().getString("servers.south");
            east = LoadDistribution.getInstance().getConfig().getString("servers.east");
            west = LoadDistribution.getInstance().getConfig().getString("servers.west");
        }
    }
    public static class Redis {
        @Getter private static String host;
        @Getter private static int port;
        @Getter private static String password;

        public static void reload() {
            host = LoadDistribution.getInstance().getConfig().getString("redis.host");
            port = LoadDistribution.getInstance().getConfig().getInt("redis.port");
            password = LoadDistribution.getInstance().getConfig().getString("redis.password");
        }
    }
}
