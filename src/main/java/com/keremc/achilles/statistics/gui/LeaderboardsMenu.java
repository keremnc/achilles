package com.keremc.achilles.statistics.gui;

import com.keremc.achilles.statistics.StatSlot;
import com.keremc.core.menu.Button;
import com.keremc.core.menu.Menu;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class LeaderboardsMenu extends Menu {
    @Override
    public String getTitle(Player player) {
        return "Leaderboards";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(1, new StatisticButton(StatSlot.KILLS));
        buttons.put(2, new StatisticButton(StatSlot.DEATHS));

        buttons.put(4, new StatisticButton(StatSlot.KDR));

        buttons.put(6, new StatisticButton(StatSlot.CURRENT_STREAK));
        buttons.put(7, new StatisticButton(StatSlot.MAX_STREAK));

        buttons.put(10, new StatisticButton(StatSlot.DUEL_WINS));
        buttons.put(11, new StatisticButton(StatSlot.DUEL_LOSSES));

        buttons.put(13, new StatisticButton(StatSlot.WLR));

        buttons.put(15, new StatisticButton(StatSlot.WINSTREAK));
        buttons.put(16, new StatisticButton(StatSlot.WINSTREAK_MAX));

        return buttons;
    }
}
