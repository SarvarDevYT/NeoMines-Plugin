package me.neomines.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface CommandInterface {
    boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args);
}
