package com.keremc.achilles.command;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.data.PlayerData;
import com.keremc.achilles.chat.Style;
import com.keremc.achilles.kit.Kit;
import com.keremc.core.command.Command;
import com.keremc.core.command.param.Parameter;
import com.keremc.core.util.PlayerUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class KitCommands {

    @Command(names = "kit")
    public static void kit(Player sender, @Parameter(name = "kit") Kit kit) {
        PlayerData pd = AchillesPlugin.getInstance().getPlayerHandler().getSession(sender.getUniqueId());

        if (!pd.ownsKit(kit)) {
            sender.sendMessage(ChatColor.RED + "You do not own this kit! Rent in the kit menu or purchase a rank.");
            return;
        }

        if (pd.getSelectedKit() != null && !pd.isSpawnProt()) {
            sender.sendMessage(ChatColor.RED + "You must have spawn protection to change your kit!");
            return;
        }

        kit.applyKit(sender);
    }

    @Command(names = {"clearkit", "nokit", "removekit"})
    public static void clearKit(Player sender) {
        PlayerData pd = AchillesPlugin.getInstance().getPlayerHandler().getSession(sender.getUniqueId());


        if (pd.getSelectedKit() == null) {
            sender.sendMessage(ChatColor.RED + "You do not have a kit equipped!");
            return;
        }

        if (!pd.isSpawnProt()) {
            sender.sendMessage(ChatColor.RED + "You must have spawn protection to remove your kit.");
            return;
        }

        pd.setSelectedKit(null);
        PlayerUtils.resetInventory(sender, GameMode.SURVIVAL);
        pd.addSpawnItems();

        sender.sendMessage(Style.header("Your kit has been cleared. You may now use the shop!"));
    }
}
