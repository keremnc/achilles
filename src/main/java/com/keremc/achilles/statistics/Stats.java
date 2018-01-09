package com.keremc.achilles.statistics;

import com.keremc.achilles.chat.Style;
import com.keremc.achilles.kit.Kit;
import com.keremc.achilles.kit.stats.KitStatistics;
import com.mongodb.BasicDBObject;
import lombok.Data;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Stats {

    private Map<String, KitStatistics> kitStatistics = new HashMap<>();
    private Map<StatSlot, Double> statSlots = new HashMap<>();

    public double get(StatSlot slot) {
        if (slot.isDynamic()) {
            if (slot == StatSlot.KDR) {
                return get(StatSlot.KILLS) / (get(StatSlot.DEATHS) == 0 ? 1 : get(StatSlot.DEATHS));
            } else if (slot == StatSlot.WLR) {
                return get(StatSlot.DUEL_WINS) / (get(StatSlot.DUEL_LOSSES) == 0 ? 1 : get(StatSlot.DUEL_LOSSES));
            }
        }
        return statSlots.getOrDefault(slot, 0D);
    }

    public void set(StatSlot slot, double i) {
        statSlots.put(slot, i);
    }

    public void inc(StatSlot slot) {
        inc(slot, 1);
    }

    public void inc(StatSlot slot, double delta) {
        set(slot, get(slot) + delta);
    }

    public void fromJSON(BasicDBObject dbo) {
        if (dbo != null) {
            for (StatSlot ss : StatSlot.values()) {
                set(ss, dbo.getInt(ss.getDbKey(), 0));
            }
        }
    }

    public BasicDBObject toJSON() {
        BasicDBObject dbo = new BasicDBObject();

        for (StatSlot ss : StatSlot.values()) {
            dbo.put(ss.getDbKey(), get(ss));
        }

        return dbo;
    }

    public KitStatistics getKitStats(Kit kit) {
        kitStatistics.putIfAbsent(kit.getName(), new KitStatistics());
        return kitStatistics.get(kit.getName());
    }

    public TextComponent[] createTooltipStatArray(boolean tooltip) {
        String format = tooltip ? "\n§f%s§7: %s" : "§6%s§f: %s";

        List<TextComponent> texts = new ArrayList<>();

        int i = 0;
        boolean split = true;
        for (StatSlot statSlot : StatSlot.values()) {
            if ((!split && i == 2) || (split && i == 3)) {
                texts.add(new TextComponent(tooltip ? "\n" : ""));
                i = 0;
                split = !split;
            }

            texts.add(new TextComponent(String.format(format, statSlot.getFriendlyName(), Style.DOUBLE_FORMAT.format(get(statSlot)))));
            i++;
        }

        return texts.toArray(new TextComponent[texts.size()]);
    }
}
