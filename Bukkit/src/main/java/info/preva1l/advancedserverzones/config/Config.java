package info.preva1l.advancedserverzones.config;

import de.exlll.configlib.*;
import info.preva1l.advancedserverzones.AdvancedServerZones;
import info.preva1l.advancedserverzones.chat.ChatSyncService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Getter
@Configuration
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("FieldMayBeFinal")
public final class Config {
    private static Config instance;

    private static final String CONFIG_HEADER = """
            #########################################
            #          AdvancedServerZones          #
            #########################################
            """;

    private Border border = new Border();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Border {
        @Comment("How many blocks from the center of the world will the border be")
        private int size = 20;
        @Comment({"How many blocks from the borders should we prevent world modification.", "Recommended: 48 (3 chunks)"})
        private int interactionRadius = 48;

        @Comment("RGB Color of the particles")
        private List<Integer> color = List.of(184, 50, 172);

        private Rainbow rainbow = new Rainbow();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Rainbow {
            private boolean enabled = false;
            @Comment("1-10")
            private int harshness = 5;
        }
    }

    private ChatSyncConfig chatsync = new ChatSyncConfig();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ChatSyncConfig {
        private boolean enabled = false;
        @Comment("Supported: VANILLA, CUSTOM, API")
        private ChatSyncService.ChatSyncMode mode = ChatSyncService.ChatSyncMode.VANILLA;
        private String customFormat = "<white>[<#FF0000>%server%<white>] <reset>%prefix%<reset> <white>%player% <dark_gray>> <gray>%message%";
    }

    private Redis redis = new Redis();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Redis {
        private String host = "127.0.0.1";
        private int port = 6379;
        private String username = "";
        private String password = "abc123";
        private String channel = "advancedserverzones:chat";
    }

    private static final YamlConfigurationProperties PROPERTIES = YamlConfigurationProperties.newBuilder()
            .charset(StandardCharsets.UTF_8)
            .setNameFormatter(NameFormatters.LOWER_KEBAB_CASE)
            .header(CONFIG_HEADER).build();

    public static void reload() {
        instance = YamlConfigurations.load(new File(AdvancedServerZones.instance.getDataFolder(), "config.yml").toPath(), Config.class, PROPERTIES);
        AdvancedServerZones.instance.getLogger().info("Configuration automatically reloaded from disk.");
    }

    public static Config i() {
        if (instance == null) {
            instance = YamlConfigurations.update(new File(AdvancedServerZones.instance.getDataFolder(), "config.yml").toPath(), Config.class, PROPERTIES);
            AutoReload.watch(AdvancedServerZones.instance.getDataFolder().toPath(), "config.yml", Config::reload);
        }

        return instance;
    }
}
