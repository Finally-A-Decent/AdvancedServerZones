package lol.arch.survival.config;

import com.google.common.collect.ImmutableList;
import lol.arch.survival.AdvancedServerZones;
import lol.arch.survival.sync.ChatSync;
import lol.arch.survival.util.BasicConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
@Getter
@AllArgsConstructor
public enum Config {
    /*
     * Border Settings
     */
    BORDER_SIZE("border.size", 20),
    // Visual
    PARTICLE_COLOR("border.particle.color", ImmutableList.of(0, 0, 0)),
    RAINBOW_PARTICLES("border.particles.rainbow", false),
    RAINBOW_HARSHNESS("border.particles.rainbow-harshness", 5),

    /*
     * Chat Sync
     */
    CHAT_SYNC("chatsync.enabled", true),
    CHAT_SYNC_MODE("chatsync.mode", "VANILLA"),
    CHAT_SYNC_CUSTOM("chatsync.custom", "<white>[<#FF0000>{server}<white>] <reset>{prefix}<reset> <white>{player_name} <dark_gray>>> <gray>{message}"),

    /*
     * Redis
     */
    REDIS_HOST("redis.host", "127.0.0.1"),
    REDIS_PORT("redis.port", 6379),
    REDIS_PASSWORD("redis.password", "abc123"),
    ;

    private final String path;
    private final Object defaultValue;

    public String toString() {
        String str = AdvancedServerZones.getInstance().getConfigFile().getString(path);
        if (str == null) {
            return defaultValue.toString();
        }
        return str;
    }

    public boolean toBoolean() {
        return Boolean.parseBoolean(toString());
    }

    public int toInteger() {
        return Integer.parseInt(toString());
    }
    public List<Integer> toIntegerList() {
        List<Integer> list = AdvancedServerZones.getInstance().getConfigFile().getConfiguration().getIntegerList(path);
        if (list.isEmpty() || list.get(0) == null) {
            return ImmutableList.of();
        }
        return list;
    }

    public ChatSync.ChatSyncMode toSyncMode() {
        return ChatSync.ChatSyncMode.valueOf(toString());
    }
    public static void loadDefault() {
        BasicConfig configFile = AdvancedServerZones.getInstance().getConfigFile();

        for (Config config : Config.values()) {
            String path = config.getPath();
            String str = configFile.getString(path);
            if (str.equals(path)) {
                configFile.getConfiguration().set(path, config.getDefaultValue());
            }
        }

        configFile.save();
        configFile.load();
    }
}