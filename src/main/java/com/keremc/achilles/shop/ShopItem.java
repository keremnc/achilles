package com.keremc.achilles.shop;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.data.PlayerData;
import com.keremc.achilles.chat.Style;
import com.keremc.achilles.shop.gui.ShopItemsMenu;
import com.keremc.core.util.ItemUtils;
import com.keremc.core.util.PlayerUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
@Data
public class ShopItem implements ShopPurchasable {
    private ItemUtils.ItemData itemData;
    private int amount;
    private int price;

    public String getFriendlyName() {
        return ItemUtils.getName(itemData.toItemStack());
    }

    public void handleBuy(Player player) {
        PlayerData pd = AchillesPlugin.getInstance().getPlayerHandler().getSession(player.getUniqueId());

        if (pd.getBalance() < getPrice()) {
            player.sendMessage(Style.header(ChatColor.RED + "You have insufficient tokens!"));
            return;
        }

        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(Style.header("§cYour inventory is full!"));
            return;
        }

        if (!pd.isCustomInv()) {
            PlayerUtils.resetInventory(player, GameMode.SURVIVAL);
        }

        ItemStack add = itemData.toItemStack();
        add.setAmount(amount);

        player.getInventory().addItem(add);
        player.sendMessage(Style.header("You have purchased §6" + amount + "§e ✕ §b" + getFriendlyName()));
        purchased(pd);
        new ShopItemsMenu().openMenu(player);
    }

}
