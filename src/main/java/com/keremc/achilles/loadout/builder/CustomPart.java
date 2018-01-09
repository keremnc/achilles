package com.keremc.achilles.loadout.builder;

import com.google.common.base.Function;
import com.keremc.achilles.loadout.builder.values.CustomBooleanValue;
import com.keremc.achilles.loadout.builder.values.CustomEnchantmentValue;
import com.keremc.achilles.loadout.builder.values.CustomItemValue;
import com.keremc.achilles.loadout.builder.values.CustomValue;
import com.keremc.achilles.shop.ShopEnchantment;
import com.keremc.core.util.ItemUtils;
import lombok.Data;
import net.minecraft.server.v1_8_R3.LocaleI18n;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

@Data
public class CustomPart<T> {
    private String primaryDisplay;
    private CustomValue[] primary;

    private String secondaryDisplay;
    private CustomValue[] secondary;

    private String title;

    private Function<CustomPart<T>, T> callableFunction;

    private int selected = 0;
    private int secondarySelected = 0;

    /**
     * Creates a CustomPart that is toggleable, either enabled or disabled
     *
     * @param title          title of the part
     * @param primaryDisplay display of the toggled field
     * @param first          first type
     * @param second         second type
     * @param defaultOn      true if defaults to enabled, false otherwise.
     *                       <b>The object enabled by default will ALWAYS be first </b>
     * @return created CustomPart object
     */
    public static <T> CustomPart<T> withToggle(String title, String primaryDisplay, Material first, Material second,
                                               boolean defaultOn) {

        CustomPart<T> cp = new CustomPart();

        cp.primaryDisplay = primaryDisplay;
        cp.title = title;

        CustomBooleanValue enabled = new CustomBooleanValue(first, true, "Enabled");
        CustomBooleanValue disabled = new CustomBooleanValue(second, false, "Disabled");

        cp.primary = new CustomValue[2];
        cp.primary[0] = defaultOn ? enabled : disabled;
        cp.primary[1] = defaultOn ? disabled : enabled;

        return cp;

    }

    /**
     * Creates a CustomPart with items and enchantments
     *
     * @param title          title of the part
     * @param primaryDisplay display of the primary field
     * @param materials      items to display
     * @param enchantment    enchantment type
     * @param min            minimum enchantment level, inclusive
     * @param max            maximum enchatnment level, incluseive
     * @return created CustomPart object
     */
    public static <T> CustomPart<T> withEnchantments(String title, String primaryDisplay, ItemUtils.ItemData[] materials,
                                                     Enchantment enchantment, int min, int max) {


        CustomPart cp = withItems(title, primaryDisplay, materials);

        cp.secondary = new CustomValue[max - min + 1];
        cp.secondaryDisplay = "Enchantments";

        for (int i = Math.max(0, min); i <= max; i++) {


            String friendly;

            if (i == 0) {
                friendly = "No " + LocaleI18n.get(net.minecraft.server.v1_8_R3.Enchantment.getById(enchantment.getId()).a());
            } else {
                friendly = new ShopEnchantment(enchantment, i, -1).getFriendlyName(false);
            }

            CustomEnchantmentValue cev = new CustomEnchantmentValue(Material.AIR, enchantment, friendly);
            cev.setIntData(i);

            cp.secondary[i - Math.max(0, min)] = cev;
        }


        return cp;

    }

    /**
     * Creates a CustomPart with items
     *
     * @param title          title of the part
     * @param primaryDisplay display of the primary field
     * @param materials      items to display
     * @return created CustomPart object
     */
    public static <T> CustomPart<T> withItems(String title, String primaryDisplay, ItemUtils.ItemData[] materials) {
        CustomPart<T> cp = new CustomPart<T>();

        cp.title = title;
        cp.primaryDisplay = primaryDisplay;

        cp.primary = new CustomValue[materials.length];

        for (int i = 0; i < materials.length; i++) {
            CustomItemValue item = new CustomItemValue(materials[i].getMaterial(), materials[i].getMaterial(), ItemUtils.getName(materials[i].toItemStack()));
            item.setShortData(materials[i].getData());

            cp.primary[i] = item;
        }

        return cp;
    }

    public CustomPart<T> setCallableFunction(Function<CustomPart<T>, T> callableFunction) {
        this.callableFunction = callableFunction;
        return this;
    }

    /**
     * Sets the damage values for each CustomPart's item object
     *
     * @param damageValues array of item damage values, order needs to be identical to order of primary insertion
     * @return same object
     */
    public CustomPart<T> setItemDamageValues(short[] damageValues) {
        for (int i = 0; i < primary.length; i++) {
            primary[i].setShortData(damageValues[i]);
        }

        return this;
    }

    /**
     * Sets the item names for each CustomPart's item object
     *
     * @param names array of item names, order needs to be identical to order of primary insertion
     * @return same object
     */
    public CustomPart<T> setNames(String[] names) {
        for (int i = 0; i < primary.length; i++) {
            primary[i].setName(names[i]);
        }

        return this;
    }

    /**
     * Sets the item amounts for each CustomPart's item object
     *
     * @param amounts array of item amoutns, order needs to be identical to order of primary insertion
     * @return same object
     */
    public CustomPart<T> setAmounts(int[] amounts) {
        for (int i = 0; i < primary.length; i++) {
            primary[i].setIntData(amounts[i]);
        }

        return this;
    }

    public T getValue() {
        try {
            return callableFunction != null ? callableFunction.apply(this) : null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @return if the selected primary value is an instance of CustomBooleanValue and is currently on
     */
    public boolean isEnabled() {
        return getCurrentPrimary() instanceof CustomBooleanValue && ((CustomBooleanValue) getCurrentPrimary()).getData();
    }

    public CustomValue getCurrentPrimary() {
        return primary[getSelected()];
    }

    public CustomValue getCurrentSecondary() {
        return secondary[getSecondarySelected()];
    }

    public void incrementPrimary() {
        selected++;

        if (selected >= primary.length) {
            selected = 0;
        }


    }

    public void incrementSecondary() {
        if (hasSecondary()) {
            secondarySelected++;

            if (secondarySelected >= secondary.length) {
                secondarySelected = 0;
            }
        } else {
            incrementPrimary();
        }
    }

    public void jumpTo(int selected) {
        this.selected = selected % primary.length;

    }


    public boolean hasSecondary() {
        return secondary != null;
    }

    @Override
    protected CustomPart clone() {
        CustomPart cloned = new CustomPart();

        cloned.title = title;

        cloned.primary = primary;
        cloned.primaryDisplay = primaryDisplay;

        cloned.secondary = secondary;
        cloned.secondaryDisplay = secondaryDisplay;

        cloned.callableFunction = callableFunction;

        return cloned;
    }
}
