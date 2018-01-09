package com.keremc.achilles.listener;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.chat.Style;
import com.keremc.achilles.match.Match;
import com.keremc.achilles.match.MatchState;
import com.keremc.achilles.region.Tag;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class MatchRestrictionListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (AchillesPlugin.getInstance().getMatchHandler().isInMatch(player)) {
            player.sendMessage(ChatColor.RED + "You cannot execute commands while in a match!");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Match match = AchillesPlugin.getInstance().getMatchHandler().getMatch(player);

        if (match != null) {
            if (!Tag.ARENA.isTagged(event.getTo()) && match.getState() == MatchState.FIGHTING) {

                player.sendMessage(Style.header("§cKilled for cheating"));
                match.eliminated(player);
            }
        }

    }
}
