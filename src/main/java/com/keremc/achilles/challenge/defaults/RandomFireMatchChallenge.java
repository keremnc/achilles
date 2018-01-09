package com.keremc.achilles.challenge.defaults;

import com.keremc.achilles.match.Match;
import com.keremc.achilles.challenge.MatchChallenge;
import com.keremc.core.CorePlugin;
import org.bukkit.entity.Player;

public class RandomFireMatchChallenge extends MatchChallenge {

    @Override
    public String getName() {
        return "Flammable Armor";
    }

    @Override
    public String getDescription() {
        return "Equipped with sub-par armor that catches fire way too easily...";
    }

    @Override
    public void tick(Match match) {
        if (CorePlugin.RANDOM.nextInt(100) == 1) {
            for (Player player : match.getPlayers()) {
                if (player != null) {
                    player.setFireTicks(200);
                }
            }
        }
    }
}

