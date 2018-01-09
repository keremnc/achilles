package com.keremc.achilles.match;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.chat.Style;
import com.keremc.achilles.data.PlayerData;
import com.keremc.achilles.data.PlayerHandler;
import com.keremc.achilles.loadout.MatchLoadout;
import com.keremc.achilles.loadout.defaults.StandardLoadout;
import com.keremc.core.util.BMComponentBuilder;
import com.keremc.core.util.ClassUtils;
import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class MatchHandler {
    public static final int INVITE_TIMEOUT = 30;

    @Getter private Set<Match> matches = new HashSet<>();
    @Getter private List<MatchLoadout> matchLoadouts = new ArrayList<>();
    @Getter private Map<MatchLoadout, UUID> matchSoloQueue = new HashMap<>();
    @Getter private Set<PlayerMatchInvite> playerMatchInvites = new HashSet<>();

    @Getter private UUID quickmatch;

    public MatchHandler() {
        loadMatchTypes();

        new BukkitRunnable() {

            public void run() {
                Iterator<PlayerMatchInvite> playerInviteIterator = playerMatchInvites.iterator();

                while (playerInviteIterator.hasNext()) {
                    PlayerMatchInvite invite = playerInviteIterator.next();

                    Player inviter = Bukkit.getPlayer(invite.getSender());
                    Player target = Bukkit.getPlayer(invite.getTarget());

                    PlayerHandler ph = AchillesPlugin.getInstance().getPlayerHandler();

                    if (!invite.isValid() || inviter == null || target == null || !ph.getSession(inviter.getUniqueId()).isDuelMode() || ph.getSession(inviter.getUniqueId()).isInMatch()) {
                        playerInviteIterator.remove();
                    }
                }
            }

        }.runTaskTimer(AchillesPlugin.getInstance(), 20L, 20L);
    }

    public MatchLoadout getLoadoutFromClass(Class<? extends MatchLoadout> clazz) {
        return matchLoadouts.stream().filter( c->c.getClass() == clazz).findFirst().orElse(null);
    }

    public void loadMatchTypes() {
        try {
            for (Class clazz : ClassUtils.getClassesInPackage(AchillesPlugin.getInstance(), "com.keremc.achilles.loadout.defaults")) {
                if (MatchLoadout.class.isAssignableFrom(clazz)) {
                    MatchLoadout loadout = (MatchLoadout) clazz.newInstance();
                    matchLoadouts.add(loadout);
                }
            }
            Collections.sort(matchLoadouts, (o1, o2) -> ((Integer) o1.getWeight()).compareTo(o2.getWeight()));

        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public boolean isInMatch(Player player) {
        return matches.stream().filter(m -> m.contains(player)).findAny().isPresent();
    }

    public Match getMatch(Player player) {
        for (Match match : matches) {
            if (match.contains(player)) {
                return match;
            }
        }
        return null;
    }

    public void quickMatch(Player clicker, Player clicked, MatchLoadout matchLoadout) {

        if (AchillesPlugin.getInstance().getMatchHandler().isInMatch(clicker) || AchillesPlugin.getInstance().getMatchHandler().isInMatch(clicked)) {
            return;
        }

        if (hasPlayerInvite(clicker.getUniqueId(), clicked.getUniqueId(), matchLoadout)) {
            PlayerMatchInvite pending = getPlayerInvite(clicker.getUniqueId(), clicked.getUniqueId());


            clicker.sendMessage(Style.header("§eA pending match request exists with this player."));
            clicker.sendMessage(Style.header("§eMatch request expires in §c§l" + (30 - pending.getLifetime()) + "s"));

            return;
        }

        clicker.spigot().playEffect(clicker.getLocation(), Effect.FIREWORKS_SPARK, 2, 2, .5F, .5F, .5F, 2, 100, 2);
        clicker.spigot().playEffect(clicker.getLocation(), Effect.COLOURED_DUST, 2, 2, .5F, .5F, .5F, 2, 100, 2);
        clicker.playSound(clicker.getLocation(), Sound.NOTE_PIANO, 20F, 0.5F);

        PlayerMatchInvite pmi = PlayerMatchInvite.createMatchInvite(clicker.getUniqueId(), clicked.getUniqueId(), matchLoadout, false);
        registerInvitation(pmi);
    }

    public void acceptAny(Player clicker) {
        for (PlayerMatchInvite pmi : getAllPlayerInvites(clicker.getUniqueId())) {
            if (pmi.isValid()) {
                acceptInvitation(pmi);
                return;

            }
        }

        clicker.sendMessage(Style.header("You have no match invites!"));

    }

    public int getMatchesAvailable(Player player) {
        int counter = 0;
        UUID uuid = player.getUniqueId();

        counter += getAllPlayerInvites(uuid).length;

        if (quickmatch != null && quickmatch != uuid) {
            counter++;
        }

        counter += matchSoloQueue.entrySet().stream().filter(e -> e.getValue() != uuid).count();

        return counter;

    }

    /**
     * Finds the next possible match for a player.
     * <p>
     * <ol>
     * <li>Finds any requests that are valid towards this player</li>
     * <li>If any other player is in the quickmatch queue</li>
     * <li>Checks if any custom matches are queued</li>
     * <li>Checks if any regular matches are queued</li>
     * <li>Finally, adds them to the quickmatch queue</li>
     * </ol>
     * </p>
     *
     * @param clicker player to queue
     */
    public void quickMatchQueue(Player clicker) {

        MatchLoadout ml = null;

        for (PlayerMatchInvite pmi : getAllPlayerInvites(clicker.getUniqueId())) {
            UUID sender = pmi.getSender();
            Player p = Bukkit.getPlayer(sender);

            if (p != null) {
                PlayerData pd = AchillesPlugin.getInstance().getPlayerHandler().getSession(sender);
                if (!pd.isInMatch() && pd.isDuelMode()) {
                    quickmatch = sender;
                    ml = pmi.getKitType();


                    break;
                }
            }

        }

        if (quickmatch == null) {

            /* Custom Matches */
            for (Map.Entry<MatchLoadout, UUID> customMatchQueue : matchSoloQueue.entrySet()) {
                if (customMatchQueue.getKey().isCustom() && customMatchQueue.getValue() != clicker.getUniqueId()) {
                    quickmatch = customMatchQueue.getValue();
                    ml = customMatchQueue.getKey();
                }
            }

            /* Standard Matches */
            if (quickmatch == null) {

                for (MatchLoadout loadout : getMatchLoadouts()) {
                    if (matchSoloQueue.containsKey(loadout) && matchSoloQueue.get(loadout) != clicker.getUniqueId()) {
                        quickmatch = matchSoloQueue.get(loadout);
                        ml = loadout;
                    }
                }
            }
        }

        if (quickmatch != null) {

            if (quickmatch == clicker.getUniqueId()) {
                quickmatch = null;
                AchillesPlugin.getInstance().getPlayerHandler().getSession(clicker.getUniqueId()).updateVisuals();
                clicker.updateInventory();

                clicker.sendMessage(Style.header("You have been §cremoved §efrom the §bquickmatch§e queue."));
                clicker.playSound(clicker.getLocation(), Sound.NOTE_STICKS, 20, 0.5F);

                return;
            } else {
                Player existing = Bukkit.getPlayer(quickmatch);

                // Local matchloadout is only null if no other queues or requests were able to be found
                // meaning that the found quickmatch is in fact another queued quickmatch

                if (ml == null) {
                    ml = getLoadoutFromClass(StandardLoadout.class);
                }

                if (existing != null) {
                    existing.spigot().sendMessage(Style.format("§eA %s match has been found against %s!", ml, clicker));
                    clicker.spigot().sendMessage(Style.format("§eA %s match has been found against %s!", ml, existing));

                    createMatch(existing.getUniqueId(), clicker.getUniqueId(), ml, false);

                    return;
                }
            }

        }

        quickmatch = clicker.getUniqueId();
        AchillesPlugin.getInstance().getPlayerHandler().getSession(quickmatch).updateVisuals();
        clicker.updateInventory();

        clicker.sendMessage(Style.header("You have been §aadded §eto the §bquickmatch§e queue."));
        clicker.playSound(clicker.getLocation(), Sound.NOTE_PLING, 20, 20);

    }

    public void queue(Player clicker, MatchLoadout mkt) {
        if (AchillesPlugin.getInstance().getMatchHandler().isInMatch(clicker)) {
            return;
        }

        UUID waiting = getMatchSoloQueue().get(mkt);

        if (waiting == null) {
            for (PlayerMatchInvite pmi : getAllPlayerInvites(clicker.getUniqueId())) {
                if (pmi.getKitType() == mkt ) {
                    waiting = pmi.getSender();
                    break;
                }
            }
        }

        if (waiting == null && quickmatch != null) {
            waiting = quickmatch;
        }

        if (waiting != null) {
            Player waiter = Bukkit.getPlayer(waiting);


            if (waiter != null) {
                waiter.spigot().sendMessage(Style.format("§eA %s match has been found against %s!", mkt, clicker));
                clicker.spigot().sendMessage(Style.format("§eA %s match has been found against %s!", mkt, waiter));

                createMatch(waiter.getUniqueId(), clicker.getUniqueId(), mkt, false);

                return;
            }

            getMatchSoloQueue().remove(mkt);

        }
        matchSoloQueue.entrySet().removeIf(e -> e.getValue() == clicker.getUniqueId());
        matchSoloQueue.put(mkt, clicker.getUniqueId());
        clicker.spigot().sendMessage(Style.format("§eYou have been added to the %s queue.", mkt));

        if (mkt.isCustom()) {
            clicker.spigot().sendMessage(Style.format("§eThe next §bquickmatch§e will be paired with you.", mkt));

        }


    }

    public boolean hasPlayerInvite(UUID sender, UUID target) {
        return (hasPlayerInvite(sender, target, null));
    }

    public boolean hasPlayerInvite(UUID sender, UUID target, MatchLoadout kitType) {
        return (getPlayerInvite(sender, target, kitType) != null);
    }

    public PlayerMatchInvite getPlayerInvite(UUID sender, UUID target) {
        return (getPlayerInvite(sender, target, null));
    }

    public PlayerMatchInvite[] getAllPlayerInvites(UUID target) {
        return playerMatchInvites.stream().filter(i -> i.getTarget() == target && i.isValid()).toArray(s -> new PlayerMatchInvite[s]);
    }

    public PlayerMatchInvite getPlayerInvite(UUID sender, UUID target, MatchLoadout kitType) {
        for (PlayerMatchInvite invite : playerMatchInvites) {
            if ((sender == null || invite.getSender().equals(sender)) && invite.getTarget().equals(target) && (kitType == null || invite.getKitType().equals(kitType)) && invite.isValid()) {
                return (invite);
            }
        }

        return (null);
    }


    public void declineInvitation(PlayerMatchInvite acceptedInvite) {
        Iterator<PlayerMatchInvite> inviteIterator = playerMatchInvites.iterator();

        while (inviteIterator.hasNext()) {
            PlayerMatchInvite invite = inviteIterator.next();

            if (invite.getSender().equals(acceptedInvite.getSender()) && invite.getTarget().equals(acceptedInvite.getTarget()) && invite.getKitType().equals(acceptedInvite.getKitType())) {
                inviteIterator.remove();

                Player inviter = Bukkit.getPlayer(invite.getSender());
                Player invited = Bukkit.getPlayer(invite.getTarget());

                if (inviter != null && invited != null) {
                    inviter.sendMessage(Style.header("§b" + invited.getDisplayName() + " §edeclined your match invitation."));
                }

            }
        }

    }

    public void registerInvitation(PlayerMatchInvite newInvite) {
        Iterator<PlayerMatchInvite> inviteIterator = playerMatchInvites.iterator();

        while (inviteIterator.hasNext()) {
            PlayerMatchInvite invite = inviteIterator.next();

            if (invite.getSender().equals(newInvite.getSender()) && invite.getTarget().equals(newInvite.getTarget()) && invite.getKitType().equals(newInvite.getKitType())) {
                inviteIterator.remove();
            }
        }

        inviteIterator = playerMatchInvites.iterator();

        while (inviteIterator.hasNext()) {
            PlayerMatchInvite invite = inviteIterator.next();

            if (invite.getSender().equals(newInvite.getTarget()) && invite.getTarget().equals(newInvite.getSender()) && invite.getKitType().equals(newInvite.getKitType())) {
                inviteIterator.remove();
                acceptInvitation(invite);
                return;
            }
        }

        Player senderPlayer = Bukkit.getPlayer(newInvite.getSender());
        Player targetPlayer = Bukkit.getPlayer(newInvite.getTarget());

        PlayerData targetData = AchillesPlugin.getInstance().getPlayerHandler().getSession(targetPlayer.getUniqueId());

        if (!targetData.isDuelMode()) {

            senderPlayer.sendMessage(Style.header("§cThat player is not in the arena!"));
            return;
        }

        if (newInvite.isRematch()) {
            senderPlayer.spigot().sendMessage(Style.format("§eYou have sent a §c§lrematch §erequest to %s.", targetPlayer));
            targetPlayer.spigot().sendMessage(Style.format("§b%s§e has requested a §c§lREMATCH§e!", senderPlayer));

        } else {
            senderPlayer.spigot().sendMessage(Style.format("§eYou have sent a %s duel request to %s!", newInvite.getKitType(), targetPlayer));
            targetPlayer.spigot().sendMessage(Style.format("§e%s has §cchallenged §eyou to a %s match!", senderPlayer, newInvite.getKitType()));
        }

        BMComponentBuilder bmc = new BMComponentBuilder(Style.HEADER);
        bmc.append("Type ").color(net.md_5.bungee.api.ChatColor.YELLOW)
                .append("/accept ").color(net.md_5.bungee.api.ChatColor.RED)
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/accept " + senderPlayer.getName()))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BMComponentBuilder("§dClick to accept match!").create()))
                .append("or ").color(net.md_5.bungee.api.ChatColor.YELLOW)
                .append("LEFT-CLICK ").color(net.md_5.bungee.api.ChatColor.GOLD)
                .append("§bwith your stick")
                .append(" to accept!").color(net.md_5.bungee.api.ChatColor.YELLOW);

        targetPlayer.spigot().sendMessage(bmc.create());

        playerMatchInvites.add(newInvite);
    }


    public void acceptInvitation(PlayerMatchInvite acceptedInvite) {
        Iterator<PlayerMatchInvite> inviteIterator = playerMatchInvites.iterator();

        while (inviteIterator.hasNext()) {
            PlayerMatchInvite invite = inviteIterator.next();

            if (invite.getSender().equals(acceptedInvite.getSender()) && invite.getTarget().equals(acceptedInvite.getTarget()) && invite.getKitType().equals(acceptedInvite.getKitType())) {
                inviteIterator.remove();
            }
        }

        Player senderPlayer = Bukkit.getPlayer(acceptedInvite.getSender());
        Player targetPlayer = Bukkit.getPlayer(acceptedInvite.getTarget());

        senderPlayer.spigot().sendMessage(Style.format("§e%s accepted your %s duel request!", targetPlayer, acceptedInvite.getKitType()));
        targetPlayer.spigot().sendMessage(Style.format("§eYou accepted %s's %s duel request!", senderPlayer, acceptedInvite.getKitType()));

        createMatch(
                senderPlayer.getUniqueId(),
                targetPlayer.getUniqueId(),
                acceptedInvite.getKitType(),
                false
        );
    }

    public void cleanup(UUID uuid) {
        matchSoloQueue.entrySet().removeIf(e -> e.getValue() == uuid);
        playerMatchInvites.removeIf(i -> i.getSender() == uuid);

        if (quickmatch == uuid) {
            quickmatch = null;
        }
    }

    public void removeFromCustomQueue(UUID uuid) {
        matchSoloQueue.entrySet().removeIf(e -> e.getKey().isCustom() && e.getValue() == uuid);
    }

    public void createMatch(UUID sender, UUID target, MatchLoadout kitType, boolean ranked) {
        cleanup(sender);
        cleanup(target);

        Match match = new Match(new ObjectId().toHexString(), sender, target, kitType, ranked);
        matches.add(match);

        match.setMatchAmount(kitType.getFirstTo());
        match.setup();
    }


    public void dispose(Match match) {
        matches.removeIf(m -> m.getPlayer1() == match.getPlayer1() && m.getPlayer2() == match.getPlayer2());
    }
}
