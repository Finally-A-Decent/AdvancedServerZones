package lol.arch.survival.config;

import lol.arch.survival.AdvancedServerZones;
import lol.arch.survival.util.BasicConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;

@Getter
@AllArgsConstructor
public enum Servers {
    CURRENT_SERVER("current-server", Bukkit.getServer()),

    NORTH_SERVER("servers.north", "survival-zone-01"),
    SOUTH_SERVER("servers.south", "survival-zone-06"),
    EAST_SERVER("servers.east", "survival-spawn-01"),
    WEST_SERVER("servers.west", ""),

    BORDER_CENTER_X("border.centerX", 0),
    BORDER_CENTER_Z("border.centerZ", 0),
    ;

    private final String path;
    private final Object defaultValue;

    public String toString() {
        String str = AdvancedServerZones.getInstance().getServersFile().getString(path);
        if (str == null) {
            return defaultValue.toString();
        }
        return str;
    }
    public double toDouble() {
        return Double.parseDouble(toString());
    }

    public static void loadDefault() {
        BasicConfig configFile = AdvancedServerZones.getInstance().getServersFile();

        for (Servers config : Servers.values()) {
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