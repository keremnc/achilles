package com.keremc.achilles.loadout;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.chat.Style;
import com.keremc.achilles.data.PlayerData;
import com.keremc.achilles.kit.item.Armor;
import com.keremc.achilles.kit.item.Items;
import com.keremc.achilles.loadout.builder.CustomLoadoutData;
import com.keremc.core.item.ItemBuilder;
import com.keremc.core.util.PlayerUtils;
import com.mongodb.BasicDBObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.server.v1_8_R3.LocaleI18n;
import net.minecraft.server.v1_8_R3.MobEffectList;
import org.apache.commons.lang.StringUtils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Data
public class MatchLoadout {
    private String name;
    private Material icon;
    private Armor armor;
    private Items items;
    private PotionEffect[] potions;
    private int healingAmount;
    private boolean soup;
    private boolean custom;
    private long created;
    private int weight;
    private int firstTo;
    private Map<String, String> metadata;

    public List<String> getData(boolean hover) {
        ArrayList<String> data = new ArrayList<>();

        String spacer = hover ? "\n" : "";

        if (hover) {
            data.add("§b§l" + name);
        }

        data.add(spacer + "");

        List<String> armorData = ItemBuilder.wrap(armor.info(), "§f", 30);
        List<String> itemData = ItemBuilder.wrap(items.info(), "§f", 30);

        data.add(spacer + "§eArmor§f: " + (armorData.size() > 0 ? armorData.remove(0) : ""));
        int armorHeaderSpace = (int) Math.ceil(Style.strSize("Armor: ") / 4D);

        armorData.forEach(s -> data.add(spacer + StringUtils.repeat(" ", armorHeaderSpace) + s));

        data.add(spacer + "§eItems§f: " + (itemData.size() > 0 ? itemData.remove(0) : ""));
        int itemHeaderSpace = (int) Math.ceil(Style.strSize("Items: ") / 4D);

        itemData.forEach(s -> data.add(spacer + StringUtils.repeat(" ", itemHeaderSpace) + s));


        data.add(spacer + "§eEffects§f: " + getPotionEffectInfo());
        data.add(spacer + "§eWins§f: " + (firstTo == 1 ? "Single match" : "First to " + firstTo));

        if (custom && !hover) {
            data.add("");
            data.add("§eCreated§f: " + Style.DATE_FORMAT.format(created));
        }

        return data;
    }

    public String getPotionEffectInfo() {
        String data = "";

        for (PotionEffect pe : potions) {
            String potData = LocaleI18n.a(MobEffectList.byId[pe.getType().getId()].a()) + " " + LocaleI18n.a("potion.potency." + pe.getAmplifier());

            data += (data.isEmpty() ? "" : ", ") + potData;
        }

        return data;
    }

    public void apply(Player player) {
        PlayerUtils.resetInventory(player, GameMode.SURVIVAL);

        Armor armor = getArmor();
        Items items = getItems();

        if (armor != null) {
            armor.apply(player);
        }

        if (items != null) {
            player.getInventory().addItem(items.getItems());
        }

        if (potions != null) {
            for (PotionEffect pe : potions) {
                player.addPotionEffect(pe, true);
            }
        }

        int healed = 0;
        while (player.getInventory().firstEmpty() != -1 && (healingAmount == -1 || healed++ < healingAmount)) {
            player.getInventory().addItem(
                    soup ? new ItemStack(Material.MUSHROOM_SOUP) : new ItemStack(Material.POTION, 1, (byte) 16421)
            );
        }

        player.getInventory().setHeldItemSlot(0);

        PlayerData pd = AchillesPlugin.getInstance().getPlayerHandler().getSession(player.getUniqueId());

        pd.setCustomInv(false);
        pd.setDuelMode(true);
        pd.setSelectedKit(null);
        pd.setSpawnProt(false);

        pd.markForSave();
    }

    public String meta() {
        return metadata.entrySet().stream().map(e -> e.getKey() + "@" + e.getValue()).collect(Collectors.joining(","));
    }

    public BasicDBObject serialize() {
        BasicDBObject data = new BasicDBObject();
        data.put("name", name);
        data.put("timestamp", created);
        data.put("metadata", metadata);

        return data;
    }

    public static MatchLoadout fromJSON(BasicDBObject dbo) {
        return CustomLoadoutData.from(dbo.getString("name"), dbo.getLong("timestamp"), (Map<String, String>) dbo.get("metadata")).create();
    }

}
