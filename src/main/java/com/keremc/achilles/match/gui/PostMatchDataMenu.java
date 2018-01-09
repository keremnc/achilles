package com.keremc.achilles.match.gui;

import com.keremc.achilles.chat.Style;
import com.keremc.achilles.match.gui.button.PostMatchDataItemButton;
import com.keremc.achilles.match.data.PostMatchData;
import com.keremc.core.menu.Button;
import com.keremc.core.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class PostMatchDataMenu extends Menu {

    private String playerName;
    private PostMatchData postMatchData;

    public String getTitle(Player player) {
        return Style.rightAlign(playerName, postMatchData.getHealth() / 2D + "§c❤", 33);
    }

    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int x = 0;
        int y = 0;

        for (ItemStack inventoryItem : postMatchData.getInventoryContents()) {
            buttons.put(getSlot(x, y), new PostMatchDataItemButton(inventoryItem));

            if (x++ >= 9) {
                x = 0;
                y++;
            }
        }

        y++;
        x = 3;

        for (ItemStack armorItem : postMatchData.getArmorContents()) {
            buttons.put(getSlot(x--, y), new PostMatchDataItemButton(armorItem));
        }

        return (buttons);
    }

}