package com.keremc.achilles.kit.gui.button;

import com.keremc.achilles.kit.gui.RentalMenu;
import com.keremc.core.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

public class ViewRentalsButton extends Button {
    @Override
    public String getName(Player player) {
        return ChatColor.YELLOW + "My Rentals";
    }

    @Override
    public List<String> getDescription(Player player) {
        return null;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.EMERALD;
    }

    @Override
    public byte getDamageValue(Player player) {
        return 0;
    }

    @Override
    public void clicked(Player player, int i, ClickType clickType, int hotbar) {
        new RentalMenu().openMenu(player);
    }
}
