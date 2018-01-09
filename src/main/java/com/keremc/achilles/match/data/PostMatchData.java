package com.keremc.achilles.match.data;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PostMatchData {

    private WrappedItemStack[] armorContents;
    private WrappedItemStack[] inventoryContents;
    @Getter @Setter private int health;

    public static PostMatchData fromPlayer(Player player) {
        PostMatchData postMatchData = new PostMatchData();

        postMatchData.setArmorContents(player.getInventory().getArmorContents());
        postMatchData.setInventoryContents(player.getInventory().getContents());
        postMatchData.setHealth((int) Math.ceil(player.getHealth()));

        return (postMatchData);
    }

    public ItemStack[] getArmorContents() {
        return (WrappedItemStack.unbox(armorContents));
    }

    public void setArmorContents(ItemStack[] armorContents) {
        this.armorContents = WrappedItemStack.box(armorContents);
    }

    public ItemStack[] getInventoryContents() {
        return (WrappedItemStack.unbox(inventoryContents));
    }

    public void setInventoryContents(ItemStack[] inventoryContents) {
        this.inventoryContents = WrappedItemStack.box(inventoryContents);
    }

}