package com.keremc.achilles.loadout.builder.gui.button;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.data.PlayerData;
import com.keremc.achilles.chat.Style;
import com.keremc.achilles.loadout.MatchLoadout;
import com.keremc.achilles.loadout.builder.CustomLoadoutData;
import com.keremc.achilles.visual.AnvilGUI;
import com.keremc.core.menu.Button;
import com.keremc.core.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

@AllArgsConstructor
public class SaveLoadoutButton extends Button {
    private CustomLoadoutData data;
    private Menu menuToOpen;

    @Override
    public String getName(Player player) {
        return "§eSave Loadout";
    }

    @Override
    public List<String> getDescription(Player player) {
        return null;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.ENDER_CHEST;
    }

    @Override
    public byte getDamageValue(Player player) {
        return 0;
    }

    @Override
    public void clicked(Player player, int i, ClickType clickType, int hotbar) {
        Button.playNeutral(player);

        if (data.getName() == null) {
            player.closeInventory();
            AnvilGUI gui = new AnvilGUI(player,
                    (event) -> {
                        if (event.getSlot() == AnvilGUI.AnvilSlot.OUTPUT) {

                            String name = event.getName();
                            PlayerData pd = AchillesPlugin.getInstance().getPlayerHandler().getSession(player.getUniqueId());

                            for (MatchLoadout existing : pd.getCustomLoadouts()) {
                                if (existing.getName().equalsIgnoreCase(name)) {

                                    player.sendMessage(Style.header("§cYou already have a loadout with this name!"));

                                    playFail(player);
                                    return;
                                }
                            }

                            data.setName(name);
                            MatchLoadout ml = data.create();

                            save(pd, ml);
                            player.spigot().sendMessage(Style.format("§eLoadout %s has been saved!", ml));

                            Bukkit.getScheduler().runTask(AchillesPlugin.getInstance(), () -> menuToOpen.openMenu(player));
                        } else {
                            event.setWillClose(false);
                            event.setWillDestroy(false);
                        }
                    });

            ItemStack tag = new ItemStack(Material.NAME_TAG);
            ItemMeta meta = tag.getItemMeta();
            meta.setDisplayName("Loadout Name");
            tag.setItemMeta(meta);

            gui.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, tag);

            gui.open();
        } else {

            MatchLoadout created = data.create();
            PlayerData pd = AchillesPlugin.getInstance().getPlayerHandler().getSession(player.getUniqueId());

            pd.getCustomLoadouts().removeIf(m -> m.getName().equalsIgnoreCase(created.getName()));

            save(pd, created);
            menuToOpen.openMenu(player);

        }
    }

    public void save(PlayerData pd, MatchLoadout ml) {
        pd.getCustomLoadouts().add(ml);
        playSuccess(pd.getPlayer());
        pd.markForSave();

        AchillesPlugin.getInstance().getMatchHandler().removeFromCustomQueue(pd.getUuid());
    }
}
