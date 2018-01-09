package com.keremc.achilles.command.params;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.kit.Kit;
import com.keremc.core.command.param.ParameterType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class KitParameterType implements ParameterType<Kit> {

    @Override
    public Kit transform(CommandSender commandSender, String s) {
        for (Kit kit : AchillesPlugin.getInstance().getKitHandler().getKits()) {
            if (kit.getName().equalsIgnoreCase(s)) {
                return kit;
            }
        }

        commandSender.sendMessage(ChatColor.RED + "Error: No kit '" + s + "' could be found.");
        return null;
    }

    @Override
    public List<String> tabComplete(Player player, Set<String> set, String s) {
        List<String> tab = new ArrayList<>();

        AchillesPlugin.getInstance().getKitHandler().getKits()
                .stream().filter(kit -> StringUtil.startsWithIgnoreCase(kit.getName(), s))
                .map(kit -> kit.getName())
                .collect(Collectors.toCollection(() -> tab));
        return tab;
    }
}
