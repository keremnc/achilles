package com.keremc.achilles.kit.command;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.kit.Kit;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;

public class CommandManager implements CommandExecutor {
    private CommandMap commandMap;

    public CommandManager() {
        if (Bukkit.getPluginManager() instanceof SimplePluginManager) {
            SimplePluginManager pluginManager = (SimplePluginManager)Bukkit.getPluginManager();
            try {
                Field field = SimplePluginManager.class.getDeclaredField("commandMap");
                field.setAccessible(true);
                this.commandMap = (CommandMap)field.get(pluginManager);
            }
            catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    public void register() {
        for (Kit kit : AchillesPlugin.getInstance().getKitHandler().getKits()) {
            BukkitCommand command = new BukkitCommand(kit.getName(), this, AchillesPlugin.getInstance());
            this.commandMap.register(kit.getName(), command);
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Kit kit = AchillesPlugin.getInstance().getKitHandler().getKitByName(command.getName());
        ((Player) sender).chat("/kit " + kit.getName());

        return false;
    }

    public CommandMap getCommandMap() {
        return this.commandMap;
    }
}

