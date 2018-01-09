package com.keremc.achilles.loadout.builder;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@AllArgsConstructor
@Data
public class ItemPart {
    private Material type;
    private short data;

    private Map<Enchantment, Integer> enchants;

    public ItemStack toStack() {
        ItemStack it = new ItemStack(type, 1, data);
        it.addUnsafeEnchantments(enchants);

        return it;
    }
}
