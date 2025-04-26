package info.preva1l.advancedserverzones;

import info.preva1l.advancedserverzones.api.ImplAdvancedServerZonesAPI;
import info.preva1l.advancedserverzones.config.Config;
import info.preva1l.advancedserverzones.chat.ChatSync;
import info.preva1l.advancedserverzones.listeners.PreventInteractionsNearBorder;
import info.preva1l.advancedserverzones.chat.PlaceholderManager;
import info.preva1l.advancedserverzones.borders.ConnectionService;
import info.preva1l.advancedserverzones.util.Logger;
import info.preva1l.trashcan.plugin.BasePlugin;
import info.preva1l.trashcan.plugin.annotations.PluginEnable;
import lombok.Getter;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.stream.Stream;

@Getter
public class AdvancedServerZones extends BasePlugin {
    private static final String PURCHASER = "%%__USERNAME__%%";
    public static final @SuppressWarnings("ConstantValue") boolean VALID_PURCHASE = !PURCHASER.contains("__USERNAME__");

    private static AdvancedServerZones instance;

    private ChatSync chatSync;

    public AdvancedServerZones() {
        instance = this;
    }

    @PluginEnable
    public void enable() {
        Stream.of(
                ConnectionService.instance,
                new PreventInteractionsNearBorder()
        ).forEach(e -> getServer().getPluginManager().registerEvents(e, this));

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        if (Config.i().getChatsync().isEnabled()) {
            chatSync = new ChatSync();
            getServer().getPluginManager().registerEvents(chatSync, this);

            if (getServer().getPluginManager().getPlugin("Vault") != null) {
                RegisteredServiceProvider<Chat> registration = getServer().getServicesManager().getRegistration(Chat.class);
                if (registration != null) PlaceholderManager.setChat(registration.getProvider());
            }
        }

        AdvancedServerZonesAPI.setInstance(new ImplAdvancedServerZonesAPI());

        Logger.info("Advanced Server Zones Loaded");
        if (VALID_PURCHASE) {
            Logger.info("Licenced to: " + PURCHASER);
        } else {
            Logger.info("Running free version!");
        }
    }

    public static AdvancedServerZones i() {
        return instance;
    }
}
