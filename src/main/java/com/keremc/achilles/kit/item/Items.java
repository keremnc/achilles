package com.keremc.achilles.kit.item;

import com.keremc.achilles.shop.ShopEnchantment;
import com.keremc.core.util.ItemUtils;
import lombok.Getter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Items {
    @Getter private ItemStack[] items;

    public Items(ItemStack... items) {
        this.items = items;
    }

    public String info() {

        List<String> items = new ArrayList<>();

        for (ItemStack it : this.items) {

            if (it != null) {

                items.add(friendly(it));


            }
        }

        return items.stream().collect(Collectors.joining("§6,§f "));
    }

    public static String friendly(ItemStack item) {
        Map<Enchantment, Integer> enchantments = item.getEnchantments();
        boolean sword = Stream.of(ItemUtils.swords()).anyMatch(id -> id.getMaterial() == item.getType());
        boolean armor = Stream.of(ItemUtils.ArmorPart.values()).anyMatch(ap -> Stream.of(ItemUtils.armorOf(ap)).anyMatch(id -> id.getMaterial() == item.getType()));

        String itemName = ItemUtils.getName(item);

        if (sword || armor) {
            for (String part : itemName.split(" ", -1)) {

                ItemUtils.SwordType st = null;
                ItemUtils.ArmorType at = null;

                boolean matches = false;
                try {
                    if (sword) {
                        st = ItemUtils.SwordType.valueOf(part.toUpperCase());
                        matches = true;
                    } else if (armor) {

                        at = ItemUtils.ArmorType.valueOf(part.toUpperCase());
                        matches = true;
                    }
                } catch (IllegalArgumentException ex) {
                }

                if (matches) {

                    int total = itemName.length();
                    String color = (sword ? color(st) : color(at));

                    itemName = color + itemName;

                    for (int i = 0; i < total; i++) {

                        if (itemName.charAt(i) == ' ') {

                            itemName = itemName.substring(0, i + 1) + color + itemName.substring(i + 1);
                            total += 2;
                            i += 1;

                        }
                    }

                    break;
                }
            }
        }

        return (enchantments.entrySet().stream().filter(e -> e.getValue() > 0).map(e ->
                new ShopEnchantment(e.getKey(), e.getValue(), -1).getFriendlyName(false)
        )).collect(Collectors.joining("§f ")) + " " + itemName + "§f";
    }

    public static String color(ItemUtils.SwordType at) {
        switch (at) {
            case DIAMOND:
                return "§b";
            case GOLD:
                return "§6";
            case IRON:
                return "§7";
            case STONE:
                return "§8";
            default:
                return "";
        }
    }

    public static String color(ItemUtils.ArmorType at) {
        switch (at) {
            case DIAMOND:
                return "§b";
            case GOLD:
                return "§6";
            case IRON:
                return "§7";
            case LEATHER:
                return "§6";
            default:
                return "";
        }
    }
}
