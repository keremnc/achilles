package com.keremc.achilles.kit.stats;

import com.mongodb.BasicDBObject;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class KitStatistics {

    private int uses;
    private int kills;
    private int deaths;

    private transient Map<String, Double> properties = new HashMap<>();
    // ! all properties should always be loaded
    // iterate through all kits and each property
    // foreach property, access the DB

    public BasicDBObject toJSON() {
        return new BasicDBObject("uses", uses).append("kills", kills).append("deaths", deaths).append("properties", new BasicDBObject(properties));
    }

    public void fromJSON(BasicDBObject db) {
        this.uses = db.getInt("uses");
        this.kills = db.getInt("kills");
        this.deaths = db.getInt("deaths");

        for (Map.Entry<String, Object> entry : ((BasicDBObject) db.get("properties")).entrySet()) {
            properties.put(entry.getKey(), (double) entry.getValue());
        }
    }

}
