package com.keremc.achilles.loadout.builder.values;

import org.bukkit.Material;

public class CustomItemValue extends CustomValue<Material> {
    public CustomItemValue(Material display, Material data, String name) {
        super(display, data, name);
    }
}
