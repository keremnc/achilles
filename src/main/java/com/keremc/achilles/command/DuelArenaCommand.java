package com.keremc.achilles.command;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.data.PlayerData;
import com.keremc.achilles.chat.Style;
import com.keremc.core.command.Command;
import org.bukkit.entity.Player;

public class DuelArenaCommand {

    @Command(names = {"1v1", "duel"}, permissionNode = "")
    public static void duel(Player sender) {

        PlayerData pd = AchillesPlugin.getInstance().getPlayerHandler().getSession(sender.getUniqueId());

        if (!pd.isSpawnProt()) {
            sender.sendMessage("§cYou must have spawn protection to warp to the duel arena.");
            return;
        }

        pd.warp(AchillesPlugin.getInstance().getPlayerHandler().chooseDuelSpawnLocation(), 10, () -> {
                    pd.addArenaItems();
                    sender.sendMessage(Style.getDuelHelp());
                    pd.setDuelMode(true);
                    pd.setSelectedKit(null);
                }
        );
    }
}
