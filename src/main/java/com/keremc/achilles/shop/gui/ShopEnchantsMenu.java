package com.keremc.achilles.shop.gui;

import com.keremc.achilles.shop.ShopEnchantment;
import com.keremc.achilles.shop.gui.button.ClearInventoryButton;
import com.keremc.achilles.shop.gui.button.FillWithSoupButton;
import com.keremc.achilles.shop.gui.button.OpenOtherMenuButton;
import com.keremc.achilles.shop.gui.button.ShopEnchantmentButton;
import com.keremc.core.menu.Button;
import com.keremc.core.menu.Menu;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ShopEnchantsMenu extends Menu {
    private static Map<Material, Enchantment> enchantmentIcon = new LinkedHashMap<>();

    static {
        enchantmentIcon.put(Material.ANVIL, Enchantment.DURABILITY);

        enchantmentIcon.put(Material.DIAMOND_SWORD, Enchantment.DAMAGE_ALL);
        enchantmentIcon.put(Material.EMERALD, Enchantment.KNOCKBACK);

        enchantmentIcon.put(Material.FLINT_AND_STEEL, Enchantment.FIRE_ASPECT);


        enchantmentIcon.put(Material.LAVA_BUCKET, Enchantment.ARROW_FIRE);
        enchantmentIcon.put(Material.ARROW, Enchantment.ARROW_DAMAGE);
        enchantmentIcon.put(Material.BOW, Enchantment.ARROW_KNOCKBACK);
        enchantmentIcon.put(Material.QUARTZ, Enchantment.ARROW_INFINITE);


        enchantmentIcon.put(Material.DIAMOND_BARDING, Enchantment.PROTECTION_ENVIRONMENTAL);


    }

    @Override
    public String getTitle(Player player) {
        return "Enchanments";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(0, new ClearInventoryButton(this));
        buttons.put(4, new OpenOtherMenuButton(false));
        buttons.put(8, new FillWithSoupButton());

        int i = 0;
        for (Map.Entry<Material, Enchantment> entry : enchantmentIcon.entrySet()) {
            Material mat = entry.getKey();
            Enchantment e = entry.getValue();

            int maxLevel = Math.min(e.getMaxLevel(), 4);

            ShopEnchantment dummyName = new ShopEnchantment(e, 1, -1);
            String[] tmp = dummyName.getFriendlyName(false).split(" ");
            tmp[tmp.length - 1] = "";
            String name = StringUtils.join(tmp, " ").trim();

            buttons.put(9 + i, Button.placeholder(mat, (byte) 0, "§6" + name));

            for (int level = 1; level <= maxLevel; level++) {
                buttons.put((9 + i) + (9 * level), new ShopEnchantmentButton(new ShopEnchantment(e, level, 100)));
            }

            i++;
        }

        return buttons;
    }
}
