package com.keremc.achilles.match.gui;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.match.PlayerMatchInvite;
import com.keremc.achilles.match.gui.button.PendingMatchRequestButton;
import com.keremc.core.menu.Button;
import com.keremc.core.menu.Menu;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PendingMatchRequestsMenu extends Menu {
    {
        setAutoUpdate(true);
    }

    @Override
    public String getTitle(Player player) {
        return "Pending Duel Requests";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        int index = 0;
        for (PlayerMatchInvite pmi : AchillesPlugin.getInstance().getMatchHandler().getAllPlayerInvites(player.getUniqueId())) {
            buttons.put(index++, new PendingMatchRequestButton(pmi));
        }

        return buttons;
    }
}
