package com.keremc.achilles.command;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.data.PlayerData;
import com.keremc.core.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class BuildCommand {

    @Command(names = "build", permissionNode = "achilles.build")
    public static void build(Player sender) {
        PlayerData pd = AchillesPlugin.getInstance().getPlayerHandler().getSession(sender.getUniqueId());

        pd.setBuild(!pd.isBuild());
        sender.sendMessage(ChatColor.YELLOW + "Build " + (pd.isBuild() ? "§aenabled" : "§cdisabled"));
    }
}
