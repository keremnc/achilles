package com.keremc.achilles.kit.gui;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.kit.Kit;
import com.keremc.achilles.kit.Rank;
import com.keremc.achilles.kit.gui.button.KitButton;
import com.keremc.achilles.kit.gui.button.ViewRentalsButton;
import com.keremc.core.menu.Button;
import com.keremc.core.menu.Menu;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class KitSelectMenu extends Menu {
    {
        setAutoUpdate(true);
    }

    @Setter private Rank rank;

    @Override
    public String getTitle(Player player) {
        return (rank == null ? "All" : rank.getName()) + " Kits";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        buttons.put(4, new ViewRentalsButton());

        int i = 0;
        for (Kit kit : AchillesPlugin.getInstance().getKitHandler().getKits()) {
            buttons.put(10 + i++, new KitButton(kit));
            if ((i + 2) % 9 == 0) {
                i += 2;
            }
        }

        return buttons;
    }
}
