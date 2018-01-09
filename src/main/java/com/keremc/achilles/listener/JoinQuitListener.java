package com.keremc.achilles.listener;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.data.SaveHandler;
import com.keremc.achilles.data.PlayerData;
import com.keremc.achilles.match.Match;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class JoinQuitListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        AchillesPlugin.getInstance().getPlayerHandler().handleJoin(event.getPlayer());

        for (Match match : AchillesPlugin.getInstance().getMatchHandler().getMatches()) {
            for (Player member : match.getPlayers()) {
                member.hidePlayer(event.getPlayer());
                event.getPlayer().hidePlayer(member);
            }
        }
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        boolean loaded = AchillesPlugin.getInstance().getPlayerHandler().handleLogin(event.getPlayer());

        if (!loaded) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§cData could not be loaded... Try again");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        PlayerData pd = AchillesPlugin.getInstance().getPlayerHandler().getSession(event.getPlayer().getUniqueId());

        AchillesPlugin.getInstance().getMatchHandler().cleanup(event.getPlayer().getUniqueId());

        Match match = AchillesPlugin.getInstance().getMatchHandler().getMatch(event.getPlayer());

        if (match != null) {
            match.quit(event.getPlayer());
        } else if (pd.hasCombatLogged()) {
            event.getPlayer().setHealth(0D);
            pd.respawn();
        }

        SaveHandler.save(pd);
    }

    @EventHandler
    public void onAsyncPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        String alias = event.getName();

        AchillesPlugin.getInstance().getPlayerHandler().loadData(uuid, alias);
    }
}
