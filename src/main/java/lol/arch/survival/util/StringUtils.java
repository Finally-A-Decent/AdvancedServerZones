package lol.arch.survival.util;

import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Basic string utils
 * @author Preva1l
 */
@UtilityClass
public class StringUtils {
    /**
     * Colorize a list. (Useful for lore)
     * @param list List typeof String
     * @return Colorized List typeof String
     */
    public List<String> colorizeList(List<String> list) {
        if (list == null) return null;
        if (list.isEmpty()) return null;
        List<String> ret = new ArrayList<>();
        for (String line : list) ret.add(colorize(line));
        return ret;
    }

    /**
     * Colorize  a string.
     * @param str String with color codes or hex codes.
     * @return Colorized String
     */
    public String colorize(String str) {
        if (str == null) return null;
        Pattern unicode = Pattern.compile("\\\\u\\+[a-fA-F0-9]{4}");
        Matcher match = unicode.matcher(str);
        while (match.find()) {
            String code = str.substring(match.start(),match.end());
            str = str.replace(code,Character.toString((char) Integer.parseInt(code.replace("\\u+",""),16)));
            match = unicode.matcher(str);
        }
        Pattern pattern = Pattern.compile("&#[a-fA-F0-9]{6}");
        match = pattern.matcher(str);
        while (match.find()) {
            String color = str.substring(match.start(),match.end());
            str = str.replace(color, ChatColor.of(color.replace("&","")) + "");
            match = pattern.matcher(str);
        }
        return ChatColor.translateAlternateColorCodes('&',str);
    }
}
