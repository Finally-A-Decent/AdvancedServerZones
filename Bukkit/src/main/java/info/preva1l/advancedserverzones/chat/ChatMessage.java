package info.preva1l.advancedserverzones.chat;

import com.google.gson.annotations.Expose;
import net.kyori.adventure.text.Component;

import java.util.UUID;

public record ChatMessage(
        @Expose UUID player,
        @Expose Component message
) {
}
