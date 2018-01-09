package com.keremc.achilles.statistics.gui;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.chat.Style;
import com.keremc.achilles.data.PlayerData;
import com.keremc.achilles.statistics.Leaderboards;
import com.keremc.achilles.statistics.StatSlot;
import com.keremc.core.menu.Button;
import com.keremc.core.util.UUIDUtils;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
public class StatisticButton extends Button {
    private StatSlot statSlot;

    @Override
    public String getName(Player player) {
        return "§a" + statSlot.getFriendlyName();
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> desc = new ArrayList<>();

        desc.add("");
        Map.Entry<UUID, Double>[] top = Leaderboards.retrieve(statSlot);

        for (int i = 0; i < Math.min(10, top.length); i++) {
            int rank = i + 1;

            String base = (rank == 1 ? "§6§l1§7. §a⚝§d" : "§7" + rank + ".§c")
                    + " " + UUIDUtils.name(top[i].getKey()) + " §7-§e " +
                    Style.DOUBLE_FORMAT.format(top[i].getValue()) + " " + statSlot.getFriendlyName().toLowerCase();

            desc.add(base);


        }

        PlayerData pd = AchillesPlugin.getInstance().getPlayerHandler().getSession(player.getUniqueId());

        desc.add("");
        desc.add("§dYour position§f: " + Leaderboards.position(statSlot, player.getUniqueId()) + "§7/§f" + top.length);

        return desc;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.BOOK;
    }

    @Override
    public byte getDamageValue(Player player) {
        return 0;
    }

    @Override
    public void clicked(Player player, int i, ClickType clickType, int i1) {

    }
}
