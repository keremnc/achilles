package com.keremc.achilles.shop.gui.button;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.data.PlayerData;
import com.keremc.achilles.shop.ShopItem;
import com.keremc.core.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class ShopItemButton extends Button {
    private ShopItem shopItem;

    @Override
    public String getName(Player player) {
        return null;
    }

    @Override
    public int getAmount(Player player) {
        return shopItem.getAmount();
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> lore = new ArrayList<>();
        PlayerData pd = AchillesPlugin.getInstance().getPlayerHandler().getSession(player.getUniqueId());

        boolean afford = pd.getBalance() >= shopItem.getPrice();

        lore.add("");
        lore.add("§6Price§f: " + (afford ? "§a" : "§c") + shopItem.getPrice() + " tokens");
        return lore;
    }

    @Override
    public Material getMaterial(Player player) {
        return shopItem.getItemData().getMaterial();
    }

    @Override
    public byte getDamageValue(Player player) {
        return (byte) shopItem.getItemData().getData();
    }

    @Override
    public void clicked(Player player, int i, ClickType clickType, int hotbar) {
        shopItem.handleBuy(player);
    }
}
