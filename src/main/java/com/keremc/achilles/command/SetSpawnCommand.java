package com.keremc.achilles.command;

import com.keremc.core.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SetSpawnCommand {

    @Command(names = "setspawn", permissionNode = "achilles.admin")
    public static void setspawn(Player sender) {
        Location l = sender.getLocation();
        sender.sendMessage(ChatColor.YELLOW + "Spawn set!");
        sender.getWorld().setSpawnLocation(l.getBlockX(), l.getBlockY(), l.getBlockZ());
    }
}
