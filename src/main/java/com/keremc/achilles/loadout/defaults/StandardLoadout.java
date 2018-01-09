package com.keremc.achilles.loadout.defaults;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.kit.Kit;
import com.keremc.achilles.kit.defaults.PVP;
import com.keremc.achilles.loadout.MatchLoadout;

import java.util.HashMap;

public class StandardLoadout extends MatchLoadout {
    private static final Kit backingKitReference = AchillesPlugin.getInstance().getKitHandler().getKitByClass(PVP.class);

    public StandardLoadout() {
        super("Standard",
                backingKitReference.getIcon(),
                backingKitReference.getArmor(),
                backingKitReference.getItems(),
                backingKitReference.getPotionEffects(),
                -1, true, false, -1L, 1, 1, new HashMap<>());
    }

    @Override
    public int getWeight() {
        return 1;
    }
}
