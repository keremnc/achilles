package com.keremc.achilles.kit;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.kit.defaults.PVP;
import com.keremc.core.util.ClassUtils;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KitHandler {
    @Getter private List<Kit> kits = new ArrayList<>();

    public KitHandler() {
        loadKits();
    }

    public void loadKits() {
        try {
            for (Class kitClass : ClassUtils.getClassesInPackage(AchillesPlugin.getInstance(), "com.keremc.achilles.kit.defaults")) {
                if (Kit.class.isAssignableFrom(kitClass)) {
                    Kit kit = (Kit) kitClass.newInstance();
                    kits.add(kit);

                    Bukkit.getPluginManager().registerEvents(kit, AchillesPlugin.getInstance());

                }
            }
            Collections.sort(kits);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Kit getById(int id) {
        return kits.stream().filter(k -> k.getId() == id).findAny().orElse(null);
    }

    public Kit getKitByName(String string) {
        return kits.stream().filter(k -> k.getName().equalsIgnoreCase(string)).findAny().orElse(null);
    }

    public Kit getKitByClass(Class<? extends Kit> clazz) {
        return kits.stream().filter(kit -> clazz.isAssignableFrom(kit.getClass())).findAny().orElse(null);
    }

    public Kit getDefault() {
        return getKitByClass(PVP.class);
    }
}
