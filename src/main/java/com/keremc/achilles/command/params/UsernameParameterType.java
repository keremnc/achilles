package com.keremc.achilles.command.params;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.data.db.DataHandler;
import com.keremc.achilles.util.ListUtils;
import com.keremc.core.command.param.ParameterType;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UsernameParameterType implements ParameterType<UsernameParameterType.Username> {


    @Override
    public Username transform(CommandSender commandSender, String s) {
        return new Username(s.equalsIgnoreCase("§") ? commandSender.getName() : s);
    }

    @Override
    public List<String> tabComplete(Player player, Set<String> set, String s) {
        return DataHandler.cachedNames.stream().filter(n -> StringUtils.startsWithIgnoreCase(n, s)).collect(Collectors.toList());
    }

    @AllArgsConstructor
    @Data
    public static class Username {
        private String name;

        public void notFound(Player sender) {
            TextComponent header = new TextComponent("Player '" + name + "' could not be found.");
            header.setColor(net.md_5.bungee.api.ChatColor.RED);

            sender.spigot().sendMessage(header);

            Bukkit.getScheduler().runTaskAsynchronously(AchillesPlugin.getInstance(), () -> autocorrect(sender, name));

        }

        public static void autocorrect(Player player, String entry) {

            String[] maybeNames = ListUtils.sortListBySearching(new ArrayList<>(DataHandler.cachedNames), entry);


            if (maybeNames != null && maybeNames.length > 0) {
                TextComponent tc = new TextComponent("Did you mean: ");
                tc.setColor(net.md_5.bungee.api.ChatColor.GRAY);

                player.spigot().sendMessage(tc);

                for (String str : maybeNames) {

                    ComponentBuilder cb = new ComponentBuilder("     ");
                    cb.append(str).color(ChatColor.YELLOW);
                    cb.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/stats " + str));

                    player.spigot().sendMessage(cb.create());

                }
            }

        }
    }
}
