package com.keremc.achilles.data;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.chat.Style;
import com.keremc.achilles.data.db.DataHandler;
import com.keremc.achilles.region.Region;
import com.keremc.achilles.region.Tag;
import com.keremc.achilles.region.select.Selection;
import com.keremc.core.CorePlugin;
import com.mongodb.BasicDBObject;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerHandler {
    @Getter private Map<UUID, PlayerData> sessions = new HashMap<>();
    private Map<UUID, Map<String, Object>> payloads = new HashMap<>();

    public void loadData(UUID uuid, String alias) {
        BasicDBObject dbo = DataHandler.getFullObject(uuid);

        payloads.put(uuid, dbo);
    }

    public boolean handleLogin(Player joiner) {

        if (!payloads.containsKey(joiner.getUniqueId())) {
            return false;
        }

        PlayerData ps = new PlayerData(joiner.getUniqueId(), joiner.getName());
        DataHandler.setupPlayer(ps, payloads.remove(joiner.getUniqueId()));

        sessions.put(joiner.getUniqueId(), ps);
        return true;

    }

    public void handleJoin(Player joiner) {
        joiner.removeMetadata(Selection.SELECTION_METADATA_KEY, AchillesPlugin.getInstance());

        PlayerData ps = getSession(joiner.getUniqueId());

        if (Tag.DUEL_LOBBY.isTagged(joiner.getLocation())) {
            ps.setDuelMode(true);
            ps.respawn();
        } else if (ps.getSelectedKit() == null && !ps.isCustomInv()) {
            ps.addSpawnItems();
        }

        if (!joiner.hasPlayedBefore()) {
            joiner.sendMessage(Style.getJoinMessages());
            ps.respawn();
        }

        AchillesPlugin.getInstance().getDataHandler().cleanup(joiner.getUniqueId());

    }

    public PlayerData getSession(UUID uuid) {
        return sessions.get(uuid);
    }

    public Location chooseDuelSpawnLocation() {
        Region duelSpawn = Tag.DUEL_SPAWN.getRegions().get(0);

        double x = duelSpawn.getX1() + (duelSpawn.getX2() - duelSpawn.getX1()) * CorePlugin.RANDOM.nextDouble();
        double y = duelSpawn.getY1() + (duelSpawn.getY2() - duelSpawn.getY1()) * CorePlugin.RANDOM.nextDouble();
        double z = duelSpawn.getZ1() + (duelSpawn.getZ2() - duelSpawn.getZ1()) * CorePlugin.RANDOM.nextDouble();

        return new Location(duelSpawn.getWorld(), x, y, z);

    }

    public Location getSpawnLocation() {
        return Bukkit.getWorld("world").getSpawnLocation().subtract(0.5, -0.7, 0.5);
    }
}
