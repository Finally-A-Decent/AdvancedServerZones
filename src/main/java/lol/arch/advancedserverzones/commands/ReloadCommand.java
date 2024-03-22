package lol.arch.advancedserverzones.commands;

import lol.arch.advancedserverzones.AdvancedServerZones;
import lol.arch.advancedserverzones.config.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        AdvancedServerZones.getInstance().getConfigFile().load();
        AdvancedServerZones.getInstance().getServersFile().load();
        AdvancedServerZones.getInstance().getLangFile().load();
        commandSender.sendMessage(Lang.RELOADED.toFormattedComponent());
        return true;
    }
}
