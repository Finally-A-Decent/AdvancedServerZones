package info.preva1l.advancedserverzones.chat;

import lombok.Setter;
import lombok.experimental.UtilityClass;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import net.milkbowl.vault.chat.Chat;

@UtilityClass
public class PlaceholderManager {
    @Setter private Chat chat;
    public String getPrefix(Player player) {
        if (chat != null) return chat.getPlayerPrefix(player);

        return "";
    }
    public String getSuffix(Player player) {
        if (chat != null) return chat.getPlayerSuffix(player);

        return "";
    }
    public String formatWithPAPI(Player player, String toFormat) {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            return PlaceholderAPI.setPlaceholders(player, toFormat);

        return toFormat;
    }
}
