package com.keremc.achilles.command;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.data.PlayerData;
import com.keremc.core.command.Command;
import org.bukkit.entity.Player;

public class SpawnCommand {
    @Command(names = "spawn", permissionNode = "")
    public static void spawn(Player sender) {

        PlayerData pd = AchillesPlugin.getInstance().getPlayerHandler().getSession(sender.getUniqueId());

        pd.warp(AchillesPlugin.getInstance().getPlayerHandler().getSpawnLocation(), 10, () -> {
            pd.setSpawnProt(true);
            pd.setDuelMode(false);
            AchillesPlugin.getInstance().getMatchHandler().cleanup(sender.getUniqueId());

            if (pd.getSelectedKit() == null && !pd.isCustomInv()) {
                pd.addSpawnItems();
            }
        });
    }
}
