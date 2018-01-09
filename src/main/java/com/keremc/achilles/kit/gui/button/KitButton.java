package com.keremc.achilles.kit.gui.button;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.data.PlayerData;
import com.keremc.achilles.chat.Style;
import com.keremc.achilles.kit.Kit;
import com.keremc.achilles.kit.stats.KitStatistics;
import com.keremc.achilles.kit.stats.MetaProperty;
import com.keremc.core.item.ItemBuilder;
import com.keremc.core.menu.Button;
import com.keremc.core.util.TimeUtils;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
public class KitButton extends Button {
    private Kit kit;

    private PlayerData getData(Player player) {
        return AchillesPlugin.getInstance().getPlayerHandler().getSession(player.getUniqueId());
    }

    @Override
    public String getName(Player player) {
        return (getData(player).ownsKit(kit) ? "§a" : "§c") + kit.getName();
    }

    @Override
    public List<String> getDescription(Player player) {
        PlayerData pd = getData(player);
        KitStatistics ks = pd.getStats().getKitStats(kit);

        List<String> lore = ItemBuilder.wrap(kit.getDescription(), "§e");

        lore.add(0, "");

        if (pd.ownsKit(kit)) {
            lore.add("");

            lore.add("§6Uses§f: " + ks.getUses());
            lore.add("§6Kills§f: " + ks.getKills());
            lore.add("§6Deaths§f: " + ks.getDeaths());

            if (kit.getMetaProperties().length > 0) {
                lore.add("");

                for (MetaProperty mp : kit.getMetaProperties()) {
                    lore.add("§6" + mp.getKey() + "§f: " + Style.DOUBLE_FORMAT.format(mp.get(pd)));
                }
            }

            if (pd.getKitRentals().containsKey(kit.getName())) {
                lore.add("");
                lore.add("§cRental expires in §e§n" + TimeUtils.formatIntoDetailedString(TimeUtils.getSecondsBetween(pd.getKitExpires(kit), new Date())));
            }
        } else {
            lore.add("");
            lore.add("§7§m" + StringUtils.repeat("-", 30));

            lore.add("§cThis kit is currently §4§lLOCKED§c!");
            lore.add("");
            lore.add("§e§lRIGHT-CLICK §6to rent kit for §a§n24 hours§6!");
            lore.add("");

            boolean afford = pd.getBalance() >= kit.getRentalPrice();

            lore.add("§ePrice§f: " + (afford ? "§a" : "§c") + kit.getRentalPrice() + " tokens");
            lore.add("§7§m" + StringUtils.repeat("-", 30));

        }

        return lore;
    }

    @Override
    public Material getMaterial(Player player) {
        return kit.getIcon();
    }

    @Override
    public byte getDamageValue(Player player) {
        return kit.getIconData();
    }

    @Override
    public void clicked(Player player, int i, ClickType clickType, int hotbar) {
        PlayerData pd = getData(player);

        if (clickType.isRightClick() && !pd.ownsKit(kit)) {
            if (pd.getBalance() < kit.getRentalPrice()) {
                player.sendMessage(Style.header(ChatColor.RED + "You have insufficient tokens!"));
                playFail(player);
                return;
            }

            pd.rentKit(kit);
            player.sendMessage(Style.header(ChatColor.YELLOW + "You have rented §b" + kit.getName() + " §efor §a24 hours§e!"));
            player.sendMessage(Style.header(ChatColor.YELLOW + "Rental expires: §6" + Style.DATE_FORMAT.format(pd.getKitExpires(kit))));

            playSuccess(player);
            return;
        } else {
            player.chat("/kit " + kit.getName());
            player.closeInventory();
            playNeutral(player);
        }
    }
}
