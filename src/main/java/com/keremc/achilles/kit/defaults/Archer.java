package com.keremc.achilles.kit.defaults;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.data.PlayerData;
import com.keremc.achilles.kit.Kit;
import com.keremc.achilles.kit.item.Armor;
import com.keremc.achilles.kit.item.Items;
import com.keremc.achilles.kit.stats.MetaProperty;
import com.keremc.core.item.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class Archer extends Kit {
    private MetaProperty arrows = new MetaProperty(this, "Arrows Shot", 0);
    private MetaProperty headshots = new MetaProperty(this, "Headshots", 0);
    private MetaProperty longshots = new MetaProperty(this, "Longshots", 0);

    @Override
    public MetaProperty[] getMetaProperties() {
        return new MetaProperty[]{arrows, headshots, longshots};
    }

    @Override
    public Material getIcon() {
        return Material.ARROW;
    }

    @Override
    public String getDescription() {
        return "A worthy fighter, able to take out enemies with extreme agility and marksmanship.";
    }

    @Override
    public Armor getArmor() {
        return Armor.of(Material.GOLD_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS);
    }

    @Override
    public Items getItems() {
        return new Items(
                new ItemBuilder().withMaterial(Material.BOW)
                        .enchant(Enchantment.ARROW_DAMAGE, 3)
                        .enchant(Enchantment.ARROW_INFINITE, 1)
                        .enchant(Enchantment.ARROW_KNOCKBACK, 1)
                        .create(),
                new ItemBuilder().withMaterial(Material.WOOD_SWORD).enchant(Enchantment.DAMAGE_ALL, 3).create(),
                new ItemStack(Material.ARROW)
        );
    }

    @Override
    public int getId() {
        return 2;
    }


    @Override
    public boolean isFree() {
        return true;
    }

    @EventHandler
    public void onArrowLaunched(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Arrow && event.getEntity().getShooter() instanceof Player) {
            Arrow arrow = (Arrow) event.getEntity();
            Player shooter = (Player) event.getEntity().getShooter();
            PlayerData data = AchillesPlugin.getInstance().getPlayerHandler().getSession(shooter.getUniqueId());

            if (hasEquipped(shooter)) {
                arrows.mod(data, i -> i + 1);
            }

            arrow.setMetadata("launcher_location", new FixedMetadataValue(AchillesPlugin.getInstance(), shooter.getLocation()));

        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onArrowHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Arrow) {
            Player target = (Player) event.getEntity();
            Arrow arrow = (Arrow) event.getDamager();

            if (arrow.getShooter() instanceof Player) {
                Player shooter = (Player) arrow.getShooter();
                PlayerData data = AchillesPlugin.getInstance().getPlayerHandler().getSession(shooter.getUniqueId());

                if (hasEquipped(shooter) && arrow.hasMetadata("launcher_location")) {
                    Location launched = (Location) arrow.getMetadata("launcher_location").get(0).value();
                    Location hit = arrow.getLocation();

                    if (hit.getY() > target.getEyeLocation().getY()) {
                        headshots.mod(data, i -> i + 1);
                        shooter.playSound(shooter.getLocation(), Sound.ZOMBIE_WOODBREAK, 5F, 20F);
                    }

                    if (launched.distanceSquared(hit) > 2500) {
                        longshots.mod(data, i -> i + 1);
                        shooter.playSound(shooter.getLocation(), Sound.ZOMBIE_UNFECT, 5F, 20F);

                    }

                }
            }

        }
    }
}
