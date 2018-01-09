package com.keremc.achilles.listener;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.data.PlayerData;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ProtectListener implements Listener {

    @EventHandler
    public void onArrowStrike(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow) {
            event.getEntity().remove();
        }
    }

    @EventHandler
    public void preventMobSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SPAWNER_EGG && event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) {
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        PlayerData pd = AchillesPlugin.getInstance().getPlayerHandler().getSession(event.getPlayer().getUniqueId());

        if (event.getClickedBlock() != null && !pd.isBuild()) {

            if (event.getItem() != null && (event.getItem().getType() == Material.CHEST || event.getItem().getType() == Material.TRAPPED_CHEST)) {
                event.setUseInteractedBlock(Event.Result.DENY);
                event.setUseItemInHand(Event.Result.DENY);
                event.setCancelled(true);
                event.getPlayer().updateInventory();
            }

            return;
        }
    }

    @EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent e) {
        if (e.getRegainReason() != EntityRegainHealthEvent.RegainReason.MAGIC
                && e.getRegainReason() != EntityRegainHealthEvent.RegainReason.MAGIC_REGEN
                && e.getRegainReason() != EntityRegainHealthEvent.RegainReason.REGEN)
            e.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(final BlockPlaceEvent event) {
        PlayerData pd = AchillesPlugin.getInstance().getPlayerHandler().getSession(event.getPlayer().getUniqueId());

        if (!pd.isBuild()) {

            event.setBuild(false);
            event.setCancelled(true);

            return;
        }


    }

    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player)) {
            return;
        }

        PlayerData pd = AchillesPlugin.getInstance().getPlayerHandler().getSession(event.getRemover().getUniqueId());

        if (!pd.isBuild()) {

            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().getType() != EntityType.ITEM_FRAME) {
            return;
        }
        PlayerData pd = AchillesPlugin.getInstance().getPlayerHandler().getSession(event.getPlayer().getUniqueId());


        if (!pd.isBuild()) {

            event.setCancelled(true);
            return;
        }
    }


    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || event.getEntity().getType() != EntityType.ITEM_FRAME) {
            return;
        }


        event.setCancelled(true);
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        PlayerData pd = AchillesPlugin.getInstance().getPlayerHandler().getSession(event.getPlayer().getUniqueId());

        if (!pd.isBuild()) {

            event.setCancelled(true);
            return;
        }

    }


    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().clear();
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        event.setCancelled(true);
    }


}
