package info.preva1l.advancedserverzones.config;

import de.exlll.configlib.Configuration;
import de.exlll.configlib.NameFormatters;
import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurations;
import info.preva1l.advancedserverzones.AdvancedServerZones;
import info.preva1l.advancedserverzones.util.Logger;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;

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
public class Servers {
    private static Servers instance;

    private static final String CONFIG_HEADER = """
            #########################################
            #          AdvancedServerZones          #
            #         Servers Configuration         #
            #########################################
            """;

    private String current = Bukkit.getServer().getName();
    private String north = "survival-zone-01";
    private String south = "survival-zone-06";
    private String east = "survival-spawn-01";
    private String west = "";

    private Border border = new Border(0, 0);

    public record Border(double centerX, double centerZ) {}

    private static final YamlConfigurationProperties PROPERTIES = YamlConfigurationProperties.newBuilder()
            .charset(StandardCharsets.UTF_8)
            .setNameFormatter(NameFormatters.LOWER_KEBAB_CASE)
            .header(CONFIG_HEADER).build();

    public static void reload() {
        instance = YamlConfigurations.load(new File(AdvancedServerZones.i().getDataFolder(), "server.yml").toPath(), Servers.class, PROPERTIES);
        Logger.info("Servers configuration automatically reloaded from disk.");
    }

    public static Servers i() {
        if (instance == null) {
            instance = YamlConfigurations.update(new File(AdvancedServerZones.i().getDataFolder(), "server.yml").toPath(), Servers.class, PROPERTIES);
            AutoReload.watch(AdvancedServerZones.i().getDataFolder().toPath(), "server.yml", Servers::reload);
        }

        return instance;
    }
}
