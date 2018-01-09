package com.keremc.achilles.kit.item;

import com.keremc.core.util.ItemUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
@Data
public class Armor {
    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;

    public String info() {

        List<String> items = new ArrayList<>();

        Material type = null;

        boolean same = true;

        for (ItemStack it : new ItemStack[]{helmet, chestplate, leggings, boots}) {
            if (it != null) {

                if (type == null) {
                    type = it.getType();
                }

                if (type.name().contains("_") && it.getType().name().contains("_")) {
                    if (!type.name().split("_")[0].equalsIgnoreCase(it.getType().name().split("_")[0])) {
                        same = false;

                    }
                } else {
                    same = false;
                }

                items.add(Items.friendly(it));


            }
        }

        if (same) {
            String mat = type.name().split("_")[0];

            String color = Stream.of(ItemUtils.ArmorType.values()).filter(at -> at.name().equalsIgnoreCase(mat)).map(at -> Items.color(at)).findAny().orElse("");
            return color + StringUtils.capitalize(mat.toLowerCase()) + " Set";
        }

        return items.stream().collect(Collectors.joining("§6,§f "));
    }

    public void apply(Player player) {
        PlayerInventory pi = player.getInventory();

        pi.setHelmet(helmet);
        pi.setChestplate(chestplate);
        pi.setLeggings(leggings);
        pi.setBoots(boots);
    }

    public void mod(int ordinal, ItemStack it) {
        switch (ordinal) {
            case 0:
                setHelmet(it);
                break;
            case 1:
                setChestplate(it);
                break;
            case 2:
                setLeggings(it);
                break;
            case 3:
                setBoots(it);
                break;
            default:
        }

    }

    public static Armor of(Material... materials) {
        Armor ad = new Armor(null, null, null, null);
        for (int i = 0; i < materials.length; i++) {
            ad.mod(i, new ItemStack(materials[i]));
        }

        return ad;
    }
}
