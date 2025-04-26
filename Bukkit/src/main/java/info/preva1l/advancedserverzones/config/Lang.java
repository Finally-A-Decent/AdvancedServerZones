package info.preva1l.advancedserverzones.config;

import de.exlll.configlib.*;
import info.preva1l.advancedserverzones.AdvancedServerZones;
import info.preva1l.advancedserverzones.util.Logger;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.File;
import java.nio.charset.StandardCharsets;
@Getter
@Configuration
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("FieldMayBeFinal")
public class Lang {
    private static Lang instance;

    private static final String CONFIG_HEADER = """
            #########################################
            #          AdvancedServerZones          #
            #         Language Configuration        #
            #########################################
            """;

    private String reloaded = "&aAdvancedServerZones Reloaded!";
    private String cannotInteract = "&7[&4!&7] &cYou cannot interact within &f3 chunks &cof the region border!";

    private Border border = new Border(
            "&cYou have reached the world border!",
            "&7Try exploring a different direction!"
    );

    public record Border(String title, String subtitle) { }

    private static final YamlConfigurationProperties PROPERTIES = YamlConfigurationProperties.newBuilder()
            .charset(StandardCharsets.UTF_8)
            .setNameFormatter(NameFormatters.LOWER_KEBAB_CASE)
            .header(CONFIG_HEADER).build();

    public static void reload() {
        instance = YamlConfigurations.load(new File(AdvancedServerZones.i().getDataFolder(), "lang.yml").toPath(), Lang.class, PROPERTIES);
        Logger.info("Language file automatically reloaded from disk.");
    }

    public static Lang i() {
        if (instance == null) {
            instance = YamlConfigurations.update(new File(AdvancedServerZones.i().getDataFolder(), "lang.yml").toPath(), Lang.class, PROPERTIES);
            AutoReload.watch(AdvancedServerZones.i().getDataFolder().toPath(), "lang.yml", Lang::reload);
        }

        return instance;
    }
}
