package info.preva1l.advancedserverzones;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@SuppressWarnings("unused")
public class ChatSyncReceiveEvent extends Event {
    @Getter private static final HandlerList handlerList = new HandlerList();
    @Getter private final UUID sender;
    @Getter private final Component message;

    public ChatSyncReceiveEvent(UUID sender, Component message) {
        super(true);
        this.sender = sender;
        this.message = message;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
