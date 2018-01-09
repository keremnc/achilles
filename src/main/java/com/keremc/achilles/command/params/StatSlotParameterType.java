package com.keremc.achilles.command.params;

import com.keremc.achilles.statistics.StatSlot;
import com.keremc.core.command.param.ParameterType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StatSlotParameterType implements ParameterType<StatSlot> {

    @Override
    public StatSlot transform(CommandSender commandSender, String s) {
        StatSlot ss = StatSlot.parse(s);

        if (ss != null) {
            return ss;
        }
        commandSender.sendMessage(ChatColor.RED + "Error: No stat '" + s + "' exists.");
        return null;
    }

    @Override
    public List<String> tabComplete(Player player, Set<String> set, String s) {
        List<String> tab = new ArrayList<>();

        Stream.of(StatSlot.values())
                .map(ss -> ss.getFriendlyName().replace(" ", ""))
                .filter(ss -> StringUtil.startsWithIgnoreCase(ss, s))
                .collect(Collectors.toCollection(() -> tab));
        return tab;
    }
}
