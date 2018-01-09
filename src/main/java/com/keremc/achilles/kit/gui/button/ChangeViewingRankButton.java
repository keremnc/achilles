package com.keremc.achilles.kit.gui.button;

import com.keremc.achilles.kit.Rank;
import com.keremc.achilles.kit.gui.KitSelectMenu;
import com.keremc.core.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

@AllArgsConstructor
public class ChangeViewingRankButton extends Button {
    private KitSelectMenu menu;
    private Rank rank;

    @Override
    public String getName(Player player) {
        return "§eView " + (rank == null ? "§fall" : (rank.getChatColor() + rank.getName())) + " §eKits";
    }

    @Override
    public List<String> getDescription(Player player) {
        return null;
    }

    @Override
    public Material getMaterial(Player player) {
        return rank == null ? Material.MILK_BUCKET : rank.getMaterial();
    }

    @Override
    public byte getDamageValue(Player player) {
        return 0;
    }

    @Override
    public void clicked(Player player, int i, ClickType clickType, int hotbar) {
        menu.setRank(rank);
        player.getOpenInventory().getTopInventory().setContents(menu.createInventory(player).getContents());
    }
}
