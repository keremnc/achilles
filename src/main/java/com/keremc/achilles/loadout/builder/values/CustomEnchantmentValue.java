package com.keremc.achilles.loadout.builder.values;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

public class CustomEnchantmentValue extends CustomValue<Enchantment> {
    public CustomEnchantmentValue(Material display, Enchantment data, String name) {
        super(display, data, name);
    }
}
