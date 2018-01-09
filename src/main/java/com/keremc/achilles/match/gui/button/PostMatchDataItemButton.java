package com.keremc.achilles.match.gui.button;

import com.keremc.core.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PostMatchDataItemButton extends Button {

    private ItemStack item;

    public PostMatchDataItemButton(ItemStack item) {
        super();

        this.item = item;
    }

    // We just override this whole method, as we need to keep enchants/potion data/etc
    @Override
    public ItemStack getButtonItem(Player player) {
        return (item);
    }

    // Not needed.
    public String getName(Player player) { return (null); }
    public List<String> getDescription(Player player) { return (null); }
    public Material getMaterial(Player player) { return (null); }
    public byte getDamageValue(Player player) { return (0); }

    public void clicked(final Player player, final int slot, ClickType clickType, int h) {}

}