package com.keremc.achilles.loadout.builder.gui.button;

import com.keremc.achilles.loadout.builder.CustomPart;
import com.keremc.achilles.loadout.builder.values.CustomEnchantmentValue;
import com.keremc.achilles.loadout.builder.values.CustomValue;
import com.keremc.core.menu.Button;
import com.keremc.core.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class CustomPartButton extends Button {
    private CustomPart customPart;

    @Override
    public String getName(Player player) {
        return "§e" + customPart.getTitle();
    }

    @Override
    public List<String> getDescription(Player player) {
        ArrayList<String> str = new ArrayList<>();
        str.add("");
        str.add("§6§lLeft-Click");
        str.add("§9  " + customPart.getPrimaryDisplay() + ":");
        for (int i = 0; i < customPart.getPrimary().length; i += 1) {
            String head = customPart.getSelected() == i ? "§a  ► " : "§c    ";

            str.add(head + customPart.getPrimary()[i].getName());

        }
        if (customPart.hasSecondary()) {
            str.add("");
            str.add("§6§lRight-Click");

            str.add("§b  " + customPart.getSecondaryDisplay() + ":");

            for (int i = 0; i < customPart.getSecondary().length; i += 1) {

                String head = customPart.getSecondarySelected() == i ? "§a  ► " : "§c    ";

                CustomValue secsel = customPart.getSecondary()[i];


                str.add(head + secsel.getName());
            }
        }
        return str;
    }

    @Override
    public Material getMaterial(Player player) {
        return customPart.getCurrentPrimary().getDisplay();
    }

    @Override
    public byte getDamageValue(Player player) {
        return (byte) customPart.getCurrentPrimary().getShortData();
    }

    @Override
    public Map<Enchantment, Integer> getEnchantments() {
        Map<Enchantment, Integer> enchantments = new HashMap<>();

        if (customPart.hasSecondary()) {
            CustomValue secsel = customPart.getCurrentSecondary();

            if (secsel instanceof CustomEnchantmentValue) {
                Enchantment e = ((CustomEnchantmentValue) secsel).getData();
                int level = secsel.getIntData();

                enchantments.put(e, level);
            }

        }
        return enchantments;
    }

    @Override
    public int getAmount(Player player) {
        return customPart.getCurrentPrimary().getIntData();
    }

    @Override
    public void clicked(Player player, int i, ClickType clickType, int hotbar) {
        if (clickType.isLeftClick()) {
            customPart.incrementPrimary();
        } else if (clickType.isRightClick()) {
            customPart.incrementSecondary();
        } else if (clickType.isKeyboardClick()) {
            customPart.jumpTo(hotbar);
        }

        Menu.updateButton(player, this);
        Button.playNeutral(player);
    }
}
