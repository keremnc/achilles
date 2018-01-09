package com.keremc.achilles.statistics;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.data.db.DataHandler;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Leaderboards extends Thread {
    private static Map<StatSlot, Map.Entry<UUID, Double>[]> stats = new ConcurrentHashMap<>();

    public static void init() {
        new Leaderboards().start();
    }

    /**
     * Retrieves cached values of stats, does not do a lookup
     *
     * @param slot StatSlot to get values for
     * @return array of UUID, Integer pairs
     */
    public static Map.Entry<UUID, Double>[] retrieve(StatSlot slot) {
        return stats.get(slot);
    }

    public static int position(StatSlot slot, UUID uuid) {
        return AchillesPlugin.getInstance().getDataHandler().getPosition(uuid, slot);
    }

    @Override
    public void run() {
        while (true) {

            DataHandler dh = AchillesPlugin.getInstance().getDataHandler();
            for (StatSlot ss : StatSlot.values()) {
                stats.put(ss, dh.getLeaderboards(ss));
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
