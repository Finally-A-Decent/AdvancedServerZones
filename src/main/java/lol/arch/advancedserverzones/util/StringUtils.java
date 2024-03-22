package lol.arch.advancedserverzones.util;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Basic string utils
 *
 * @author Preva1l
 */
@UtilityClass
public class StringUtils {
    /**
     * Converts legacy colour codes to MiniMessage
     * @param message message with legacy codes
     * @return string with mini message formatting (not colorized)
     */
    public static String legacyToMiniMessage(String message) {
        //Codes AGHHHH MANUAL CONVERSION
        message = message.replace("&4", "<dark_red>")
                .replace("&c", "<red>")
                .replace("&6", "<gold>")
                .replace("&e", "<yellow>")
                .replace("&2", "<dark_green>")
                .replace("&a", "<green>")
                .replace("&b", "<aqua>")
                .replace("&3", "<dark_aqua>")
                .replace("&1", "<dark_blue>")
                .replace("&9", "<blue>")
                .replace("&d", "<light_purple>")
                .replace("&5", "<dark_purple>")
                .replace("&f", "<white>")
                .replace("&7", "<gray>")
                .replace("&8", "<dark_gray>")
                .replace("&0", "<black>")
                .replace("&l", "<b>")
                .replace("&k", "<obf>")
                .replace("&m", "<st>")
                .replace("&n", "<u>")
                .replace("&o", "<i>")
                .replace("&r", "<reset>");

        //Hex
        Pattern pattern = Pattern.compile("&#[a-fA-F0-9]{6}");
        Matcher match = pattern.matcher(message);
        String code = message;
        while (match.find()) {
            code = message.substring(match.start(),match.end());
            code = code.replace("&", "<");
            code = code + ">";
        }
        return message.replaceAll("&#[a-fA-F0-9]{6}", code);
    }

    /**
     * Formats Strings with placeholders
     * @param message message with placeholders: {index}
     * @param args things to replace with
     * @return formatted string
     */
    public static String formatPlaceholders(String message, Object... args) {
        for (int i = 0; i < args.length; i++) {
            if (!message.contains("{" + i + "}")) {
                continue;
            }

            message = message.replace("{" + i + "}", String.valueOf(args[i]));
        }
        return message;
    }

    /**
     * Formats a string into a component.
     * @param message string with mini message formatted colours and or placeholders
     * @param args arguments for {@link StringUtils#formatPlaceholders(String, Object...)}
     * @return formatted component
     */
    public static Component message(String message, Object... args) {
        message = legacyToMiniMessage(message);
        message = formatPlaceholders(message, args);

        return MiniMessage.miniMessage().deserialize(message);
    }
}
