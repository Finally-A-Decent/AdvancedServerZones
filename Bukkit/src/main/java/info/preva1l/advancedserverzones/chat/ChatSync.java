package info.preva1l.advancedserverzones.chat;

import info.preva1l.advancedserverzones.config.Config;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public final class ChatSync implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    private void chatMessageListener(AsyncChatEvent event) {
        event.setCancelled(true);
        Config.i().getChatsync().getMode().execute(event);
    }
}
