package com.keremc.achilles.loadout.builder.functions;

import com.google.common.base.Function;
import com.keremc.achilles.loadout.builder.CustomPart;
import com.keremc.achilles.loadout.builder.ItemPart;
import com.keremc.achilles.loadout.builder.values.CustomEnchantmentValue;
import com.keremc.achilles.loadout.builder.values.CustomItemValue;
import com.keremc.achilles.loadout.builder.values.CustomValue;
import org.bukkit.enchantments.Enchantment;

import java.util.HashMap;
import java.util.Map;

public class ItemPartFunction implements Function<CustomPart<ItemPart>, ItemPart> {

    @Override
    public ItemPart apply(CustomPart customPart) {

        Map<Enchantment, Integer> enchts = new HashMap<>();

        CustomValue priamry = customPart.getCurrentPrimary();

        if (priamry instanceof CustomItemValue) {

            if (customPart.hasSecondary()) {
                CustomValue secondary = customPart.getCurrentSecondary();

                if (secondary.getIntData() > 0 && secondary instanceof CustomEnchantmentValue) {
                    enchts.put(((CustomEnchantmentValue) secondary).getData(), secondary.getIntData());
                }
            }

            return new ItemPart(((CustomItemValue) priamry).getData(), priamry.getShortData(), enchts);

        } else {
            return null;
        }

    }
}
