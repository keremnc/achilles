package com.keremc.achilles.data.db;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.data.PlayerData;
import com.keremc.achilles.kit.stats.KitStatistics;
import com.keremc.achilles.loadout.MatchLoadout;
import com.keremc.achilles.statistics.CachedStats;
import com.keremc.achilles.statistics.StatSlot;
import com.keremc.achilles.statistics.Stats;
import com.mongodb.BasicDBObject;
import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public interface DataHandler {
    ConcurrentHashMap<UUID, CachedStats> cachedStats = new ConcurrentHashMap<>();
    ConcurrentSkipListSet<String> cachedNames = new ConcurrentSkipListSet<>();

    String KIT_STATISTICS = "kit_statistics";
    String RENTALS = "kit_rentals";
    String BASIC_STATS = "basic_stats";
    String LOADOUTS = "loadouts";

    /* ------- All part of basic_stats --------- */
    String BALANCE = "balance"; // double
    String LAST_KIT = "last_kit"; // str
    String CURRENT_KIT = "current_kit"; // str
    String SPAWN_PROTECTION = "spawn_prot"; // bool
    String CUSTOM_INV = "custom_inventory"; // bool
    String PVP_STATS = "pvp_stats"; // not in table itself, json is stored locally in payload

    /**
     * loads regions
     */
    void loadRegions();

    /**
     * Saves regions
     */
    void saveRegions();

    /**
     * @param uuid UUID to lookup
     * @return Map with case-sensitive kit names mapped to a player's stats under that kit
     */
    Map<String, KitStatistics> getKitStatistics(UUID uuid);

    /**
     * @param uuid UUID to look up
     * @return Map with case-sensitive kit names mapped to the milliseconds after epoch for when they expire
     */
    Map<String, Long> getKitRentals(UUID uuid);

    /**
     * @param uuid UUID to look up
     * @return key-value map containing all basic information values
     * as well as actual PVP stats
     */
    BasicDBObject getBasicStats(UUID uuid);

    /**
     * @param uuid UUID to lookup
     * @return array of matchloadout objects
     */
    MatchLoadout[] getLoadouts(UUID uuid);

    /**
     * @param stat the stat to query
     * @return players, ranked highest to lowest
     */
    Map.Entry[] getLeaderboards(StatSlot stat);

    /**
     * @param uuid UUID to lookup
     * @param stat stat to query
     * @return number of players + 1 with higer scores
     */
    int getPosition(UUID uuid, StatSlot stat);

    /**
     * @return array of all player names to join the server
     */
    String[] getPlayerNames();

    /**
     * Method called when the DataHandler prepares to load data for a player
     *
     * @param uuid UUID to look up
     */
    void init(UUID uuid);

    /**
     * Saves a player.
     *
     * @param playerData player to save
     */
    void save(PlayerData playerData);

    /**
     * Called after init() and loading are complete
     *
     * @param uuid UUID to cleanup
     */
    void cleanup(UUID uuid);

    /**
     * @param uuid UUID to look up
     * @return composite of all of the different database lookups
     */
    static BasicDBObject getFullObject(UUID uuid) {
        DataHandler dh = AchillesPlugin.getInstance().getDataHandler();
        dh.init(uuid);

        BasicDBObject data = new BasicDBObject();

        data.put(KIT_STATISTICS, dh.getKitStatistics(uuid));
        data.put(RENTALS, dh.getKitRentals(uuid));
        data.put(BASIC_STATS, dh.getBasicStats(uuid));
        data.put(LOADOUTS, dh.getLoadouts(uuid));

        return data;
    }


    static CachedStats loadOfflineStats(UUID uuid) {

        if (cachedStats.contains(uuid)) {
            CachedStats existing = cachedStats.get(uuid);

            if (existing.getTimeToLive() < System.currentTimeMillis()) {
                cachedStats.remove(uuid);
            } else {
                return existing;
            }

        }
        CachedStats stats = new CachedStats(System.currentTimeMillis() + (60 * 1000));

        AchillesPlugin.getInstance().getDataHandler().init(uuid);
        BasicDBObject basicStats = AchillesPlugin.getInstance().getDataHandler().getBasicStats(uuid);
        AchillesPlugin.getInstance().getDataHandler().cleanup(uuid);

        if (basicStats != null) {
            stats.fromJSON((BasicDBObject) basicStats.get(PVP_STATS));
        } else {
            return null;
        }

        if (cachedStats.size() >= 250) {
            UUID oldest = cachedStats.entrySet().stream().sorted((e1, e2) ->
                    Long.valueOf(e1.getValue().getTimeToLive()).compareTo(e2.getValue().getTimeToLive())
            ).findFirst().get().getKey();

            cachedStats.remove(oldest);
        }

        cachedStats.put(uuid, stats);

        return stats;

    }

    static void setupPlayer(PlayerData playerData, Map<String, Object> payload) {

        cachedStats.remove(playerData.getUuid());

        BasicDBObject basicStats = (BasicDBObject) payload.get(BASIC_STATS);
        Map<String, Long> kitRentals = (Map<String, Long>) payload.get(RENTALS);
        Map<String, KitStatistics> kitStats = (Map<String, KitStatistics>) payload.get(KIT_STATISTICS);
        MatchLoadout[] loadouts = (MatchLoadout[]) payload.get(LOADOUTS);

        Stats stats = new Stats();

        if (basicStats != null) {
            double balance = basicStats.getDouble(BALANCE, 0);
            String lastKit = basicStats.getString(LAST_KIT);
            String currKit = basicStats.getString(CURRENT_KIT);
            boolean spawnProt = basicStats.getBoolean(SPAWN_PROTECTION);
            boolean customInv = basicStats.getBoolean(CUSTOM_INV);

            playerData.setCustomInv(customInv);
            playerData.setSpawnProt(spawnProt);
            playerData.setBalance(balance);
            playerData.setLastKit(AchillesPlugin.getInstance().getKitHandler().getKitByName(lastKit));
            playerData.setSelectedKit(AchillesPlugin.getInstance().getKitHandler().getKitByName(currKit));

            stats.fromJSON((BasicDBObject) basicStats.get(PVP_STATS));

        }

        if (loadouts != null) {
            Arrays.asList(loadouts).forEach(playerData.getCustomLoadouts()::add);
        }

        if (kitStats != null) {
            stats.setKitStatistics(kitStats);
        }

        playerData.setStats(stats);

        if (kitRentals != null) {
            playerData.setKitRentals(kitRentals);
        }

    }

    static void init() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(AchillesPlugin.getInstance(), () -> {
            cachedNames.clear();
            cachedNames.addAll(Arrays.asList(AchillesPlugin.getInstance().getDataHandler().getPlayerNames()));
        }, 0L, 60 * 20L);
    }
}
