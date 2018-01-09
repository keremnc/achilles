package com.keremc.achilles.command.params;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.chat.Style;
import com.keremc.achilles.match.PlayerMatchInvite;
import com.keremc.core.command.param.ParameterType;
import com.keremc.core.util.UUIDUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ChallengerParameterType implements ParameterType<ChallengerParameterType.Challenger> {

    private Set<String> getChallengers(CommandSender sender) {
        Set<String> names = new HashSet<>();

        for (PlayerMatchInvite pmi : AchillesPlugin.getInstance().getMatchHandler().getAllPlayerInvites(((Player) sender).getUniqueId())) {
            names.add(UUIDUtils.name(pmi.getSender()));
        }

        return names;
    }


    @Override
    public Challenger transform(CommandSender commandSender, String s) {

        if (s.equalsIgnoreCase("§")) {
            return new Challenger(null);
        }

        for (String str : getChallengers(commandSender)) {
            if (str.equalsIgnoreCase(s)) {
                return new Challenger(str);
            }
        }

        commandSender.sendMessage(Style.header("§eNo pending match invites from '§c" + s + "'§e"));
        return null;

    }

    @Override
    public List<String> tabComplete(Player player, Set<String> set, String s) {
        return getChallengers(player).stream().filter(n -> StringUtils.startsWithIgnoreCase(n, s)).collect(Collectors.toList());
    }

    @AllArgsConstructor
    @Data
    public static class Challenger {
        private String name;

    }
}
