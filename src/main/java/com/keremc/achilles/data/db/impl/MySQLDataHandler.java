package com.keremc.achilles.data.db.impl;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.data.PlayerData;
import com.keremc.achilles.data.db.DataHandler;
import com.keremc.achilles.kit.stats.KitStatistics;
import com.keremc.achilles.loadout.MatchLoadout;
import com.keremc.achilles.loadout.builder.CustomLoadoutData;
import com.keremc.achilles.statistics.StatSlot;
import com.keremc.achilles.statistics.Stats;
import com.keremc.core.util.UUIDUtils;
import com.mongodb.BasicDBObject;
import org.jooq.lambda.SQL;
import org.jooq.lambda.Unchecked;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MySQLDataHandler implements DataHandler {

    private static FlatFileJsonDataHandler ffjdh = new FlatFileJsonDataHandler();

    public static final String CREATE_BASIC_STATS_TABLE = "CREATE TABLE IF NOT EXISTS " + BASIC_STATS
            + "(player_id VARCHAR(255), "
            + BALANCE + " DECIMAL(13, 4), "
            + LAST_KIT + " VARCHAR(255), "
            + CURRENT_KIT + " VARCHAR(255), "
            + SPAWN_PROTECTION + " BOOLEAN, "
            + CUSTOM_INV + " BOOLEAN, "
            + "PRIMARY KEY (player_id))";
    public static final String SELECT_BASIC_STATS = "SELECT * FROM " + BASIC_STATS + " WHERE player_id = ?";
    public static final String INSERT_BASIC_STATS = "REPLACE INTO " + BASIC_STATS
            + "(player_id, "
            + BALANCE + "," + LAST_KIT + "," + CURRENT_KIT + "," + SPAWN_PROTECTION + "," + CUSTOM_INV + ")"
            + " VALUES  (?,?,?,?,?,?)";

    public static final String CREATE_PVP_STATS_TABLE;
    public static final String SELECT_PVP_STATS = "SELECT * FROM " + PVP_STATS + " WHERE player_id = ?";
    public static final String INSERT_PVP_STATS;

    public static final String LEADERBOARDS_TOP_10 = "SELECT * FROM " + PVP_STATS + " ORDER BY %1$s DESC LIMIT 10";
    public static final String LEADERBOARDS_AHEAD = "SELECT COUNT(*) from "
            + PVP_STATS + " where %1$s > (SELECT %1$s FROM " + PVP_STATS + " WHERE player_id = ?) GROUP BY %1$s";


    public static final String CREATE_KIT_RENTALS_TABLE = "CREATE TABLE IF NOT EXISTS " + RENTALS
            + "(player_id VARCHAR(255), kit_id TINYINT(1) UNSIGNED, expiration BIGINT(1) UNSIGNED, "
            + "UNIQUE INDEX rental (player_id, kit_id))";
    public static final String SELECT_KIT_RENTALS = "SELECT * FROM " + RENTALS + " WHERE player_id = ?";
    public static final String INSERT_KIT_RENTALS = "REPLACE INTO " + RENTALS
            + "(player_id, kit_id, expiration) VALUES (?,?,?)";

    public static final String CREATE_KIT_PROPERTY_TABLE = "CREATE TABLE IF NOT EXISTS " + KIT_STATISTICS
            + "(player_id VARCHAR(255), kit_id TINYINT(1) UNSIGNED, property VARCHAR(255), value DECIMAL(13, 4), "
            + "UNIQUE INDEX stat (player_id, kit_id, property))";
    public static final String SELECT_KIT_PROPERTY = "SELECT * FROM " + KIT_STATISTICS + " WHERE player_id = ?";
    public static final String INSERT_KIT_PROPERTY = "REPLACE INTO " + KIT_STATISTICS
            + "(player_id, kit_id, property, value) VALUES (?,?,?,?)";

    public static final String CREATE_CUSTOM_LOADOUTS_TABLE = "CREATE TABLE IF NOT EXISTS " + LOADOUTS
            + "(player_id VARCHAR(255), kit_name VARCHAR(255), timestamp BIGINT(1) UNSIGNED, metadata VARCHAR(255),"
            + "UNIQUE INDEX loadout (player_id, kit_name))";
    public static final String SELECT_CUSTOM_LOADOUTS = "SELECT * FROM " + LOADOUTS + " WHERE player_id = ?";
    public static final String INSERT_CUSTOM_LOADOUTS = "REPLACE INTO " + LOADOUTS
            + "(player_id, kit_name, timestamp, metadata) VALUES (?,?,?,?)";

    static final String DB_URL = "jdbc:mysql://localhost/bm_kits";
    static final String USER = "root";
    static final String PASS = "";

    private static Connection sqlConnection;

    static {

        CREATE_PVP_STATS_TABLE = "CREATE TABLE IF NOT EXISTS " + PVP_STATS
                + "(player_id VARCHAR(255), "
                + Stream.of(StatSlot.values()).map(ss -> ss.getDbKey() + " DECIMAL(13,4)").collect(Collectors.joining(", "))
                + ", PRIMARY KEY (player_id))";

        INSERT_PVP_STATS = "REPLACE INTO " + PVP_STATS
                + "(player_id, "
                + Stream.of(StatSlot.values()).map(StatSlot::getDbKey).collect(Collectors.joining(", "))
                + ") VALUES ("
                + IntStream.range(0, StatSlot.values().length + 1).mapToObj(i -> "?").collect(Collectors.joining(",")) + ")";

    }

    public MySQLDataHandler() {

        try {
            Class.forName("com.mysql.jdbc.Driver");

            System.out.println("Connecting to database...");
            ensureConnection();
            System.out.println("Database connection established!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Create PVP_STATS table
        try {
            sqlConnection.createStatement().executeUpdate(CREATE_CUSTOM_LOADOUTS_TABLE);
            sqlConnection.createStatement().executeUpdate(CREATE_PVP_STATS_TABLE);
            sqlConnection.createStatement().executeUpdate(CREATE_BASIC_STATS_TABLE);
            sqlConnection.createStatement().executeUpdate(CREATE_KIT_RENTALS_TABLE);
            sqlConnection.createStatement().executeUpdate(CREATE_KIT_PROPERTY_TABLE);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void ensureConnection() {
        try {

            if (sqlConnection == null || sqlConnection.isClosed()) {
                sqlConnection = DriverManager.getConnection(DB_URL, USER, PASS);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void loadRegions() {
        ffjdh.loadRegions();
    }

    @Override
    public void saveRegions() {
        ffjdh.saveRegions();
    }

    @Override
    public Map<String, KitStatistics> getKitStatistics(UUID uuid) {
        ensureConnection();
        Map<String, KitStatistics> stats = new HashMap<>();

        try {
            PreparedStatement rentalStatement = sqlConnection.prepareStatement(SELECT_KIT_PROPERTY);
            rentalStatement.setString(1, uuid.toString());
            ResultSet rs = rentalStatement.executeQuery();

            while (rs.next()) {
                String kitName = AchillesPlugin.getInstance().getKitHandler().getById(rs.getInt("kit_id")).getName();
                String property = rs.getString("property");
                double val = rs.getBigDecimal("value").doubleValue();

                stats.putIfAbsent(kitName, new KitStatistics());

                KitStatistics existing = stats.get(kitName);

                for (Field field : KitStatistics.class.getDeclaredFields()) {
                    field.setAccessible(true);

                    if (field.getName().equalsIgnoreCase(property)) {
                        if (field.getType().isAssignableFrom(int.class)) {
                            field.setInt(existing, (int) val);
                        } else {
                            field.set(existing, val);
                        }
                        break;
                    }
                }

                existing.getProperties().put(property, val);


            }

        } catch (SQLException | IllegalAccessException ex) {
            ex.printStackTrace();
        }

        return stats;
    }

    @Override
    public Map<String, Long> getKitRentals(UUID uuid) {
        ensureConnection();
        Map<String, Long> rentals = new HashMap<>();

        try {
            PreparedStatement rentalStatement = sqlConnection.prepareStatement(SELECT_KIT_RENTALS);
            rentalStatement.setString(1, uuid.toString());
            ResultSet rs = rentalStatement.executeQuery();

            while (rs.next()) {
                String kitName = AchillesPlugin.getInstance().getKitHandler().getById(rs.getInt("kit_id")).getName();
                long expiration = rs.getBigDecimal("expiration").longValue();

                rentals.put(kitName, expiration);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return rentals;
    }

    @Override
    public BasicDBObject getBasicStats(UUID uuid) {
        ensureConnection();
        BasicDBObject dbo = new BasicDBObject();

        try {

            PreparedStatement pvpStats = sqlConnection.prepareStatement(SELECT_PVP_STATS);
            PreparedStatement basicStats = sqlConnection.prepareStatement(SELECT_BASIC_STATS);

            pvpStats.setString(1, uuid.toString());
            basicStats.setString(1, uuid.toString());

            ResultSet pvpResult = pvpStats.executeQuery();
            ResultSet basicResult = basicStats.executeQuery();

            Stats stats = new Stats();

            while (pvpResult.next()) {
                for (StatSlot ss : StatSlot.values()) {
                    stats.set(ss, pvpResult.getDouble(ss.getDbKey()));
                }
            }

            while (basicResult.next()) {
                dbo.put(SPAWN_PROTECTION, basicResult.getBoolean(SPAWN_PROTECTION));
                dbo.put(BALANCE, basicResult.getDouble(BALANCE));
                dbo.put(LAST_KIT, basicResult.getString(LAST_KIT));
                dbo.put(CURRENT_KIT, basicResult.getString(CURRENT_KIT));
                dbo.put(CUSTOM_INV, basicResult.getBoolean(CUSTOM_INV));
                dbo.put(PVP_STATS, stats.toJSON());
            }

            return dbo;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public MatchLoadout[] getLoadouts(UUID uuid) {
        ensureConnection();
        List<MatchLoadout> loadouts = new ArrayList<>();


        try {

            PreparedStatement loadoutsStatement = sqlConnection.prepareStatement(SELECT_CUSTOM_LOADOUTS);

            loadoutsStatement.setString(1, uuid.toString());

            ResultSet rs = loadoutsStatement.executeQuery();

            while (rs.next()) {
                String kitName = rs.getString("kit_name");
                long timestamp = rs.getBigDecimal("timestamp").longValue();
                String meta = rs.getString("metadata");


                MatchLoadout ml = CustomLoadoutData.from(kitName, timestamp,
                        Stream.of(meta.split(",")).collect(Collectors.toMap(s -> s.split("@")[0], s -> s.split("@")[1]))).create();

                loadouts.add(ml);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return loadouts.toArray(new MatchLoadout[loadouts.size()]);
    }

    @Override
    public Map.Entry[] getLeaderboards(StatSlot stat) {
        Map<UUID, Double> leaderboards = new HashMap<>();

        ensureConnection();
        try {
            PreparedStatement q = sqlConnection.prepareStatement(String.format(LEADERBOARDS_TOP_10, stat.getDbKey()));

            ResultSet rs = q.executeQuery();

            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("player_id"));
                double val = rs.getDouble(stat.getDbKey());

                leaderboards.put(uuid, val);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return leaderboards.entrySet().toArray(new Map.Entry[]{});
    }

    @Override
    public int getPosition(UUID uuid, StatSlot stat) {
        ensureConnection();
        try {
            PreparedStatement q = sqlConnection.prepareStatement(String.format(LEADERBOARDS_AHEAD, stat.getDbKey()));
            q.setString(1, uuid.toString());

            ResultSet rs = q.executeQuery();

            if (rs.next()) {
                return 1 + rs.getInt(1);
            } else {
                return 1;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;
    }


    @Override
    public String[] getPlayerNames() {
        ensureConnection();

        try {
            Statement st = sqlConnection.createStatement();
            ResultSet uuids = st.executeQuery("select player_id from " + BASIC_STATS);

            return SQL.seq(uuids, Unchecked.function(rs -> rs.getString("player_id")))
                    .map(u -> UUIDUtils.name(UUID.fromString(u))).toArray(String[]::new);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return new String[0];
    }

    @Override
    public void init(UUID uuid) {
    }

    @Override
    public void save(PlayerData playerData) {
        try {
            PreparedStatement saveStats = sqlConnection.prepareStatement(INSERT_PVP_STATS);
            PreparedStatement saveBasic = sqlConnection.prepareStatement(INSERT_BASIC_STATS);
            PreparedStatement saveRentals = sqlConnection.prepareStatement(INSERT_KIT_RENTALS);
            PreparedStatement saveKitStats = sqlConnection.prepareStatement(INSERT_KIT_PROPERTY);
            PreparedStatement saveLoadouts = sqlConnection.prepareStatement(INSERT_CUSTOM_LOADOUTS);

            saveStats.setString(1, playerData.getUuid().toString());
            saveBasic.setString(1, playerData.getUuid().toString());
            saveRentals.setString(1, playerData.getUuid().toString());
            saveKitStats.setString(1, playerData.getUuid().toString());
            saveLoadouts.setString(1, playerData.getUuid().toString());


            for (MatchLoadout ml : playerData.getCustomLoadouts()) {
                saveLoadouts.setString(2, ml.getName());
                saveLoadouts.setBigDecimal(3, new BigDecimal(ml.getCreated()));
                saveLoadouts.setString(4, ml.meta());
                saveLoadouts.addBatch();
            }


            for (Map.Entry<String, KitStatistics> kitStatisticsEntry : playerData.getStats().getKitStatistics().entrySet()) {
                String kitName = kitStatisticsEntry.getKey();
                KitStatistics stats = kitStatisticsEntry.getValue();

                saveKitStats.setInt(2, AchillesPlugin.getInstance().getKitHandler().getKitByName(kitName).getId());

                for (Field field : KitStatistics.class.getDeclaredFields()) {
                    field.setAccessible(true);
                    if (!Modifier.isTransient(field.getModifiers())) {
                        String fName = field.getName();
                        double val = field.getInt(stats);
                        saveKitStats.setString(3, fName);

                        saveKitStats.setBigDecimal(4, new BigDecimal(val));
                        saveKitStats.addBatch();
                    }
                }

                for (Map.Entry<String, Double> custom : stats.getProperties().entrySet()) {
                    String prop = custom.getKey();
                    double val = custom.getValue();

                    saveKitStats.setString(3, prop);
                    saveKitStats.setBigDecimal(4, new BigDecimal(val));
                    saveKitStats.addBatch();
                }

            }

            int ind = 2;
            for (StatSlot ss : StatSlot.values()) {
                saveStats.setDouble(ind++, playerData.getStats().get(ss));
            }

            saveBasic.setDouble(2, playerData.getBalance());
            saveBasic.setString(3, playerData.getLastKit() == null ? null : playerData.getLastKit().getName());
            saveBasic.setString(4, playerData.getSelectedKit() == null ? null : playerData.getSelectedKit().getName());
            saveBasic.setBoolean(5, playerData.isSpawnProt());
            saveBasic.setBoolean(6, playerData.isCustomInv());

            for (Map.Entry<String, Long> kitRental : playerData.getKitRentals().entrySet()) {
                saveRentals.setInt(2, AchillesPlugin.getInstance().getKitHandler().getKitByName(kitRental.getKey()).getId());
                saveRentals.setBigDecimal(3, new BigDecimal(kitRental.getValue()));
                saveRentals.addBatch();
            }

            saveStats.executeUpdate();
            saveBasic.executeUpdate();
            saveRentals.executeBatch();
            saveKitStats.executeBatch();
            saveLoadouts.executeBatch();

        } catch (SQLException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void cleanup(UUID uuid) {
    }
}
