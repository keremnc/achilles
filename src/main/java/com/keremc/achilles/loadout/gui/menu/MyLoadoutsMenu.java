package com.keremc.achilles.loadout.gui.menu;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.data.PlayerData;
import com.keremc.achilles.loadout.MatchLoadout;
import com.keremc.achilles.loadout.gui.button.CreateCustomLoadoutButton;
import com.keremc.achilles.loadout.gui.button.LoadoutButton;
import com.keremc.core.menu.Button;
import com.keremc.core.menu.Menu;
import com.keremc.core.menu.buttons.BackButton;
import com.keremc.core.util.Callback;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class MyLoadoutsMenu extends Menu {

    private Callback<MatchLoadout> callback;
    private boolean queue;

    @Override
    public String getTitle(Player player) {
        return "Custom Loadouts";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(4, new BackButton(new LoadoutSelectionMenu(callback, queue)));

        buttons.put(8, new CreateCustomLoadoutButton(callback, queue, this));

        int i = 0;
        PlayerData pd = AchillesPlugin.getInstance().getPlayerHandler().getSession(player.getUniqueId());

        for (MatchLoadout loadout : pd.getCustomLoadouts()) {
            buttons.put(10 + i++, new LoadoutButton(loadout, callback, false, queue));
            if ((i + 2) % 9 == 0) {
                i += 2;
            }
        }

        return buttons;
    }
}
