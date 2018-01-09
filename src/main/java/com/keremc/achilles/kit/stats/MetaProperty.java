package com.keremc.achilles.kit.stats;

import com.google.common.base.Function;
import com.keremc.achilles.data.PlayerData;
import com.keremc.achilles.kit.Kit;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MetaProperty {
    private Kit kit;
    private String key;
    private double def;

    public double get(PlayerData pd) {
        return pd.getStats().getKitStats(kit).getProperties().getOrDefault(key, def);
    }

    public void set(PlayerData pd, double data) {
        pd.getStats().getKitStats(kit).getProperties().put(key, data);
    }

    public void mod(PlayerData pd, Function<Double, Double> func) {
        set(pd, func.apply(get(pd)));
    }

}
