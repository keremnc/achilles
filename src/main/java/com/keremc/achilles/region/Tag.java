package com.keremc.achilles.region;

import com.keremc.achilles.AchillesPlugin;
import org.bukkit.Location;

import java.util.List;
import java.util.stream.Collectors;

public enum Tag {
    SPAWN,

    ARENA,

    ARENA_SPAWNPOINTS,

    DUEL_SPAWN,

    DUEL_LOBBY;

    public List<Region> getRegions() {
        return AchillesPlugin.getInstance().getRegionHandler().getRegions().stream().filter(
                r -> r.getType() == this
        ).collect(Collectors.toList());
    }

    public boolean isTagged(Location location) {
        return getRegions().stream().anyMatch(d -> d.contains(location));
    }

}
