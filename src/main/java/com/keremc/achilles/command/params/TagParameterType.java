package com.keremc.achilles.command.params;

import com.keremc.achilles.region.Tag;
import com.keremc.core.command.param.ParameterType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TagParameterType implements ParameterType<Tag> {

    @Override
    public Tag transform(CommandSender commandSender, String s) {
        for (Tag tag : Tag.values()) {
            if (tag.name().equalsIgnoreCase(s)) {
                return tag;
            }
        }

        commandSender.sendMessage(ChatColor.RED + "Error: No tag '" + s + "' could be found.");
        return null;
    }

    @Override
    public List<String> tabComplete(Player player, Set<String> set, String s) {
        List<String> tab = new ArrayList<>();

        for (Tag tag : Tag.values()) {
            if (StringUtil.startsWithIgnoreCase(tag.name(), s)) {
                tab.add(tag.name());
            }
        }
        return tab;
    }
}
