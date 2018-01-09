package com.keremc.achilles.loadout.gui.button;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.data.PlayerData;
import com.keremc.achilles.chat.Style;
import com.keremc.achilles.loadout.MatchLoadout;
import com.keremc.achilles.loadout.builder.CustomLoadoutData;
import com.keremc.achilles.loadout.builder.gui.menu.LoadoutBuilderMenu;
import com.keremc.achilles.loadout.gui.menu.MyLoadoutsMenu;
import com.keremc.core.menu.Button;
import com.keremc.core.menu.Menu;
import com.keremc.core.menu.menus.ConfirmMenu;
import com.keremc.core.util.Callback;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

@AllArgsConstructor
public class LoadoutButton extends Button {
    private MatchLoadout loadout;
    private Callback<MatchLoadout> callback;
    private boolean last;
    private boolean queue;

    @Override
    public String getName(Player player) {
        boolean inQueue = player.getUniqueId() == AchillesPlugin.getInstance().getMatchHandler().getMatchSoloQueue().get(loadout);
        return (last ? "§eLast: " : "") + "§b" + loadout.getName() + (inQueue ? " §c[§eIn Queue§c]" : "");
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> data = loadout.getData(false);

        if (loadout.isCustom()) {
            data.add("");
            if (callback != null) {
                data.add("§6LEFT-CLICK§7 -§a Select");
            } else if (queue) {
                if (player.getUniqueId() == AchillesPlugin.getInstance().getMatchHandler().getMatchSoloQueue().get(loadout)) {
                    data.add("§6LEFT-CLICK§7 -§a Leave Queue");

                } else {
                    data.add("§6LEFT-CLICK§7 -§a Join Queue");
                }
            }

            data.add("§6RIGHT-CLICK§7 -§e Edit");
            data.add("§6SHIFT-CLICK§7 -§c Delete");

        }

        if (callback == null && queue && !loadout.isCustom()) {
            data.add("");

            if (player.getUniqueId() == AchillesPlugin.getInstance().getMatchHandler().getMatchSoloQueue().get(loadout)) {
                data.add("§6CLICK§7 -§a Leave Queue");

            } else {

                data.add("§6CLICK§7 -§a Join Queue");

                if (AchillesPlugin.getInstance().getMatchHandler().getMatchSoloQueue().get(loadout) != null) {
                    data.add("§cA player§b is waiting for a match!");
                }
            }
        }

        return data;
    }

    @Override
    public Material getMaterial(Player player) {
        return loadout.getIcon();
    }

    @Override
    public byte getDamageValue(Player player) {
        return 0;
    }

    @Override
    public void clicked(Player player, int i, ClickType clickType, int hotbar) {

        if (callback == null && queue && (!loadout.isCustom() || (clickType.isLeftClick() && !clickType.isShiftClick()))) {
            playSuccess(player);

            if (player.getUniqueId() == AchillesPlugin.getInstance().getMatchHandler().getMatchSoloQueue().get(loadout)) {
                AchillesPlugin.getInstance().getMatchHandler().getMatchSoloQueue().remove(loadout);

                player.spigot().sendMessage(Style.format("§eYou have been removed from the %s queue.", loadout));

            } else {
                AchillesPlugin.getInstance().getMatchHandler().queue(player, loadout);
            }
            Menu.updateButton(player, this);
        } else if (clickType.isShiftClick() && loadout.isCustom()) {
            ConfirmMenu delete = new ConfirmMenu("Delete Loadout?",
                    (b) -> {
                        if (b) {
                            PlayerData pd = AchillesPlugin.getInstance().getPlayerHandler().getSession(player.getUniqueId());

                            pd.getCustomLoadouts().removeIf(ml ->
                                    ml == loadout
                            );

                            Button.playSuccess(player);

                        }
                        new MyLoadoutsMenu(callback, queue).openMenu(player);

                    }, false,
                    Button.placeholder(loadout.getIcon(), (byte) 1, "§eDelete §b" + loadout.getName() + "§e?")
            );

            delete.openMenu(player);

        } else if (!loadout.isCustom() || (clickType.isLeftClick() && callback != null)) {
            callback.callback(loadout);

        } else if (clickType.isRightClick() && loadout.isCustom()) {
            new LoadoutBuilderMenu(callback, CustomLoadoutData.from(loadout.getName(), loadout.getCreated(), loadout.getMetadata()), queue, new MyLoadoutsMenu(callback, queue)).openMenu(player);
        }

    }
}
