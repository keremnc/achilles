package com.keremc.achilles.listener;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.data.PlayerData;
import com.keremc.achilles.kit.Kit;
import com.keremc.achilles.match.Match;
import com.keremc.achilles.match.MatchState;
import com.keremc.achilles.region.Tag;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveListener implements Listener {


    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        PlayerData ps = AchillesPlugin.getInstance().getPlayerHandler().getSession(event.getPlayer().getUniqueId());

        Location to = event.getTo();
        Location from = event.getFrom();

        if (to.getX() != from.getX() || to.getZ() != from.getZ() || to.getY() != from.getY()) {

            Match match = AchillesPlugin.getInstance().getMatchHandler().getMatch(event.getPlayer());

            if (match != null && match.getState() == MatchState.GRACE_PERIOD) {
                Location newTo = from.clone();
                newTo.setPitch(to.getPitch());
                newTo.setYaw(to.getYaw());

                event.setTo(newTo);
                return;
            }

            if (ps.isWarping()) {
                if (from.distance(to) > 0.1) {
                    ps.cancelWarp();
                }
            }

            if (ps.isDuelMode()) {
                ps.setSpawnProt(false);
                return;
            }

            if (ps.isSpawnProt() && !Tag.SPAWN.isTagged(event.getTo())) {
                ps.setSpawnProt(false);
                event.getPlayer().sendMessage(ChatColor.GRAY + "You no longer have spawn protection.");


                if (ps.getSelectedKit() == null && !ps.isCustomInv()) {
                    Kit defKit = AchillesPlugin.getInstance().getKitHandler().getDefault();

                    if (ps.getLastKit() != null && ps.ownsKit(ps.getLastKit())) {
                        defKit = ps.getLastKit();
                    }

                    defKit.applyKit(event.getPlayer());
                }

            }
        }
    }
}
