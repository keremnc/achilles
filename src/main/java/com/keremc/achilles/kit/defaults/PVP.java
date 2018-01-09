package com.keremc.achilles.kit.defaults;

import com.keremc.achilles.kit.Kit;
import com.keremc.achilles.kit.item.Armor;
import com.keremc.achilles.kit.item.Items;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PVP extends Kit {

    @Override
    public Material getIcon() {
        return Material.DIAMOND_SWORD;
    }

    @Override
    public String getDescription() {
        return "The standard PVP class. Equip and fight!";
    }

    @Override
    public Armor getArmor() {
        return Armor.of(Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS);
    }

    @Override
    public PotionEffect[] getPotionEffects() {
        return new PotionEffect[]{new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0)};
    }

    @Override
    public Items getItems() {
        return new Items(new ItemStack(Material.DIAMOND_SWORD));
    }

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public boolean isFree() {
        return true;
    }
}
