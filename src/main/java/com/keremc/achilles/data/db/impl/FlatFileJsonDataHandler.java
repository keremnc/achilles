package com.keremc.achilles.data.db.impl;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.data.PlayerData;
import com.keremc.achilles.data.db.DataHandler;
import com.keremc.achilles.kit.Kit;
import com.keremc.achilles.kit.stats.KitStatistics;
import com.keremc.achilles.kit.stats.MetaProperty;
import com.keremc.achilles.loadout.MatchLoadout;
import com.keremc.achilles.region.Region;
import com.keremc.achilles.statistics.Leaderboards;
import com.keremc.achilles.statistics.StatSlot;
import com.keremc.achilles.statistics.Stats;
import com.keremc.core.util.UUIDUtils;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import org.apache.commons.io.FileUtils;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FlatFileJsonDataHandler implements DataHandler {
    private Map<UUID, BasicDBObject> loaded = new HashMap<>();

    @Override
    public void loadRegions() {
        try {
            File rg = getRegionFile();

            String paylaod = FileUtils.readFileToString(rg);

            if (!paylaod.isEmpty()) {
                BasicDBObject data = BasicDBObject.parse(paylaod);

                BasicDBList list = (BasicDBList) data.get("regions");

                for (Object obj : list) {
                    Region reg = new Region((BasicDBObject) obj);
                    AchillesPlugin.getInstance().getRegionHandler().addRegion(reg);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void saveRegions() {
        try {
            File rg = getRegionFile();

            BasicDBObject regions = new BasicDBObject();
            BasicDBList list = new BasicDBList();

            for (Region reg : AchillesPlugin.getInstance().getRegionHandler().getRegions()) {
                list.add(reg.toJSON());
            }

            FileUtils.writeStringToFile(rg, regions.append("regions", list).toJson(new JsonWriterSettings(JsonMode.SHELL, true)));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public MatchLoadout[] getLoadouts(UUID uuid) {
        List<MatchLoadout> matchLoadouts = new ArrayList<>();

        BasicDBList loadouts = (BasicDBList) loaded.get(uuid).get(LOADOUTS);

        if (loadouts != null) {
            for (Object dbo : loadouts) {
                MatchLoadout ml = MatchLoadout.fromJSON((BasicDBObject) dbo);
                matchLoadouts.add(ml);

            }
        }


        return matchLoadouts.toArray(new MatchLoadout[matchLoadouts.size()]);
    }

    @Override
    public Map<String, KitStatistics> getKitStatistics(UUID uuid) {
        BasicDBObject kitStats = (BasicDBObject) loaded.get(uuid).get(KIT_STATISTICS);

        Map<String, KitStatistics> data = new HashMap<>();

        for (Kit kit : AchillesPlugin.getInstance().getKitHandler().getKits()) {
            KitStatistics ks = new KitStatistics();

            for (MetaProperty mp : kit.getMetaProperties()) {
                ks.getProperties().put(mp.getKey(), mp.getDef());
            }

            if (kitStats != null && kitStats.containsKey(kit.getName())) {
                ks.fromJSON((BasicDBObject) kitStats.get(kit.getName()));
            }
            data.put(kit.getName(), ks);
        }

        return data;
    }

    @Override
    public Map<String, Long> getKitRentals(UUID uuid) {
        Map<String, Long> rentals = new HashMap<>();

        BasicDBObject rentalJson = (BasicDBObject) loaded.get(uuid).get(RENTALS);

        if (rentalJson != null) {
            for (Map.Entry<String, Object> entry : rentalJson.entrySet()) {
                rentals.put(entry.getKey(), rentalJson.getLong(entry.getKey()));
            }
        }

        return rentals;
    }

    @Override
    public BasicDBObject getBasicStats(UUID uuid) {
        return (BasicDBObject) loaded.get(uuid).get(BASIC_STATS);
    }

    @Override
    public String[] getPlayerNames() {
        File dataFolder = new File("achilles_data" + File.separator + "players");
        dataFolder.mkdirs();

        String[] uuidArray = dataFolder.list();
        String[] names = new String[uuidArray.length];

        for (int i = 0; i < uuidArray.length; i++) {
            names[i] = UUIDUtils.name(UUID.fromString(uuidArray[i].split("\\.")[0]));
        }

        return names;
    }


    @Override
    public Map.Entry<UUID, Double>[] getLeaderboards(StatSlot stat) {
        Map<UUID, Double> leaderboards = new HashMap<>();

        Stream.of(getPlayerNames()).map(UUIDUtils::uuid)
                .map(u -> new AbstractMap.SimpleEntry<>(u, DataHandler.loadOfflineStats(u)))
                .filter(e -> e.getValue() != null)
                .forEach(e -> leaderboards.put(e.getKey(), e.getValue().get(stat)));


        Map<UUID, Double> sortedMap =
                leaderboards.entrySet().stream()
                        .sorted(Map.Entry.<UUID, Double>comparingByValue().reversed())
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                (e1, e2) -> e1, LinkedHashMap::new));


        return sortedMap.entrySet().toArray(new Map.Entry[]{});
    }

    @Override
    public int getPosition(UUID uuid, StatSlot slot) {
        int pos = 1;
        double stat = AchillesPlugin.getInstance().getPlayerHandler().getSession(uuid).getStats().get(slot);

        for (Map.Entry existing : Leaderboards.retrieve(slot)) {
            double val = (double) existing.getValue();

            if (stat >= val) {
                return pos;
            }
            pos++;
        }

        return pos;
    }

    @Override
    public void save(PlayerData playerData) {
        BasicDBObject data = new BasicDBObject();
        Stats stats = playerData.getStats();

        BasicDBObject kitStatistics = new BasicDBObject();
        BasicDBObject kitRentals = new BasicDBObject();
        BasicDBObject basicStats = new BasicDBObject();
        BasicDBList loadout = new BasicDBList();

        for (Map.Entry<String, KitStatistics> entry : stats.getKitStatistics().entrySet()) {
            kitStatistics.put(entry.getKey(), entry.getValue().toJSON());
        }

        for (Map.Entry<String, Long> entry : playerData.getKitRentals().entrySet()) {
            kitRentals.append(entry.getKey(), entry.getValue().longValue());
        }


        for (MatchLoadout ml : playerData.getCustomLoadouts()) {
            loadout.add(ml.serialize());
        }

        basicStats.put(SPAWN_PROTECTION, playerData.isSpawnProt());
        basicStats.put(BALANCE, playerData.getBalance());
        basicStats.put(LAST_KIT, playerData.getLastKit() == null ? null : playerData.getLastKit().getName());
        basicStats.put(CURRENT_KIT, playerData.getSelectedKit() == null ? null : playerData.getSelectedKit().getName());
        basicStats.put(CUSTOM_INV, playerData.isCustomInv());

        basicStats.put(PVP_STATS, stats.toJSON());

        data.put(KIT_STATISTICS, kitStatistics);
        data.put(RENTALS, kitRentals);
        data.put(BASIC_STATS, basicStats);
        data.put(LOADOUTS, loadout);

        try {
            FileUtils.writeStringToFile(getFile(playerData.getUuid()), data.toJson(new JsonWriterSettings(JsonMode.SHELL, true)));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private File getFile(UUID uuid) throws IOException {
        File dataFolder = new File("achilles_data" + File.separator + "players");
        dataFolder.mkdirs();

        File playerFile = new File(dataFolder + File.separator + uuid + ".bm");

        if (!playerFile.exists()) {
            playerFile.createNewFile();
        }
        return playerFile;
    }

    private File getRegionFile() throws IOException {
        File dataFolder = new File("achilles_data");
        dataFolder.mkdirs();

        File regionsFile = new File(dataFolder + File.separator + "regions.bm");

        if (!regionsFile.exists()) {
            regionsFile.createNewFile();
        }
        return regionsFile;
    }


    @Override
    public void init(UUID uuid) {
        try {
            File playerFile = getFile(uuid);
            String payload = FileUtils.readFileToString(playerFile);

            if (!payload.isEmpty()) {
                BasicDBObject json = BasicDBObject.parse(payload);
                loaded.put(uuid, json);
            } else {
                loaded.put(uuid, new BasicDBObject());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void cleanup(UUID uuid) {
        loaded.remove(uuid);

        PlayerData pd = AchillesPlugin.getInstance().getPlayerHandler().getSession(uuid);

        if (pd != null) {
            save(pd);
        }
    }

}
