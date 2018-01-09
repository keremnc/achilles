package com.keremc.achilles.shop.gui.button;

import com.keremc.core.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class FillWithSoupButton extends Button {
    @Override
    public String getName(Player player) {
        return "§aFill inventory with soup";
    }

    @Override
    public List<String> getDescription(Player player) {
        return null;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.MUSHROOM_SOUP;
    }

    @Override
    public byte getDamageValue(Player player) {
        return 0;
    }

    @Override
    public void clicked(Player player, int i, ClickType clickType, int hotbar) {
        while (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP));
        }
    }
}
