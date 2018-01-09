package com.keremc.achilles.challenge;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.match.Match;
import com.keremc.core.util.ClassUtils;

public abstract class MatchChallenge {

    static {
        try {
            for (Class clazz : ClassUtils.getClassesInPackage(AchillesPlugin.getInstance(), "com.keremc.achilles.challenge.defaults")) {

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public abstract String getName();
    public abstract String getDescription();

    public abstract void tick(Match match);


}
