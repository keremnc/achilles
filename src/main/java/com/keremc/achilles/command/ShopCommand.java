package com.keremc.achilles.command;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.data.PlayerData;
import com.keremc.achilles.chat.Style;
import com.keremc.achilles.shop.gui.ShopItemsMenu;
import com.keremc.core.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ShopCommand {
    @Command(names = "shop", permissionNode = "")
    public static void shop(Player sender) {

        PlayerData pd = AchillesPlugin.getInstance().getPlayerHandler().getSession(sender.getUniqueId());

        if (pd.getSelectedKit() != null) {

            sender.sendMessage(Style.header("You cannot do this with a kit equipped."));
            sender.sendMessage(Style.header("Please type §c/clearkit §eto clear your kit."));
            return;
        }

        if (!pd.isSpawnProt()) {
            sender.sendMessage(ChatColor.RED + "You may only use the shop with spawn protection.");
            return;
        }

        new ShopItemsMenu().openMenu(sender);

    }
}
