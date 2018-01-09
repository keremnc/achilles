package com.keremc.achilles.data;

import com.keremc.achilles.AchillesPlugin;
import org.bukkit.Bukkit;

public class SaveHandler {

    public static void save(PlayerData playerData) {
        Bukkit.getScheduler().runTaskAsynchronously(AchillesPlugin.getInstance(), () ->{
            AchillesPlugin.getInstance().getDataHandler().save(playerData);
        });
    }


    public static void saveAll() {
       for (PlayerData pd : AchillesPlugin.getInstance().getPlayerHandler().getSessions().values()) {
           AchillesPlugin.getInstance().getDataHandler().save(pd);

       }
    }
}
