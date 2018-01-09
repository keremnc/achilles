package com.keremc.achilles.loadout.builder.gui.button;

import com.keremc.achilles.loadout.MatchLoadout;
import com.keremc.achilles.loadout.builder.CustomLoadoutData;
import com.keremc.core.menu.Button;
import com.keremc.core.util.Callback;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

@AllArgsConstructor
public class FinalizeLoadoutButton extends Button {
    private Callback<MatchLoadout> callback;
    private CustomLoadoutData data;

    @Override
    public String getName(Player player) {
        return "§eCreate match";
    }

    @Override
    public List<String> getDescription(Player player) {
        return null;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.HOPPER;
    }

    @Override
    public byte getDamageValue(Player player) {
        return 0;
    }

    @Override
    public void clicked(Player player, int i, ClickType clickType, int hotbar) {
        callback.callback(data.create());
    }
}
