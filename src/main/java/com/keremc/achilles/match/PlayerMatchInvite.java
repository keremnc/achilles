package com.keremc.achilles.match;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.loadout.MatchLoadout;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor(access=AccessLevel.PRIVATE)
public class PlayerMatchInvite {

    @Getter private UUID sender;
    @Getter private UUID target;
    @Getter private MatchLoadout kitType;
    @Getter private long sent;
    @Getter private boolean rematch;

    public static PlayerMatchInvite createMatchInvite(UUID sender, UUID target, MatchLoadout detailedKitType, boolean isRematch) {

        if (detailedKitType == null) {
            detailedKitType = AchillesPlugin.getInstance().getMatchHandler().getMatchLoadouts().get(0);
        }

        return new PlayerMatchInvite(sender, target, detailedKitType, System.currentTimeMillis(), isRematch);
    }

    public int getLifetime() {
        return ((int) (System.currentTimeMillis() - sent) / 1000);
    }

    public boolean isValid() {
        return (getLifetime() <= MatchHandler.INVITE_TIMEOUT);
    }

}