package com.keremc.achilles.kit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;

@AllArgsConstructor
public enum Rank {
    DIAMOND(Color.AQUA, "Diamond", ChatColor.AQUA, Material.DIAMOND),
    PLATINUM(Color.GRAY, "Platinum", ChatColor.GRAY, Material.IRON_INGOT),
    GOLD(Color.YELLOW, "Gold", ChatColor.GOLD, Material.GOLD_INGOT);

    @Getter private Color color;
    @Getter private String name;
    @Getter private ChatColor chatColor;
    @Getter private Material material;

}
