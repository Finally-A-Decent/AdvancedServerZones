package lol.arch.survival.api;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
@SuppressWarnings("unused")
public class ChatSyncReceiveEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final UUID sender;
    private final String message;

    public ChatSyncReceiveEvent(UUID sender, String message) {
        this.sender = sender;
        this.message = message;
        Bukkit.getServer().getPluginManager().callEvent(this);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
