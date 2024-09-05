package info.preva1l.advancedserverzones.config;

import info.preva1l.advancedserverzones.AdvancedServerZones;
import info.preva1l.advancedserverzones.util.BasicConfig;
import info.preva1l.advancedserverzones.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.Component;

@Getter
@AllArgsConstructor
public enum Lang {
    RELOADED("reloaded", "&aPlugin Reloaded"),
    CANNOT_INTERACT("cannot-interact-here", "&7[&4!&7] &cYou cannot interact within &f3 chunks &cof the region border!"),
    BORDER_TITLE("world-border.title", "&cYou have reached the world border!"),
    BORDER_SUBTITLE("world-border.subtitle", "&7Try exploring a different direction!"),
    ;

    private final String path;
    private final Object defaultValue;

    public String toString() {
        String str = AdvancedServerZones.getInstance().getLangFile().getString(path);
        if (str == null) {
            return defaultValue.toString();
        }
        return str;
    }

    public Component toFormattedComponent() {
        String str = AdvancedServerZones.getInstance().getLangFile().getString(path);
        if (str.equals(path)) {
            return StringUtils.message(defaultValue.toString());
        }
        return StringUtils.message(str);
    }

    public static void loadDefault() {
        BasicConfig configFile = AdvancedServerZones.getInstance().getLangFile();

        for (Lang config : Lang.values()) {
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