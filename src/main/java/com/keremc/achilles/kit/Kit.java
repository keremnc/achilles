package com.keremc.achilles.kit;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.data.PlayerData;
import com.keremc.achilles.chat.Style;
import com.keremc.achilles.kit.item.Armor;
import com.keremc.achilles.kit.item.Items;
import com.keremc.achilles.kit.stats.MetaProperty;
import com.keremc.core.util.PlayerUtils;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public abstract class Kit implements Listener, Comparable {

    public final boolean hasEquipped(Player player) {
        PlayerData pd = AchillesPlugin.getInstance().getPlayerHandler().getSession(player.getUniqueId());

        return pd.getSelectedKit() == this && !pd.isDuelMode() && !pd.isDuelMode();
    }

    public final void applyKit(Player player) {
        PlayerUtils.resetInventory(player, GameMode.SURVIVAL);

        player.sendMessage(Style.header(ChatColor.YELLOW + "You have equipped " + ChatColor.AQUA + getName() + ChatColor.YELLOW + "!"));

        player.spigot().playEffect(player.getLocation(), Effect.FIREWORKS_SPARK, 2, 2, .5F, .5F, .5F, 2, 100, 2);
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 10));

        Armor armor = getArmor();
        Items items = getItems();

        if (armor != null) {
            armor.apply(player);
        }

        if (items != null) {
            player.getInventory().addItem(items.getItems());
        }

        if (getPotionEffects() != null) {
            for (PotionEffect pe : getPotionEffects()) {
                player.addPotionEffect(pe, true);
            }
        }

        while (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP));
        }

        player.getInventory().setHeldItemSlot(0);

        PlayerData pd = AchillesPlugin.getInstance().getPlayerHandler().getSession(player.getUniqueId());

        pd.setCustomInv(false);
        pd.setDuelMode(false);
        pd.setLastKit(this);
        pd.setSelectedKit(this);

        pd.getStats().getKitStats(this).setUses(pd.getStats().getKitStats(this).getUses() + 1);

        pd.markForSave();
    }

    @Override
    public final int compareTo(Object kit) {
        if (kit instanceof Kit) {
            return Integer.valueOf(getId()).compareTo(((Kit) kit).getId());
        }
        return -1;
    }

    public boolean isFree() {
        return false;
    }

    public String getName() {
        return getClass().getSimpleName();
    }

    public abstract Material getIcon();

    public abstract String getDescription();

    public abstract int getId();

    public byte getIconData() {
        return (byte) 0;
    }

    public int getRentalPrice() {
        return 5000;
    }

    public PotionEffect[] getPotionEffects() {
        return new PotionEffect[]{};
    }

    public abstract Armor getArmor();

    public abstract Items getItems();

    public MetaProperty[] getMetaProperties() {
        return new MetaProperty[]{};
    }

}
