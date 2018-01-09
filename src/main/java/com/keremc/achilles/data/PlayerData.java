package com.keremc.achilles.data;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.chat.Style;
import com.keremc.achilles.kit.Kit;
import com.keremc.achilles.kit.gui.KitSelectMenu;
import com.keremc.achilles.kit.stats.KitStatistics;
import com.keremc.achilles.loadout.MatchLoadout;
import com.keremc.achilles.loadout.gui.menu.LoadoutSelectionMenu;
import com.keremc.achilles.match.gui.PendingMatchRequestsMenu;
import com.keremc.achilles.statistics.StatSlot;
import com.keremc.achilles.statistics.Stats;
import com.keremc.achilles.visual.Title;
import com.keremc.core.CorePlugin;
import com.keremc.core.item.ItemBuilder;
import com.keremc.core.util.PlayerUtils;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.inventivetalent.bossbar.BossBarAPI;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

@Data
@RequiredArgsConstructor
public class PlayerData {
    public static final double MIN_PERCENT_FOR_ASSIST = .25;

    @NonNull
    private UUID uuid;
    @NonNull
    private String currentName;

    private boolean spawnProt;

    private Stats stats;

    private Kit lastKit;
    private Kit selectedKit;

    private Map<String, Long> kitRentals = new HashMap<>();

    private double balance = 0;

    private long combatTaggedTil;
    private BukkitRunnable tpTask;

    private boolean build;

    private boolean duelMode;
    private boolean customInv;

    private Map<UUID, Double> damageReceived = new HashMap<>();
    private boolean lastTickHad;

    private String lastLoadout;

    private List<MatchLoadout> customLoadouts = new LinkedList<>();

    private long lastDyeUpdate = System.currentTimeMillis();

    public void setSpawnProt(boolean sp) {
        this.spawnProt = sp;

        markForSave();

    }

    public void markForSave() {
        SaveHandler.save(this);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public List<String> applyAssists(Player killer, double finalTokens) {
        List<String> assisters = new ArrayList<>();
        for (UUID uuid : damageReceived.keySet()) {
            Player damager = Bukkit.getPlayer(uuid);

            if (damager != null && damager != killer) {
                double pct = getPctDamaged(damager);
                if (pct >= MIN_PERCENT_FOR_ASSIST) {
                    double tokens = pct * finalTokens;

                    TextComponent tc = Style.assisted(getPlayer(), tokens, pct);
                    damager.spigot().sendMessage(tc);
                    assisters.add(damager.getName());
                }
            }
        }

        return assisters;
    }

    public void selectLoadout(MatchLoadout ml) {
        lastLoadout = (ml.isCustom() ? "custom_" : "default_") + ml.getName();
    }

    public MatchLoadout getLastLoadout() {
        if (lastLoadout != null) {
            if (lastLoadout.startsWith("custom_")) {
                for (MatchLoadout ml : customLoadouts) {
                    if (ml.getName().equalsIgnoreCase(lastLoadout.split("custom_")[1])) {
                        return ml;
                    }
                }
            } else {
                for (MatchLoadout ml : AchillesPlugin.getInstance().getMatchHandler().getMatchLoadouts()) {
                    if (ml.getName().equalsIgnoreCase(lastLoadout.split("default_")[1])) {
                        return ml;
                    }
                }
            }
        }
        return null;
    }

    public boolean hasCombatLogged() {
        return combatTaggedTil > System.currentTimeMillis() && !isSpawnProt();
    }

    public void damagedBy(Player damager, double damage) {
        UUID uuid;
        if (damager == null) {
            uuid = new UUID(420, 420);
        } else {
            uuid = damager.getUniqueId();
        }

        damageReceived.putIfAbsent(uuid, 0D);
        damageReceived.put(uuid, damageReceived.get(uuid) + damage);
        combatTaggedTil = System.currentTimeMillis() + (1000 * 60);
    }

    public double getPctDamaged(Player damager) {
        double total = damageReceived.values().stream().flatMapToDouble(DoubleStream::of).sum();
        double dealt = damageReceived.get(damager.getUniqueId());

        return total == 0 ? 0 : dealt / total;
    }

    public boolean isInMatch() {
        return AchillesPlugin.getInstance().getMatchHandler().isInMatch(getPlayer());
    }

    public void respawn() {
        combatTaggedTil = -1;

        setCustomInv(false);
        damageReceived.clear();

        if (duelMode) {
            getPlayer().teleport(AchillesPlugin.getInstance().getPlayerHandler().chooseDuelSpawnLocation());
            addArenaItems();
        } else {
            addSpawnItems();
            setSpawnProt(true);
            getPlayer().teleport(AchillesPlugin.getInstance().getPlayerHandler().getSpawnLocation());
        }
    }

    public double getWorth() {
        if (duelMode) {
            double base = (CorePlugin.RANDOM.nextDouble() * 10D) + 35;
            double kdr = getStats().get(StatSlot.KILLS) / (getStats().get(StatSlot.DEATHS) == 0 ? 1 : getStats().get(StatSlot.DEATHS));
            double kills = getStats().get(StatSlot.KILLS);

            double mult = .75 + (kdr * Math.min(1, kills / 50));

            return base * mult;

        } else {

            double base = (CorePlugin.RANDOM.nextDouble() * 10D) + 30;

            base += selectedKit != null && !selectedKit.isFree() ? 15 : 0;
            base += getStats().get(StatSlot.CURRENT_STREAK);

            return base;
        }
    }

    public boolean killed(Player dead) {
        boolean ksEnded = false;

        stats.inc(StatSlot.KILLS);
        stats.inc(StatSlot.CURRENT_STREAK);

        PlayerData deadPlayer = AchillesPlugin.getInstance().getPlayerHandler().getSession(dead.getUniqueId());
        double worth = deadPlayer.getWorth();

        setBalance(getBalance() + worth);
        TextComponent killerMsg = Style.killed(dead, worth);

        Bukkit.getScheduler().runTaskLater(AchillesPlugin.getInstance(), () -> getPlayer().spigot().sendMessage(killerMsg), 1L);

        Title title = new Title(null, "§6+§b" + Style.DOUBLE_FORMAT.format(worth) + " §etokens");

        title.fast().send(getPlayer());

        if (deadPlayer.getStats().get(StatSlot.CURRENT_STREAK) >= 10) {
            TextComponent endedKs = Style.endedKillstreak(getPlayer(), dead, (int) deadPlayer.getStats().get(StatSlot.CURRENT_STREAK));

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.spigot().sendMessage(endedKs);
            }

            ksEnded = true;

        }

        if (stats.get(StatSlot.CURRENT_STREAK) > stats.get(StatSlot.MAX_STREAK)) {
            stats.set(StatSlot.MAX_STREAK, stats.get(StatSlot.CURRENT_STREAK));
        }

        if (getSelectedKit() != null) {
            KitStatistics ks = stats.getKitStats(getSelectedKit());
            ks.setKills(ks.getKills() + 1);
        }

        if ((stats.get(StatSlot.CURRENT_STREAK) > 15 && stats.get(StatSlot.CURRENT_STREAK) % 10 == 0) || (stats.get(StatSlot.CURRENT_STREAK) == 5 && stats.get(StatSlot.CURRENT_STREAK) <= 15)) {
            dead.getWorld().playSound(dead.getLocation(), Sound.WITHER_SPAWN, 20, 0.1F);

            TextComponent ksMsg = Style.killstreak(getPlayer(), (int) stats.get(StatSlot.CURRENT_STREAK));
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.spigot().sendMessage(ksMsg);
            }

        }
        markForSave();

        return ksEnded;

    }

    public Kit[] getOwned() {
        return AchillesPlugin.getInstance().getKitHandler().getKits().stream().filter(
                this::ownsKit
        ).collect(Collectors.toList()).toArray(new Kit[]{});
    }

    public boolean ownsKit(Kit kit) {

        if (kitRentals.containsKey(kit.getName()) && kitRentals.get(kit.getName()) < System.currentTimeMillis()) {
            kitRentals.remove(kit.getName());
        }

        return kit.isFree() ||
                getPlayer().hasPermission("achilles." + kit.getName().toLowerCase()) ||
                (kitRentals.containsKey(kit.getName()) && kitRentals.get(kit.getName()) > System.currentTimeMillis());
    }

    public void rentKit(Kit kit) {
        balance -= kit.getRentalPrice();

        kitRentals.put(kit.getName(), System.currentTimeMillis() + (24 * 3600 * 1000));
        markForSave();

    }

    public Date getKitExpires(Kit kit) {
        if (ownsKit(kit)) {
            if (!kitRentals.containsKey(kit.getName())) {
                return Date.from(Instant.MAX);
            } else {
                return new Date(kitRentals.get(kit.getName()));
            }
        }
        return new Date(-1);
    }

    public void addArenaItems() {
        Player player = getPlayer();
        ItemBuilder ib = new ItemBuilder();

        PlayerUtils.resetInventory(player, GameMode.SURVIVAL);

        ib.withMaterial(Material.BLAZE_ROD).withDisplayName("§eQuickmatch Stick")
                .action(a -> a.name().contains("LEFT"),
                        (p) -> AchillesPlugin.getInstance().getMatchHandler().acceptAny(p));

        player.getInventory().setItem(0, ib.create());


        ib.reset();

        ib.withMaterial(Material.BONE).withDisplayName("§eCustom Match Stick")
                .action(a -> a.name().contains("LEFT"),
                        (p) -> AchillesPlugin.getInstance().getMatchHandler().acceptAny(p));

        player.getInventory().setItem(1, ib.create());

        ib.reset();

        ib.withMaterial(Material.SKULL_ITEM).withData((byte) 3).withDisplayName("§ePending Matches")
                .interact(p -> AchillesPlugin.getInstance().getPlayerHandler().getSession(p.getUniqueId()).openPendingMatchRequests());

        player.getInventory().setItem(4, ib.create());


        ib.reset();

        ib.withMaterial(Material.CHEST).withDisplayName("§eLoadouts/Queues")
                .interact(p -> new LoadoutSelectionMenu(null, true).openMenu(p));

        player.getInventory().setItem(6, ib.create());

        ib.reset();

        ib.withMaterial(Material.INK_SACK).withData((byte) 8).withDisplayName("§eQuickmatch Queue")
                .interact(p -> AchillesPlugin.getInstance().getMatchHandler().quickMatchQueue(player));

        player.getInventory().setItem(8, ib.create());

    }

    public void openPendingMatchRequests() {
        new PendingMatchRequestsMenu().openMenu(getPlayer());
    }

    public void updateVisuals() {

        if (duelMode && !isInMatch())  {
            int amt = AchillesPlugin.getInstance().getMatchHandler().getMatchesAvailable(getPlayer());

            BossBarAPI.setMessage(getPlayer(),

                    "§dThere " + (amt == 1 ? "is" : "are") + " §e§l" + amt + "§d available match" + (amt == 1 ? "" : "es") + "!"
            );
        } else {
            BossBarAPI.removeBar(getPlayer());
        }

        Player player = getPlayer();
        int firstInkSack = player.getInventory().first(Material.INK_SACK);

        if (firstInkSack == -1) {
            return;
        }

        if (AchillesPlugin.getInstance().getMatchHandler().getQuickmatch() == player.getUniqueId()) {
            ItemStack it = player.getInventory().getItem(firstInkSack);

            it.setDurability((byte) 10);
            ItemMeta meta = it.getItemMeta();

            String suffix = "";

            if (meta.getDisplayName().contains("Searching")) {
                suffix = (meta.getDisplayName() + " ").split("match")[1].trim() + ".";
            }
            if (suffix.length() == 5) {
                suffix = "";
            }

            meta.setDisplayName("§eSearching for match" + suffix);
            it.setItemMeta(meta);

            player.updateInventory();


        } else {
            ItemStack it = player.getInventory().getItem(firstInkSack);

            if (AchillesPlugin.getInstance().getMatchHandler().getAllPlayerInvites(player.getUniqueId()).length > 0) {
                it.setDurability((short) 5);
            } else {
                it.setDurability((byte) 8);
            }
            ItemMeta meta = it.getItemMeta();
            meta.setDisplayName("§eQuickmatch Queue");
            it.setItemMeta(meta);
        }
    }

    public void tick() {
        ItemBuilder ib = new ItemBuilder();
        Player player = getPlayer();


        if (System.currentTimeMillis() - lastDyeUpdate > 400) {
            updateVisuals();
            lastDyeUpdate = System.currentTimeMillis();

        }

        Inventory inv = player.getOpenInventory().getTopInventory();

        if (inv instanceof CraftingInventory) {

            if (!duelMode && spawnProt && selectedKit == null) {
                ItemStack shop = ib
                        .reset()
                        .withMaterial(Material.CHEST)
                        .withDisplayName("§b§lShop")
                        .interact(p -> p.chat("/shop"))
                        .clicked(p -> p.chat("/shop"))
                        .create();


                player.getOpenInventory().getTopInventory().setItem(0, shop);
                player.updateInventory();
                lastTickHad = true;

            } else {
                player.getOpenInventory().getTopInventory().setItem(0, null);

                if (lastTickHad) {
                    player.updateInventory();
                }

                lastTickHad = false;
            }
        }
    }


    public void addSpawnItems() {
        Player player = getPlayer();
        ItemBuilder ib = new ItemBuilder();
        setDuelMode(false);

        tick();
        PlayerUtils.resetInventory(player, GameMode.SURVIVAL);

        ItemStack book = ib
                .withMaterial(Material.BOOK)
                .withDisplayName("§eKits")
                .interact(p -> new KitSelectMenu(null).openMenu(p))
                .create();

        player.getInventory().setItem(0, book);

        if (getLastKit() != null) {
            Kit last = getLastKit();

            ItemStack lastKit = ib
                    .reset()
                    .withMaterial(Material.WATCH)
                    .withDisplayName("§eSelect Last Kit: §b" + getLastKit().getName())
                    .interact(p -> last.applyKit(p))
                    .create();

            player.getInventory().setItem(8, lastKit);
        }
    }

    public boolean canWarp() {
        int max = 32;
        Player player = getPlayer();

        List<Entity> nearbyEntities = player.getNearbyEntities(max, max, max);

        if (player.getGameMode() == GameMode.CREATIVE) {
            return true;
        }

        for (Entity entity : nearbyEntities) {
            if ((entity instanceof Player)) {
                Player other = (Player) entity;
                if (!other.canSee(player)) {
                    return true;
                }
                if (!player.canSee(other)) {
                    continue;
                }

                PlayerData pd = AchillesPlugin.getInstance().getPlayerHandler().getSession(other.getUniqueId());

                if (pd.isSpawnProt()) {
                    continue;
                }

            }
        }

        return true;
    }

    public void warp(Location loc, int seconds, Runnable... onTp) {

        if (canWarp()) {
            getPlayer().teleport(loc);
            for (Runnable rb : onTp) {
                rb.run();
            }
            combatTaggedTil = -1;
            return;
        }

        getPlayer().sendMessage(ChatColor.GRAY + "Warping in 10 seconds... Stay still.");

        if (tpTask != null) {
            tpTask.cancel();
        }

        tpTask = new BukkitRunnable() {
            @Override
            public void run() {
                Player player = getPlayer();

                if (player != null && player.isOnline()) {
                    player.teleport(loc);
                    combatTaggedTil = -1;


                    if (onTp != null && onTp.length > 0) {
                        for (Runnable rb : onTp) {
                            rb.run();
                        }
                    }
                }
                cancel();
                tpTask = null;

            }
        };

        tpTask.runTaskLater(AchillesPlugin.getInstance(), seconds * 20L);
    }

    public boolean isWarping() {
        return tpTask != null;
    }

    public void cancelWarp() {
        getPlayer().sendMessage(ChatColor.GRAY + "Warp cancelled!");
        tpTask.cancel();
        tpTask = null;
    }
}
