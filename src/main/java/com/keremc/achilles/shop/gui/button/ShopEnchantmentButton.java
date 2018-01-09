package com.keremc.achilles.shop.gui.button;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.data.PlayerData;
import com.keremc.achilles.shop.ShopEnchantment;
import com.keremc.achilles.shop.gui.SelectEnchantedItemMenu;
import com.keremc.core.menu.Button;
import com.keremc.core.util.ItemUtils;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class ShopEnchantmentButton extends Button {
    private ShopEnchantment shopEnchantment;

    @Override
    public String getName(Player player) {
        return "§9" + shopEnchantment.getFriendlyName(false);
    }

    @Override
    public List<String> getDescription(Player player) {
        ShopEnchantment.StackData[] enchantable = shopEnchantment.getEnchantables(player);

        PlayerData pd = AchillesPlugin.getInstance().getPlayerHandler().getSession(player.getUniqueId());

        List<String> lore = new ArrayList<>();
        lore.add("");

        if (enchantable.length == 0) {
            lore.add("§cThis enchantment cannot be applied");
            lore.add("§cto any of your items.");
        } else if (enchantable.length == 1) {
            lore.add("§eClick to enchant your §d" + ItemUtils.getName(enchantable[0].item));
            lore.add("§ein §6slot " + enchantable[0].slot);
        } else {
            lore.add("§eClick to select an item to enchant");
        }

        boolean afford = pd.getBalance() >= shopEnchantment.getPrice();

        lore.add("");
        lore.add("§6Price§f: " + (afford ? "§a" : "§c") + shopEnchantment.getPrice() + " tokens");

        return lore;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.ENCHANTED_BOOK;
    }

    @Override
    public byte getDamageValue(Player player) {
        return 0;
    }

    @Override
    public void clicked(Player player, int i, ClickType clickType, int hotbar) {
        ShopEnchantment.StackData[] enchantable = shopEnchantment.getEnchantables(player);

        List<String> lore = new ArrayList<>();
        lore.add("");

        if (enchantable.length == 1) {
            shopEnchantment.handleBuy(player, enchantable[0]);
        } else if (enchantable.length > 1) {
            new SelectEnchantedItemMenu(shopEnchantment).openMenu(player);
        }

    }

}

