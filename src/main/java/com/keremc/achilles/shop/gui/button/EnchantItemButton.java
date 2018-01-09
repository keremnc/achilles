package com.keremc.achilles.shop.gui.button;

import com.keremc.achilles.shop.ShopEnchantment;
import com.keremc.core.menu.Button;
import com.keremc.core.util.ItemUtils;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class EnchantItemButton extends Button {
    private ShopEnchantment enchant;
    private ShopEnchantment.StackData data;

    @Override
    public String getName(Player player) {
        return ItemUtils.getName(data.item);
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§eEnchant the §d" + getName(player) + " §ein §6slot " + data.slot);

        return lore;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        ItemStack it = super.getButtonItem(player);

        ItemStack existing = player.getInventory().getItem(data.slot);

        if (existing != null) {
            it.addUnsafeEnchantments(existing.getEnchantments());
        }

        return it;
    }

    @Override
    public Material getMaterial(Player player) {
        return data.item.getType();
    }

    @Override
    public byte getDamageValue(Player player) {
        return (byte) data.item.getDurability();
    }

    @Override
    public void clicked(Player player, int i, ClickType clickType, int hotbar) {
        enchant.handleBuy(player, data);
    }
}
