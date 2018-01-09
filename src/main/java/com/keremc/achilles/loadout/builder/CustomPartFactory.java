package com.keremc.achilles.loadout.builder;

import com.keremc.achilles.loadout.builder.functions.AmountPartFunction;
import com.keremc.achilles.loadout.builder.functions.EnabledPartFunction;
import com.keremc.achilles.loadout.builder.functions.ItemPartFunction;
import com.keremc.core.util.ItemUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory class. Stores static final instances of CustomPart objects and returns clones of them via static getters
 */
public class CustomPartFactory {

    private static final CustomPart<ItemPart> SWORD = CustomPart.<ItemPart>withEnchantments(
            "Sword",
            "Switch sword Types",
            ItemUtils.swords(),
            Enchantment.DAMAGE_ALL,
            0, 5
    ).setCallableFunction(new ItemPartFunction());

    private static final CustomPart<Integer> MATCH_AMOUNT = CustomPart.<Integer>withItems(
            "First to",
            "Select match amount",
            ItemUtils.repeat(Material.ITEM_FRAME, 3)
    ).setAmounts(new int[]{1, 3, 5}).setNames(new String[]{"Single Match", "First to 3", "First to 5"})
            .setCallableFunction(new AmountPartFunction());

    private static final CustomPart<Boolean> SOUP_REFILLING = CustomPart.<Boolean>withToggle(
            "Soup",
            "Refills",
            Material.MUSHROOM_SOUP, Material.BOWL
            , true).setAmounts(new int[]{35, 8}).setCallableFunction(new EnabledPartFunction());


    private static final CustomPart<Boolean> STRENGTH_II = CustomPart.<Boolean>withToggle(
            "Strength",
            "Strength II Enabled",
            Material.POTION, Material.GLASS_BOTTLE
            , true).setItemDamageValues(new short[]{8233, 0}).setCallableFunction(new EnabledPartFunction());


    private static final CustomPart<Boolean> SPEED_II = CustomPart.<Boolean>withToggle(
            "Speed",
            "Speed II Enabled",
            Material.POTION, Material.GLASS_BOTTLE
            , true).setItemDamageValues(new short[]{8226, 0}).setCallableFunction(new EnabledPartFunction());


    private static final Map<ItemUtils.ArmorPart, CustomPart> ARMOR_BACKING_PARTS = new HashMap<>();

    static {
        for (ItemUtils.ArmorPart ap : ItemUtils.ArmorPart.values()) {
            String friendly = StringUtils.capitalize(ap.name().toLowerCase());

            CustomPart<ItemPart> add = CustomPart.<ItemPart>withEnchantments(
                    friendly,
                    "Switch " + friendly + " types",
                    ItemUtils.armorOf(ap),
                    Enchantment.PROTECTION_ENVIRONMENTAL,
                    0, 5
            ).setCallableFunction(new ItemPartFunction());

            ARMOR_BACKING_PARTS.put(ap, add);
        }
    }


    public static CustomPart<ItemPart> createArmor(ItemUtils.ArmorPart ap) {
        return ARMOR_BACKING_PARTS.get(ap).clone();
    }

    public static CustomPart<ItemPart> createSword() {
        return SWORD.clone();
    }

    public static CustomPart<Integer> createMatchAmount() {
        return MATCH_AMOUNT.clone();
    }

    public static CustomPart<Boolean> createSpeedII() {
        return SPEED_II.clone();
    }

    public static CustomPart<Boolean> createStrengthII() {
        return STRENGTH_II.clone();
    }

    public static CustomPart<Boolean> createSoupRefilling() {
        return SOUP_REFILLING.clone();
    }
}
