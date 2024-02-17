package lol.arch.survival.commands;

import lol.arch.survival.LoadDistribution;
import lol.arch.survival.config.Config;
import lol.arch.survival.util.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        LoadDistribution.getInstance().reloadConfig();
        Config.reload();
        commandSender.sendMessage(StringUtils.colorize(Config.Messages.getReloaded()));
        return true;
    }
}
