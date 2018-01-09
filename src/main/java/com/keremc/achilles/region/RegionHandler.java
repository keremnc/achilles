package com.keremc.achilles.region;

import lombok.Getter;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class RegionHandler {
    @Getter private List<Region> regions = new ArrayList<>();

    public void addRegion(Region region) {
        regions.add(region);
    }

    public void deleteRegion(String name) {
        regions.removeIf(rg -> rg.getName().equalsIgnoreCase(name));
    }

    public Region getRegion(Location loc) {
        for (Region reg : regions) {
            if (reg.contains(loc)) {
                return reg;
            }
        }
        return null;
    }

    public Region getRegion(String name) {
        for (Region reg : regions) {
            if (reg.getName().equalsIgnoreCase(name)) {
                return reg;
            }
        }
        return null;
    }
}
