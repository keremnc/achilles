package com.keremc.achilles.command;

import com.keremc.achilles.data.SaveHandler;
import com.keremc.core.command.Command;
import org.bukkit.entity.Player;

public class ForceSaveCommand {

    @Command(names = {"forcesave"}, permissionNode = "achilles.save")
    public static void forcesave(Player sender) {
        SaveHandler.saveAll();
    }
}
