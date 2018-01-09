package com.keremc.achilles.match;

import com.keremc.achilles.visual.LoadingBar;
import com.keremc.achilles.visual.Title;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class MatchStartLoadingBar extends LoadingBar {

    public MatchStartLoadingBar() {
        super("Match Countdown", "Match start!", Match.GRACE_PERIOD);
    }

    @Override
    public void on(Player player, double second) {
        if (Math.floor(second) == second && second > 0) {
            new Title("§a" + (int) second).fast().send(player);
            player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1L, 1L);

        }

    }

    @Override
    public void completed(Player player) {
        new Title("§eFight!").fast().send(player);
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1L, 20L);
    }
}
