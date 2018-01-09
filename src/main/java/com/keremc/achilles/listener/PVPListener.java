package com.keremc.achilles.listener;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.data.PlayerData;
import com.keremc.achilles.chat.Style;
import com.keremc.achilles.kit.Kit;
import com.keremc.achilles.match.Match;
import com.keremc.achilles.match.MatchState;
import com.keremc.achilles.region.Tag;
import com.keremc.achilles.statistics.StatSlot;
import com.keremc.core.CorePlugin;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Iterator;
import java.util.List;

public class PVPListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player dead = event.getEntity();
        Player killer = dead.getKiller();

        event.setDeathMessage(null);
        dead.setVelocity(new Vector(0, 0, 0));

        PlayerData pd = AchillesPlugin.getInstance().getPlayerHandler().getSession(dead.getUniqueId());

        Match match = AchillesPlugin.getInstance().getMatchHandler().getMatch(dead);


        int i = 0;

        for (ItemStack it : event.getDrops()) {
            Item item = dead.getWorld().dropItem(dead.getLocation().add(CorePlugin.RANDOM.nextInt(2) - 1, 0, CorePlugin.RANDOM.nextInt(2) - 1), it);
            i++;
            Bukkit.getScheduler().runTaskLater(AchillesPlugin.getInstance(), () -> {
                item.remove();
            }, 5L + (4 * i));
        }
        event.getDrops().clear();

        Bukkit.getScheduler().runTaskLater(AchillesPlugin.getInstance(), () -> {
            dead.spigot().respawn();
        }, 1L);


        if (match != null) {
            match.eliminated(dead);

        } else {

            Bukkit.getScheduler().runTaskLater(AchillesPlugin.getInstance(), () -> {
                if (dead.isOnline()) {
                    pd.respawn();
                }

            }, 2L);

            pd.getStats().inc(StatSlot.DEATHS);

            boolean ksended = false;

            if (killer != null && killer != dead) {
                PlayerData pk = AchillesPlugin.getInstance().getPlayerHandler().getSession(killer.getUniqueId());
                ksended = pk.killed(dead);
            }

            List<String> assisters = pd.applyAssists(killer, pd.getWorth());

            if (!ksended) {
                TextComponent tc = Style.deathMessage(killer, dead);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player != killer && !assisters.contains(player.getName())) {
                        player.spigot().sendMessage(tc);
                    }
                }
            }

            pd.getStats().set(StatSlot.CURRENT_STREAK, 0);

            Kit selectedKit = pd.getSelectedKit();
            if (selectedKit != null) {
                pd.getStats().getKitStats(selectedKit).setDeaths(pd.getStats().getKitStats(selectedKit).getDeaths() + 1);
                pd.setSelectedKit(null);
            }


        }
        pd.markForSave();


    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        Material type = event.getItemDrop().getItemStack().getType();

        if (type == Material.MUSHROOM_SOUP || type == Material.BOWL) {
            event.getItemDrop().remove();
        } else {
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamagePercentage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player damaged = (Player) e.getEntity();
            PlayerData pd = AchillesPlugin.getInstance().getPlayerHandler().getSession(damaged.getUniqueId());

            if (pd.isInMatch() && AchillesPlugin.getInstance().getMatchHandler().getMatch(damaged).getState() != MatchState.FIGHTING) {
                e.setCancelled(true);
                return;
            }

            if (Tag.DUEL_LOBBY.isTagged(damaged.getLocation())) {
                e.setCancelled(true);

                return;
            }


            Player damager = null;

            if (e instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
                if (event.getDamager() instanceof Player) {
                    damager = (Player) event.getDamager();

                } else if (event.getDamager() instanceof Projectile) {
                    if (!(((Projectile) event.getDamager()).getShooter() instanceof Player)) {
                        return;
                    }
                    damager = ((Player) ((Projectile) event.getDamager()).getShooter());
                }

                if (damager == damaged) {
                    return;
                }

            }
            pd.damagedBy(damager, e.getDamage());
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

        if (event.getEntity() instanceof Player) {

            if (event.isCancelled()) {
                return;
            }

            Player player = (Player) event.getEntity();
            PlayerData pd = AchillesPlugin.getInstance().getPlayerHandler().getSession(player.getUniqueId());

            if (pd.isWarping()) {
                pd.cancelWarp();
            }

            Player damager;

            if (event.getDamager() instanceof Player) {
                damager = (Player) event.getDamager();


            } else if (event.getDamager() instanceof Projectile) {
                if (!(((Projectile) event.getDamager()).getShooter() instanceof Player)) {
                    return;
                }
                damager = ((Player) ((Projectile) event.getDamager()).getShooter());

                if (damager == player) {
                    Bukkit.getScheduler().runTaskLater(AchillesPlugin.getInstance(), () -> {
                        player.setVelocity(new Vector(0, 0, 0));
                    }, 1L);
                }
            } else {
                return;
            }
            if (damager == player) {
                return;
            }

            PlayerData damagerData = AchillesPlugin.getInstance().getPlayerHandler().getSession(damager.getUniqueId());

            if (pd.isSpawnProt()) {

                damager.sendMessage(ChatColor.RED + "That player has spawn protection!");
                event.setCancelled(true);
                return;
            } else if (damagerData.isSpawnProt()) {
                damagerData.setSpawnProt(false);
                damager.sendMessage(ChatColor.GRAY + "You no longer have spawn protection.");

            }

        }

    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {

        Player shooter = (Player) event.getPotion().getShooter();

        Iterator<LivingEntity> iter = event.getAffectedEntities().iterator();

        while (iter.hasNext()) {
            LivingEntity ent = iter.next();

            if (ent instanceof Player) {
                Player player = (Player) ent;
                PlayerData pd = AchillesPlugin.getInstance().getPlayerHandler().getSession(player.getUniqueId());
                PlayerData shooterData = AchillesPlugin.getInstance().getPlayerHandler().getSession(shooter.getUniqueId());

                if (!pd.isSpawnProt()) {

                    if (shooterData.isSpawnProt()) {
                        shooterData.setSpawnProt(false);
                        shooter.sendMessage(ChatColor.GRAY + "You no longer have spawn protection.");

                    }
                }
            }

        }

    }

    @EventHandler
    public void onDamage(EntityDamageEvent entity) {
        if (entity.getEntity() instanceof Player) {
            Player player = (Player) entity.getEntity();
            PlayerData pd = AchillesPlugin.getInstance().getPlayerHandler().getSession(player.getUniqueId());

            if (pd.isSpawnProt()) {
                entity.setCancelled(true);
                return;
            }
        }
    }
}
