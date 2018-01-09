package com.keremc.achilles.kit.gui;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.data.PlayerData;
import com.keremc.achilles.kit.Kit;
import com.keremc.achilles.kit.gui.button.KitButton;
import com.keremc.core.menu.Button;
import com.keremc.core.menu.Menu;
import com.keremc.core.menu.buttons.BackButton;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class RentalMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "My Rentals";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        buttons.put(4, new BackButton(new KitSelectMenu(null)));

        int i = 0;
        PlayerData pd = AchillesPlugin.getInstance().getPlayerHandler().getSession(player.getUniqueId());

        for (String rental : pd.getKitRentals().keySet()) {
            Kit kit = AchillesPlugin.getInstance().getKitHandler().getKitByName(rental);
            buttons.put(10 + i++, new KitButton(kit));
            if ((i + 2) % 9 == 0) {
                i += 2;
            }
        }

        return buttons;
    }
}
