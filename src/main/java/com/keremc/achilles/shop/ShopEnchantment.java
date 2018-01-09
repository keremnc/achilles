package com.keremc.achilles.shop;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.data.PlayerData;
import com.keremc.achilles.chat.Style;
import com.keremc.achilles.shop.gui.ShopEnchantsMenu;
import com.keremc.core.util.ItemUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.server.v1_8_R3.LocaleI18n;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
public class ShopEnchantment implements ShopPurchasable {
    private Enchantment enchantment;
    private int level;

    private int price;

    public String getFriendlyName(boolean colored) {
        net.minecraft.server.v1_8_R3.Enchantment nmsE = net.minecraft.server.v1_8_R3.Enchantment.getById(enchantment.getId());
        String bet = (colored ? "§c" : "");

        return bet + LocaleI18n.get(nmsE.a()) + " " + bet + LocaleI18n.get("enchantment.level." + level) + "§f";

    }

    public StackData[] getEnchantables(Player player) {
        List<StackData> items = new ArrayList<>();

        for (int i = 0; i <= 39; i++) {
            ItemStack it = player.getInventory().getItem(i);

            if (it != null && getEnchantment().canEnchantItem(it)) {
                if (it.containsEnchantment(enchantment) && it.getEnchantmentLevel(enchantment) >= level) {
                    continue;
                }

                items.add(new StackData(it, i));
            }
        }

        return items.toArray(new StackData[]{});
    }


    public void handleBuy(Player player, StackData data) {
        ItemStack atSlot = player.getInventory().getItem(data.slot);

        if (atSlot.getType() != data.item.getType()) {
            player.sendMessage(ChatColor.RED + "Found " + atSlot.toString() + " instead of " + data.item.toString() + " at slot " + data.slot);
            return;
        }

        PlayerData pd = AchillesPlugin.getInstance().getPlayerHandler().getSession(player.getUniqueId());

        if (pd.getBalance() < getPrice()) {
            player.sendMessage(Style.header(ChatColor.RED + "You have insufficient tokens!"));
            return;
        }

        atSlot.addUnsafeEnchantment(getEnchantment(), getLevel());
        player.sendMessage(Style.header("§b" + ItemUtils.getName(atSlot) + " §ein §6slot " + data.slot + " §ehas been enchanted!"));
        purchased(pd);

        new ShopEnchantsMenu().openMenu(player);
    }

    @AllArgsConstructor
    public static class StackData {
        public ItemStack item;
        public int slot;
    }
}
