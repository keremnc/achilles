package com.keremc.achilles.command;

import com.keremc.achilles.match.data.PostMatchData;
import com.keremc.achilles.match.gui.PostMatchDataMenu;
import com.keremc.core.command.Command;
import com.keremc.core.command.param.Parameter;
import com.keremc.core.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ViewMatchInvCommand {

    private static Map<UUID, Map<UUID, PostMatchData>> postMatchData = new HashMap<>();

    public static void saveInventoryView(UUID player, PostMatchData playerInventory, UUID... targets) {
        for (UUID target : targets) {
            postMatchData.putIfAbsent(target, new HashMap<>());
            postMatchData.get(target).put(player, playerInventory);
        }
    }

    public static PostMatchData getPostMatchData(UUID sender, UUID target) {
        if (!postMatchData.containsKey(sender)) {
            return (null);
        }

        Map<UUID, PostMatchData> personalViews = postMatchData.get(sender);

        if (personalViews.containsKey(target)) {
            return (personalViews.get(target));
        } else {
            return (null);
        }
    }


    @Command(names = {"viewmatchinv"}, permissionNode = "")
    public static void viewmatchinv(Player sender, @Parameter(name = "player") UUID player) {
        PostMatchData postMatchData = getPostMatchData(sender.getUniqueId(), player);

        if (postMatchData != null) {
            (new PostMatchDataMenu(UUIDUtils.name(player), postMatchData)).openMenu(sender);
        } else {
            sender.sendMessage(ChatColor.RED + "No data found.");
        }
    }
}
