package lol.arch.advancedserverzones;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@SuppressWarnings("unused")
public class ChatSyncSendEvent extends Event {
    @Getter private static final HandlerList handlerList = new HandlerList();
    @Getter private final UUID sender;
    @Getter private final String message;

    public ChatSyncSendEvent(UUID sender, String message) {
        super(true);
        this.sender = sender;
        this.message = message;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
