package com.keremc.achilles.kit.defaults;

import com.keremc.achilles.kit.Kit;
import com.keremc.achilles.kit.item.Armor;
import com.keremc.achilles.kit.item.Items;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class AnotherRentalTest extends Kit {

    @Override
    public Material getIcon() {
        return Material.CHEST;
    }

    @Override
    public String getDescription() {
        return "Rent me as well";
    }

    @Override
    public Armor getArmor() {
        return Armor.of(Material.BEACON, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.LEATHER_BOOTS);
    }

    @Override
    public Items getItems() {
        return new Items(new ItemStack(Material.IRON_BLOCK));
    }

    @Override
    public int getId() {
        return 4;
    }

}
