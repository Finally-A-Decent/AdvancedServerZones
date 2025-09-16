package info.preva1l.advancedserverzones;

import info.preva1l.advancedserverzones.api.ImplAdvancedServerZonesAPI;
import info.preva1l.advancedserverzones.config.Config;
import info.preva1l.advancedserverzones.chat.ChatSync;
import info.preva1l.advancedserverzones.listeners.PreventInteractionsNearBorder;
import info.preva1l.advancedserverzones.chat.PlaceholderManager;
import info.preva1l.advancedserverzones.util.Logger;
import info.preva1l.hooker.Hooker;
import info.preva1l.hooker.HookerOptions;
import info.preva1l.trashcan.extension.BasePlugin;
import info.preva1l.trashcan.extension.annotations.PluginEnable;
import info.preva1l.trashcan.extension.annotations.PluginLoad;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.plugin.RegisteredServiceProvider;

public class AdvancedServerZones extends BasePlugin {
    public static AdvancedServerZones instance;

    public AdvancedServerZones() {
        instance = this;
    }

    @PluginLoad
    public void load() {
        Hooker.register(this.getClass(), new HookerOptions("info.preva1l.advancedserverzones.hooks"));
    }

    @PluginEnable
    public void enable() {
        getServer().getPluginManager().registerEvents(new PreventInteractionsNearBorder(), this);

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        if (Config.i().getChatsync().isEnabled()) {
            ChatSync chatSync = new ChatSync();
            getServer().getPluginManager().registerEvents(chatSync, this);

            if (getServer().getPluginManager().getPlugin("Vault") != null) {
                RegisteredServiceProvider<Chat> registration = getServer().getServicesManager().getRegistration(Chat.class);
                if (registration != null) PlaceholderManager.setChat(registration.getProvider());
            }
        }

        AdvancedServerZonesAPI.setInstance(new ImplAdvancedServerZonesAPI());

        Logger.info("Advanced Server Zones Loaded");
    }
}
