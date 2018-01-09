package com.keremc.achilles.shop.gui.button;

import com.keremc.core.menu.Button;
import com.keremc.core.menu.Menu;
import com.keremc.core.menu.menus.ConfirmMenu;
import com.keremc.core.util.PlayerUtils;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

@AllArgsConstructor
public class ClearInventoryButton extends Button {
    private Menu currentMenu;

    @Override
    public String getName(Player player) {
        return "§cClear inventory";
    }

    @Override
    public List<String> getDescription(Player player) {
        return null;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.TNT;
    }

    @Override
    public byte getDamageValue(Player player) {
        return 0;
    }

    @Override
    public void clicked(Player player, int i, ClickType clickType, int hotbar) {
        new ConfirmMenu("Clear inventory?", b -> {
            if (b) {
                PlayerUtils.resetInventory(player);
            }
            currentMenu.openMenu(player);

        }, false).openMenu(player);
    }
}
