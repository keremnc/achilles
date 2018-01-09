package com.keremc.achilles.listener;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.data.PlayerData;
import com.keremc.achilles.loadout.gui.menu.LoadoutSelectionMenu;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class MatchChallengeListener implements Listener {

    @EventHandler
    public void onEntityInteractEntityEvent(PlayerInteractEntityEvent event) {
        Player clicker = event.getPlayer();
        Entity rightClicked = event.getRightClicked();


        if (rightClicked instanceof Player) {
            ItemStack item = clicker.getItemInHand();

            if (item.getType() != null && item.hasItemMeta()) {
                if (item.getType() == Material.BLAZE_ROD) {

                    PlayerData pd = AchillesPlugin.getInstance().getPlayerHandler().getSession(clicker.getUniqueId());

                    if (pd.isInMatch()) {
                        return;
                    }

                    if (pd.isDuelMode()) {
                        AchillesPlugin.getInstance().getMatchHandler().quickMatch(clicker, (Player) rightClicked, null);
                    } else {
                        System.out.println("\n\n" + clicker.getName() + " is NOT in duel mode!\n\n");
                    }
                } else if (item.getType() == Material.BONE) {


                    PlayerData pd = AchillesPlugin.getInstance().getPlayerHandler().getSession(clicker.getUniqueId());

                    LoadoutSelectionMenu lsm = new LoadoutSelectionMenu(
                            (ml) -> {
                                if (pd.isInMatch()) {
                                    return;
                                }

                                if (pd.isDuelMode()) {
                                    AchillesPlugin.getInstance().getMatchHandler().quickMatch(clicker, (Player) rightClicked, ml);
                                } else {
                                    System.out.println("\n\n" + clicker.getName() + " is NOT in duel mode!\n\n");
                                }

                                clicker.closeInventory();
                            }, false
                    );

                    lsm.openMenu(clicker);

                }

            }
        }

    }
}
