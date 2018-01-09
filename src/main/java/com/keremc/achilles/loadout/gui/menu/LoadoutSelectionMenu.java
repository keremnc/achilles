package com.keremc.achilles.loadout.gui.menu;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.data.PlayerData;
import com.keremc.achilles.loadout.MatchLoadout;
import com.keremc.achilles.loadout.gui.button.CreateCustomLoadoutButton;
import com.keremc.achilles.loadout.gui.button.LoadoutButton;
import com.keremc.achilles.loadout.gui.button.MyLoadoutsButton;
import com.keremc.core.menu.Button;
import com.keremc.core.menu.Menu;
import com.keremc.core.util.Callback;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class LoadoutSelectionMenu extends Menu {
    private Callback<MatchLoadout> callback;
    private boolean queue;

    {
        setAutoUpdate(true);
        setUpdateAfterClick(false);
    }

    @Override
    public String getTitle(Player player) {
        return "Choose Loadout";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {

        Map<Integer, Button> buttons = new HashMap<>();
        PlayerData pd = AchillesPlugin.getInstance().getPlayerHandler().getSession(player.getUniqueId());

        buttons.put(4, new MyLoadoutsButton(callback, queue));

        MatchLoadout last = pd.getLastLoadout();

        if (last != null && callback != null) {
            //   buttons.put(4, new LoadoutButton(last, callback, true));

        }

        int i = 0;
        for (MatchLoadout ml : AchillesPlugin.getInstance().getMatchHandler().getMatchLoadouts()) {
            buttons.put(10 + i++, new LoadoutButton(ml, callback, false, queue));
            if ((i + 2) % 9 == 0) {
                i += 2;
            }
        }

        buttons.put(8, new CreateCustomLoadoutButton(callback, queue, this));


        return buttons;
    }
}
