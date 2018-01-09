package com.keremc.achilles.match.gui.button;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.data.PlayerData;
import com.keremc.achilles.match.PlayerMatchInvite;
import com.keremc.achilles.statistics.StatSlot;
import com.keremc.core.menu.Button;
import com.keremc.core.util.UUIDUtils;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class PendingMatchRequestButton extends Button {
    private PlayerMatchInvite pmi;

    @Override
    public String getName(Player player) {
        return "§e§l" + UUIDUtils.name(pmi.getSender());
    }

    @Override
    public int getAmount(Player player) {
        return 30 - pmi.getLifetime();
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> lore = new ArrayList<>();

        PlayerData challengerData = AchillesPlugin.getInstance().getPlayerHandler().getSession(pmi.getSender());

        lore.add("");

        if (challengerData != null) {
            for (StatSlot ss : StatSlot.values()) {
                if (!ss.isFfa()) {
                    lore.add("§e" + ss.getFriendlyName() + "§f: " + challengerData.getStats().get(ss));
                }
            }

            lore.add("");
            lore.add("§eLoadout§f: §b" + pmi.getKitType().getName());
            lore.addAll(pmi.getKitType().getData(false));

            lore.add("");
            lore.add("§aLEFT-CLICK §6- §eAccept");
            lore.add("§cRIGHT-CLICK §6- §eDecline");
        }
        return lore;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.SKULL_ITEM;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, getAmount(player), getDamageValue(player));
        SkullMeta sm = (SkullMeta) skull.getItemMeta();
        sm.setOwner(UUIDUtils.name(pmi.getSender()));
        sm.setDisplayName(getName(player));
        sm.setLore(getDescription(player));

        skull.setItemMeta(sm);

        return skull;

    }

    @Override
    public byte getDamageValue(Player player) {
        return 3;
    }

    @Override
    public void clicked(Player player, int i, ClickType clickType, int hotbar) {
        if (clickType.isRightClick()) {
            AchillesPlugin.getInstance().getMatchHandler().declineInvitation(pmi);
            playNeutral(player);

        } else {
            if (AchillesPlugin.getInstance().getMatchHandler().hasPlayerInvite(pmi.getSender(), pmi.getTarget())) {
                AchillesPlugin.getInstance().getMatchHandler().acceptInvitation(pmi);
                playSuccess(player);
            } else {
                playFail(player);
            }
        }
    }
}
