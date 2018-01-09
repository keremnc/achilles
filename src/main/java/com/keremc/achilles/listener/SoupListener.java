package com.keremc.achilles.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SoupListener implements Listener {

    @EventHandler
    public void onFoodLoss(FoodLevelChangeEvent event) {
        event.setCancelled(true);
        event.setFoodLevel(20);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {

            double h = event.getPlayer().getHealth();

            if (event.getPlayer().getItemInHand().getTypeId() == 282 && h < 20D) {

                event.setCancelled(true);
                Player player = event.getPlayer();
                player.setHealth((player.getHealth() + 7) > 20D ? 20 : player.getHealth() + 7);
                player.getItemInHand().setType(Material.BOWL);

            } else if (event.getPlayer().getItemInHand().getTypeId() == 282 && event.getPlayer().getFoodLevel() < 20) {

                event.setCancelled(true);
                Player player = event.getPlayer();
                player.setFoodLevel((player.getFoodLevel() + 7) > 20D ? 20 : player.getFoodLevel() + 7);
                player.getItemInHand().setType(Material.BOWL);
            }
        }
    }
}
