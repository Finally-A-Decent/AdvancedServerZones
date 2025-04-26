package info.preva1l.advancedserverzones.util;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.internal.parser.Token;
import net.kyori.adventure.text.minimessage.internal.parser.TokenParser;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * String Formatting Helper.
 */
@UtilityClass
public class Text {
    private final MiniMessage miniMessage = MiniMessage.builder().build();
    private final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.legacyAmpersand();
    private final Pattern REMOVE_PATTERN = Pattern.compile("&#(\\w{5}[0-9a-fA-F])|&[0-9a-fA-Fk-orK-OR]");

    @SafeVarargs
    public Component text(@NotNull String message, Tuple<String, Object>... args) {
        return text(null, message, args);
    }

    @SafeVarargs
    public Component text(@NotNull List<String> message, Tuple<String, Object>... args) {
        return text(String.join("\n", message), args);
    }

    @SafeVarargs
    public Component text(@Nullable Player player, @NotNull String message, Tuple<String, Object>... args) {
        return miniMessage.deserialize(unescape(miniMessage.serialize(
                                        legacySerializer.deserialize("<!italic>" + replace(message, args)))));
    }

    @SafeVarargs
    public List<Component> list(@NotNull List<String> list, Tuple<String, Object>... args) {
        return list(null, list, args);
    }

    @SafeVarargs
    public List<Component> list(@Nullable Player player, List<String> list, Tuple<String, Object>... args) {
        return list.stream().map(string -> Text.text(player, string, args)).collect(Collectors.toList());
    }

    /**
     * Unescapes minimessage tags.
     * <p>
     *     This will be removed once minimessage adds the option to prevent the serializer from escaping them in the first place.
     * </p>
     *
     * @param input the minimessage formatted string with escaped tags
     * @return the minimessage formatted string without escaped tags
     */
    @SuppressWarnings("UnstableApiUsage")
    private String unescape(@NotNull String input) {
        List<Token> tokens = TokenParser.tokenize(input, false);
        tokens.sort(Comparator.comparingInt(Token::startIndex));
        StringBuilder output = new StringBuilder();
        int lastIndex = 0;
        for (Token token : tokens) {
            int start = token.startIndex();
            int end = token.endIndex();
            if (lastIndex < start) output.append(input, lastIndex, start);
            output.append(
                    TokenParser.unescape(input.substring(start, end),
                            0, end - start,
                            escape -> escape == TokenParser.TAG_START || escape == TokenParser.ESCAPE)
            );
            lastIndex = end;
        }

        if (lastIndex < input.length()) output.append(input.substring(lastIndex));
        return output.toString();
    }

    /**
     * Formats a message with placeholders.
     *
     * @param message message with placeholders
     * @param args    placeholders to replace
     * @return formatted string
     */
    @SafeVarargs
    public String replace(String message, Tuple<String, Object>... args) {
        for (Tuple<String, Object> replacement : args) {
            if (!message.contains(replacement.first())) continue;

            if (replacement.second() instanceof Component comp) {
                message = message.replace(replacement.first(), legacySerializer.serialize(comp));
                continue;
            }

            message = message.replace(replacement.first(), String.valueOf(replacement.second()));
        }
        return message;
    }
}
