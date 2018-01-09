package com.keremc.achilles.shop.gui;

import com.keremc.achilles.shop.ShopItem;
import com.keremc.achilles.shop.gui.button.ClearInventoryButton;
import com.keremc.achilles.shop.gui.button.FillWithSoupButton;
import com.keremc.achilles.shop.gui.button.OpenOtherMenuButton;
import com.keremc.achilles.shop.gui.button.ShopItemButton;
import com.keremc.core.menu.Button;
import com.keremc.core.menu.Menu;
import com.keremc.core.util.ItemUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ShopItemsMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "Shop";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(0, new ClearInventoryButton(this));
        buttons.put(4, new OpenOtherMenuButton(true));
        buttons.put(8, new FillWithSoupButton());

        for (ArmorType at : ArmorType.values()) {
            int column = 9 + at.ordinal();
            ShopItemButton[] armorButtons = armorButton(at, at.getPrice());

            for (int i = 0; i <= 3; i++) {
                buttons.put(column + (i * 9), armorButtons[i]);
            }
        }

        buttons.put(15, new ShopItemButton(new ShopItem(new ItemUtils.ItemData(Material.DIAMOND_SWORD, (short) 0), 1, 350)));
        buttons.put(24, new ShopItemButton(new ShopItem(new ItemUtils.ItemData(Material.IRON_SWORD, (short) 0), 1, 250)));
        buttons.put(33, new ShopItemButton(new ShopItem(new ItemUtils.ItemData(Material.GOLD_SWORD, (short) 0), 1, 150)));
        buttons.put(42, new ShopItemButton(new ShopItem(new ItemUtils.ItemData(Material.STONE_SWORD, (short) 0), 1, 50)));

        buttons.put(17, new ShopItemButton(new ShopItem(new ItemUtils.ItemData(Material.BOW, (short) 0), 1, 200)));
        buttons.put(26, new ShopItemButton(new ShopItem(new ItemUtils.ItemData(Material.ARROW, (short) 0), 64, 50)));

        return buttons;
    }

    private static ShopItemButton[] armorButton(ArmorType at, int basePrice) {
        return new ShopItemButton[]{
                new ShopItemButton(new ShopItem(new ItemUtils.ItemData(of(at, "HELMET"), (short) 0), 1, basePrice + 50)),
                new ShopItemButton(new ShopItem(new ItemUtils.ItemData(of(at, "CHESTPLATE"), (short) 0), 1, basePrice + 350)),
                new ShopItemButton(new ShopItem(new ItemUtils.ItemData(of(at, "LEGGINGS"), (short) 0), 1, basePrice + 300)),
                new ShopItemButton(new ShopItem(new ItemUtils.ItemData(of(at, "BOOTS"), (short) 0), 1, basePrice + 100))

        };
    }

    private static Material of(ArmorType at, String part) {
        return Material.valueOf(at.name() + "_" + part.toUpperCase());
    }

    @AllArgsConstructor
    private enum ArmorType {
        DIAMOND(1500), IRON(1000), GOLD(900), CHAINMAIL(750), LEATHER(500);

        @Getter int price;
    }
}
