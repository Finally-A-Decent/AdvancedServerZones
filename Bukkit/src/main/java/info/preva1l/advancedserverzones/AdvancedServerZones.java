package info.preva1l.advancedserverzones;

import info.preva1l.advancedserverzones.api.ImplAdvancedServerZonesAPI;
import info.preva1l.advancedserverzones.config.Config;
import info.preva1l.advancedserverzones.chat.ChatSyncService;
import info.preva1l.hooker.Hooker;
import info.preva1l.hooker.HookerOptions;
import info.preva1l.trashcan.extension.BasePlugin;
import info.preva1l.trashcan.extension.annotations.PluginEnable;
import info.preva1l.trashcan.extension.annotations.PluginLoad;

public final class AdvancedServerZones extends BasePlugin {
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
        if (Config.i().getChatsync().isEnabled()) {
            ChatSyncService chatSync = new ChatSyncService();
            getServer().getPluginManager().registerEvents(chatSync, this);
        }

        AdvancedServerZonesAPI.setInstance(new ImplAdvancedServerZonesAPI());

        getLogger().info("Advanced Server Zones Loaded");
    }
}
