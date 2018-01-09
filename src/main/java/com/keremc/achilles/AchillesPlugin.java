package com.keremc.achilles;

import com.keremc.achilles.command.params.*;
import com.keremc.achilles.command.params.UsernameParameterType.Username;
import com.keremc.achilles.data.PlayerData;
import com.keremc.achilles.data.PlayerHandler;
import com.keremc.achilles.data.SaveHandler;
import com.keremc.achilles.data.db.DataHandler;
import com.keremc.achilles.data.db.impl.MySQLDataHandler;
import com.keremc.achilles.kit.Kit;
import com.keremc.achilles.kit.KitHandler;
import com.keremc.achilles.kit.command.CommandManager;
import com.keremc.achilles.match.MatchHandler;
import com.keremc.achilles.region.RegionHandler;
import com.keremc.achilles.region.Tag;
import com.keremc.achilles.statistics.Leaderboards;
import com.keremc.achilles.statistics.StatSlot;
import com.keremc.achilles.visual.ScoreboardHandler;
import com.keremc.core.command.BMCommandHandler;
import com.keremc.core.scoreboard.BMScoreboardHandler;
import com.keremc.core.util.ClassUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class AchillesPlugin extends JavaPlugin {

    @Getter private static AchillesPlugin instance;

    @Getter private KitHandler kitHandler;
    @Getter private RegionHandler regionHandler;
    @Getter private PlayerHandler playerHandler;
    @Getter private DataHandler dataHandler;
    @Getter private MatchHandler matchHandler;

    @Override
    public void onEnable() {
        instance = this;
        dataHandler = new MySQLDataHandler();

        kitHandler = new KitHandler();
        regionHandler = new RegionHandler();
        playerHandler = new PlayerHandler();
        matchHandler = new MatchHandler();

        AchillesPlugin.getInstance().getDataHandler().loadRegions();

        new CommandManager().register();
        BMCommandHandler.loadCommandsFromPackage(this, "com.keremc.achilles.command");

        ClassUtils.getClassesInPackage(this, "com.keremc.achilles.listener").stream().filter(Listener.class::isAssignableFrom).forEach(clazz -> {
            try {
                Bukkit.getPluginManager().registerEvents((Listener) clazz.newInstance(), this);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        BMCommandHandler.registerParameterType(Kit.class, new KitParameterType());
        BMCommandHandler.registerParameterType(Tag.class, new TagParameterType());
        BMCommandHandler.registerParameterType(Username.class, new UsernameParameterType());
        BMCommandHandler.registerParameterType(StatSlot.class, new StatSlotParameterType());
        BMCommandHandler.registerParameterType(ChallengerParameterType.Challenger.class, new ChallengerParameterType());

        Leaderboards.init();

        for (Player player : Bukkit.getOnlinePlayers()) {
            getPlayerHandler().loadData(player.getUniqueId(), player.getName());
            getPlayerHandler().handleLogin(player);
            getPlayerHandler().handleJoin(player);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    PlayerData pd = AchillesPlugin.getInstance().getPlayerHandler().getSession(player.getUniqueId());

                    pd.tick();
                }
            }
        }.runTaskTimer(this, 5L, 2L);
        DataHandler.init();

        BMScoreboardHandler.setConfiguration(ScoreboardHandler.create());
    }

    @Override
    public void onDisable() {
        SaveHandler.saveAll();
    }
}
