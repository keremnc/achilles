package com.keremc.achilles.shop.gui.button;

import com.keremc.achilles.shop.gui.ShopEnchantsMenu;
import com.keremc.achilles.shop.gui.ShopItemsMenu;
import com.keremc.core.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

@AllArgsConstructor
public class OpenOtherMenuButton extends Button {
    private boolean enchants;

    @Override
    public String getName(Player player) {
        return "§eOpen " + (enchants ? "enchant" : "item") + " shop";
    }

    @Override
    public List<String> getDescription(Player player) {
        return null;
    }

    @Override
    public Material getMaterial(Player player) {
        return enchants ? Material.ENCHANTMENT_TABLE : Material.DIAMOND_SWORD;
    }

    @Override
    public byte getDamageValue(Player player) {
        return 0;
    }

    @Override
    public void clicked(Player player, int i, ClickType clickType, int hotbar) {
        (enchants ? new ShopEnchantsMenu() : new ShopItemsMenu()).openMenu(player);
    }
}
