package com.keremc.achilles.loadout.gui.button;

import com.keremc.achilles.loadout.MatchLoadout;
import com.keremc.achilles.loadout.gui.menu.MyLoadoutsMenu;
import com.keremc.core.menu.Button;
import com.keremc.core.util.Callback;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

@AllArgsConstructor
public class MyLoadoutsButton extends Button {
    private Callback<MatchLoadout> callback;
    private boolean queue;

    @Override
    public String getName(Player player) {
        return "§aCustom Loadouts";
    }

    @Override
    public List<String> getDescription(Player player) {
        return null;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.CHEST;
    }

    @Override
    public byte getDamageValue(Player player) {
        return 0;
    }

    @Override
    public void clicked(Player player, int i, ClickType clickType, int hotbar) {
        new MyLoadoutsMenu(callback, queue).openMenu(player);
    }
}
