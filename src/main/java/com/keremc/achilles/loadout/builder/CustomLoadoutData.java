package com.keremc.achilles.loadout.builder;

import com.keremc.achilles.kit.item.Armor;
import com.keremc.achilles.kit.item.Items;
import com.keremc.achilles.loadout.MatchLoadout;
import com.keremc.core.util.ItemUtils;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class CustomLoadoutData {

    private String name;
    private long timestamp = -1;

    private CustomPart<ItemPart> helmet;
    private CustomPart<ItemPart> chestplate;
    private CustomPart<ItemPart> leggings;
    private CustomPart<ItemPart> boots;

    private CustomPart<ItemPart> sword;
    private CustomPart<Boolean> soupAmount;

    private CustomPart<Integer> matchAmount;

    private CustomPart<Boolean> speedII;
    private CustomPart<Boolean> strengthII;

    public CustomLoadoutData() {
        helmet = CustomPartFactory.createArmor(ItemUtils.ArmorPart.HELMET);
        chestplate = CustomPartFactory.createArmor(ItemUtils.ArmorPart.CHESTPLATE);
        leggings = CustomPartFactory.createArmor(ItemUtils.ArmorPart.LEGGINGS);
        boots = CustomPartFactory.createArmor(ItemUtils.ArmorPart.BOOTS);

        sword = CustomPartFactory.createSword();
        soupAmount = CustomPartFactory.createSoupRefilling();

        matchAmount = CustomPartFactory.createMatchAmount();

        speedII = CustomPartFactory.createSpeedII();
        strengthII = CustomPartFactory.createStrengthII();

    }


    public MatchLoadout create() {
        Map<String, String> metadata = new HashMap<>();

        try {
            for (Field field : CustomLoadoutData.class.getDeclaredFields()) {
                field.setAccessible(true);

                if (field.getType() == CustomPart.class) {
                    CustomPart cp = (CustomPart) field.get(this);
                    metadata.put(field.getName(), cp.getSelected() + ":" + cp.getSecondarySelected());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Material icon = Material.CHEST;
        Armor ad = new Armor(helmet.getValue().toStack(), chestplate.getValue().toStack(), leggings.getValue().toStack(), boots.getValue().toStack());

        Items it = new Items(sword.getValue().toStack());

        List<PotionEffect> pe = new ArrayList<>();

        if (strengthII.getValue()) {
            pe.add(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1));
        }
        if (speedII.getValue()) {
            pe.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        }

        int soups = soupAmount.getValue() ? -1 : 8;
        int firstTo = matchAmount.getValue();


        MatchLoadout ml = new MatchLoadout(
                name == null ? "Unsaved_Custom_" + (System.currentTimeMillis() + "").substring(4, 8) : name,
                icon, ad, it, pe.toArray(new PotionEffect[]{}),
                soups, true, true, timestamp == -1 ? System.currentTimeMillis() : timestamp, 1, firstTo, metadata
        );

        return ml;
    }

    public static CustomLoadoutData from(String name, long timestamp, Map<String, String> metadata) {


        CustomLoadoutData cld = new CustomLoadoutData();

        cld.name = name;
        cld.timestamp = timestamp;

        try {
            for (Field field : CustomLoadoutData.class.getDeclaredFields()) {
                field.setAccessible(true);

                if (field.getType() == CustomPart.class) {
                    CustomPart cp = (CustomPart) field.get(cld);
                    String val = metadata.get(field.getName());


                    if (val != null) {
                        int pr = Integer.parseInt(val.split(":")[0]);
                        int sc = Integer.parseInt(val.split(":")[1]);

                        cp.setSelected(pr);
                        cp.setSecondarySelected(sc);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return cld;
    }
}
