package com.keremc.achilles.shop.gui;

import com.keremc.achilles.shop.ShopEnchantment;
import com.keremc.achilles.shop.gui.button.EnchantItemButton;
import com.keremc.core.menu.Button;
import com.keremc.core.menu.Menu;
import com.keremc.core.menu.buttons.BackButton;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class SelectEnchantedItemMenu extends Menu {
    private ShopEnchantment enchant;

    @Override
    public String getTitle(Player player) {
        return enchant.getFriendlyName(false);
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(4, new BackButton(new ShopItemsMenu()));
        int i = 0;

        ShopEnchantment.StackData[] enchantables = enchant.getEnchantables(player);

        for (ShopEnchantment.StackData sd : enchantables) {
            buttons.put(10 + i++, new EnchantItemButton(enchant, sd));
            if ((i + 2) % 9 == 0) {
                i += 2;
            }
        }


        return buttons;
    }
}
