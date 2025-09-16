package info.preva1l.advancedserverzones.config;

import de.exlll.configlib.*;
import info.preva1l.advancedserverzones.AdvancedServerZones;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.util.NumberConversions;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * Created on 26/04/2025
 *
 * @author Preva1l
 */
@Getter
@Configuration
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("FieldMayBeFinal")
public final class Servers {
    private static Servers instance;

    private static final String CONFIG_HEADER = """
            #########################################
            #          AdvancedServerZones          #
            #         Servers Configuration         #
            #########################################
            """;

    private String current = "survival-zone-03";
    private String north = "survival-zone-01";
    private String south = "survival-zone-06";
    private String east = "survival-spawn-01";
    private String west = "";

    private Border border = new Border();

    @Configuration
    public static class Border {
        @Getter
        private double centerX, centerZ = 0;

        @Ignore
        private Integer flooredCenterX = null;
        @Ignore
        private Integer flooredCenterZ = null;

        public int flooredCenterX() {
            if (flooredCenterX == null) flooredCenterX = NumberConversions.floor(centerX);

            return flooredCenterX;
        }

        public int flooredCenterZ() {
            if (flooredCenterZ == null) flooredCenterZ = NumberConversions.floor(centerZ);

            return flooredCenterZ;
        }
    }

    private static final YamlConfigurationProperties PROPERTIES = YamlConfigurationProperties.newBuilder()
            .charset(StandardCharsets.UTF_8)
            .setNameFormatter(NameFormatters.LOWER_KEBAB_CASE)
            .header(CONFIG_HEADER).build();

    public static void reload() {
        instance = YamlConfigurations.load(new File(AdvancedServerZones.instance.getDataFolder(), "server.yml").toPath(), Servers.class, PROPERTIES);
        AdvancedServerZones.instance.getLogger().info("Servers configuration automatically reloaded from disk.");
    }

    public static Servers i() {
        if (instance == null) {
            instance = YamlConfigurations.update(new File(AdvancedServerZones.instance.getDataFolder(), "server.yml").toPath(), Servers.class, PROPERTIES);
            AutoReload.watch(AdvancedServerZones.instance.getDataFolder().toPath(), "server.yml", Servers::reload);
        }

        return instance;
    }
}
