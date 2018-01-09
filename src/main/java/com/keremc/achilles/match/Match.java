package com.keremc.achilles.match;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.chat.Style;
import com.keremc.achilles.command.ViewMatchInvCommand;
import com.keremc.achilles.data.PlayerData;
import com.keremc.achilles.loadout.MatchLoadout;
import com.keremc.achilles.match.data.PostMatchData;
import com.keremc.achilles.region.Tag;
import com.keremc.achilles.statistics.StatSlot;
import com.keremc.achilles.statistics.Stats;
import com.keremc.achilles.util.FaceUtil;
import com.keremc.achilles.visual.Title;
import com.keremc.core.util.BMComponentBuilder;
import com.keremc.core.util.PlayerUtils;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
@Data
public class Match {
    public static final int GRACE_PERIOD = 3;

    @NonNull
    private String id;
    @NonNull
    private UUID player1;
    @NonNull
    private UUID player2;
    @NonNull
    private MatchLoadout kitType;
    @NonNull
    private boolean ranked;

    private MatchState state;
    private long startedAt = -1;
    private long endedAt = -1;

    private boolean quitEnded = false;

    private int matchAmount = 1;
    private Map<UUID, Integer> wins = new HashMap<>();

    private String winnerName;

    public void setup() {
        state = MatchState.GRACE_PERIOD;

        for (Player other : Bukkit.getOnlinePlayers()) {
            for (Player member : getPlayers()) {
                if (!contains(other)) {
                    member.hidePlayer(other);
                    other.hidePlayer(member);
                }
            }
        }

        Location[] corners = getSpawnPoints();
        for (int i = 0; i < 2; i++) {
            final int index = i;
            Bukkit.getScheduler().runTaskLater(AchillesPlugin.getInstance(),
                    () -> getPlayers()[index].teleport(corners[index]), 5 * i);
        }


        Bukkit.getScheduler().runTaskLater(AchillesPlugin.getInstance(), () -> {
                    for (Player player : getPlayers()) {
                        if (player == null) {
                            finished(null);
                            return;
                        }

                        player.showPlayer(Bukkit.getPlayer(getOpposite(player.getUniqueId())));
                        new MatchStartLoadingBar().display(player);

                        kitType.apply(player);
                    }

                    Bukkit.getScheduler().runTaskLater(AchillesPlugin.getInstance(), () -> start(), GRACE_PERIOD * 20L);
                },
                10L);


    }

    public Location[] getSpawnPoints() {
        Location[] loc = new Location[2];
        Location c1 = Tag.ARENA_SPAWNPOINTS.getRegions().get(0).getLowerCorner();
        Location c2 = Tag.ARENA_SPAWNPOINTS.getRegions().get(0).getUpperCorner();


        BlockFace bf1 = FaceUtil.getDirection(c1, c2, true);
        c1.setDirection(FaceUtil.faceToVector(bf1));


        BlockFace bf2 = FaceUtil.getDirection(c2, c1, true);
        c2.setDirection(FaceUtil.faceToVector(bf2));


        loc[0] = c1;
        loc[1] = c2;

        return loc;
    }

    public void start() {
        if (state == MatchState.FINISHED) {
            return;
        }

        state = MatchState.FIGHTING;

        startedAt = System.currentTimeMillis();
    }

    public void finished(Player winner) {
        state = MatchState.FINISHED;
        endedAt = System.currentTimeMillis();
        this.winnerName = winner == null ? "No one" : winner.getName();
        final AtomicBoolean again = new AtomicBoolean(false);

        if (winner != null) {
            wins.put(winner.getUniqueId(), wins.getOrDefault(winner.getUniqueId(), 0) + 1);


            if (!quitEnded && wins.get(winner.getUniqueId()) < matchAmount) {
                again.set(true);
            }
        }

        Player p1 = Bukkit.getPlayer(player1);
        Player p2 = Bukkit.getPlayer(player2);

        Title winnerTitle = new Title("§e§l" + this.winnerName + " wins!",
                again.get() ?
                        p1.getDisplayName() + "§e(§b" + wins.getOrDefault(player1, 0) + "§e)  §7:  "
                                + p2.getDisplayName() + "§e(§b" + wins.getOrDefault(player2, 0) + "§e)"
                        : ""
        );

        for (Player player : getPlayers()) {
            if (player != null) {
                winnerTitle.send(player);
            }
        }


        boolean newMatch = again.get();

        if (!newMatch) {
            endMatch(winner);
        }

        Bukkit.getScheduler().runTaskLater(AchillesPlugin.getInstance(), () -> reset(newMatch), 120L);
    }

    public void endMatch(Player winner) {
        if (winner != null) {
            Player loser = Bukkit.getPlayer(getOpposite(winner.getUniqueId()));

            PlayerData winnerData = AchillesPlugin.getInstance().getPlayerHandler().getSession(winner.getUniqueId());
            PlayerData loserData = AchillesPlugin.getInstance().getPlayerHandler().getSession(loser.getUniqueId());

            Stats ws = winnerData.getStats();
            Stats ls = loserData.getStats();

            ws.inc(StatSlot.DUEL_WINS);
            ws.inc(StatSlot.WINSTREAK);

            if (ws.get(StatSlot.WINSTREAK) > ws.get(StatSlot.WINSTREAK_MAX)) {
                ws.set(StatSlot.WINSTREAK_MAX, ws.get(StatSlot.WINSTREAK));
            }

            ls.inc(StatSlot.DUEL_LOSSES);
            ls.set(StatSlot.WINSTREAK, 0);

            String winnerMessage = Style.header("You have §awon§e the §6duel§e against§b " + loser.getName() + "§e!");
            String loserMessage = Style.header("You have §clost§e the §6duel§e against§b " + getWinnerName() + "§e!");

            BMComponentBuilder invViewBuilder = new BMComponentBuilder(Style.HEADER);
            invViewBuilder.append("Click").color(ChatColor.YELLOW);

            invViewBuilder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/viewmatchinv " + winner.getName()));
            invViewBuilder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BMComponentBuilder("Open " + winner.getName() + "'s inventory").create()));

            invViewBuilder.append(" here ").color(ChatColor.RED);
            invViewBuilder.append("to see ").color(ChatColor.YELLOW);
            invViewBuilder.append("opponent inventory").color(ChatColor.GREEN);

            loser.sendMessage(loserMessage);
            winner.sendMessage(winnerMessage);

            ViewMatchInvCommand.saveInventoryView(winner.getUniqueId(), PostMatchData.fromPlayer(winner), loser.getUniqueId());
            loser.spigot().sendMessage(invViewBuilder.create());

        }
    }

    public void reset(boolean again) {


        for (Player player : getPlayers()) {

            if (player != null) {
                PlayerData pd = AchillesPlugin.getInstance().getPlayerHandler().getSession(player.getUniqueId());

                player.setAllowFlight(false);
                player.setFlying(false);

                if (!again) {
                    pd.respawn();
                } else {
                    setup();
                }
            }
        }

        if (!again) {
            for (Player other : Bukkit.getOnlinePlayers()) {
                Match match = AchillesPlugin.getInstance().getMatchHandler().getMatch(other);

                if (match == null) {

                    for (Player member : getPlayers()) {
                        if (member != null) {

                            other.showPlayer(member);

                            if (!other.hasMetadata("invisible")) {
                                member.showPlayer(other);
                            }
                        }
                    }

                }
            }

            AchillesPlugin.getInstance().getMatchHandler().dispose(this);
        }
    }

    @Override
    public int hashCode() {
        return player1.hashCode() ^ player2.hashCode();
    }

    public void quit(Player player) {
        quitEnded = true;

        finished(Bukkit.getPlayer(getOpposite(player.getUniqueId())));

        PlayerUtils.resetInventory(player, GameMode.SURVIVAL);
        player.teleport(AchillesPlugin.getInstance().getPlayerHandler().chooseDuelSpawnLocation());
    }

    public void eliminated(Player player) {
        finished(Bukkit.getPlayer(getOpposite(player.getUniqueId())));

        PlayerUtils.resetInventory(player, GameMode.SPECTATOR);

        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10, 2), true);
        player.setAllowFlight(true);
        player.teleport(player.getLocation().add(0, 3, 0));
        player.setFlying(true);
    }

    public UUID getOpposite(UUID uuid) {
        if (uuid.equals(player1)) {
            return player2;
        }
        return player1;
    }

    public boolean contains(Player player) {
        return player.getUniqueId().equals(player1) || player.getUniqueId().equals(player2);
    }

    public Player[] getPlayers() {
        return new Player[]{Bukkit.getPlayer(player1), Bukkit.getPlayer(player2)};
    }

    public void messageAll(String message) {
        for (Player player : getPlayers()) {
            player.sendMessage(message);
        }
    }

    public void messageAll(BaseComponent... component) {
        for (Player player : getPlayers()) {
            player.spigot().sendMessage(component);
        }
    }
}
