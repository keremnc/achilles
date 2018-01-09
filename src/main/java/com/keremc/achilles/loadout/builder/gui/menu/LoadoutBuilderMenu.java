package com.keremc.achilles.loadout.builder.gui.menu;

import com.keremc.achilles.loadout.MatchLoadout;
import com.keremc.achilles.loadout.builder.CustomLoadoutData;
import com.keremc.achilles.loadout.builder.gui.button.CustomPartButton;
import com.keremc.achilles.loadout.builder.gui.button.FinalizeLoadoutButton;
import com.keremc.achilles.loadout.builder.gui.button.SaveLoadoutButton;
import com.keremc.core.menu.Button;
import com.keremc.core.menu.Menu;
import com.keremc.core.menu.buttons.BackButton;
import com.keremc.core.util.Callback;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class LoadoutBuilderMenu extends Menu {

    private static final int HELMET_SLOT = 10;
    private static final int CHESTPLATE_SLOT = 19;
    private static final int LEGGINGS_SLOT = 28;
    private static final int BOOTS_SLOT = 37;

    private static final int SWORD_SLOT = 21;
    private static final int SOUP_SLOT = 23;
    private static final int AMOUNT_SLOT = 40;

    private static final int SPEED_SLOT = 25;
    private static final int STRENGTH_SLOT = 34;

    private Callback<MatchLoadout> callback;
    private CustomLoadoutData data;
    private boolean queue;
    private Menu backMenu;

    {
        setAutoUpdate(false);
        setUpdateAfterClick(false);
    }

    @Override
    public String getTitle(Player player) {
        return "Build Custom Loadout";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(0, new SaveLoadoutButton(data, backMenu));
        buttons.put(4, new BackButton(backMenu));

        if (callback != null) {
            buttons.put(8, new FinalizeLoadoutButton(callback, data));
        }

        buttons.put(HELMET_SLOT, new CustomPartButton(data.getHelmet()));
        buttons.put(CHESTPLATE_SLOT, new CustomPartButton(data.getChestplate()));
        buttons.put(LEGGINGS_SLOT, new CustomPartButton(data.getLeggings()));
        buttons.put(BOOTS_SLOT, new CustomPartButton(data.getBoots()));

        buttons.put(SWORD_SLOT, new CustomPartButton(data.getSword()));
        buttons.put(SOUP_SLOT, new CustomPartButton(data.getSoupAmount()));
        buttons.put(AMOUNT_SLOT, new CustomPartButton(data.getMatchAmount()));

        buttons.put(SPEED_SLOT, new CustomPartButton(data.getSpeedII()));
        buttons.put(STRENGTH_SLOT, new CustomPartButton(data.getStrengthII()));

        return buttons;
    }
}
