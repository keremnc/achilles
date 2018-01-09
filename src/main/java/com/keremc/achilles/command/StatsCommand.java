package com.keremc.achilles.command;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.chat.Style;
import com.keremc.achilles.command.params.UsernameParameterType;
import com.keremc.achilles.data.PlayerData;
import com.keremc.achilles.data.db.DataHandler;
import com.keremc.achilles.statistics.CachedStats;
import com.keremc.achilles.statistics.Leaderboards;
import com.keremc.achilles.statistics.StatSlot;
import com.keremc.achilles.statistics.Stats;
import com.keremc.achilles.statistics.gui.LeaderboardsMenu;
import com.keremc.core.command.Command;
import com.keremc.core.command.param.Parameter;
import com.keremc.core.util.UUIDUtils;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class StatsCommand {

    @Command(names = "stats")
    public static void stats(CommandSender sender, @Parameter(name = "name", defaultValue = "§") UsernameParameterType.Username username) {
        String name = username.getName();
        boolean self = sender.getName().equals(name);

        UUID uuid = UUIDUtils.uuid(name);

        if (uuid == null) {
            username.notFound((Player) sender);
            return;
        }

        PlayerData pd = AchillesPlugin.getInstance().getPlayerHandler().getSession(uuid);
        if (pd != null && pd.getPlayer() != null) {
            Stats stats = pd.getStats();

            sendStats(sender, stats, UUIDUtils.name(uuid));
        } else {
            sender.sendMessage(Style.header("§7Loading stats..."));

            Bukkit.getScheduler().runTaskAsynchronously(AchillesPlugin.getInstance(), () -> {
                CachedStats cs = DataHandler.loadOfflineStats(uuid);

                if (cs == null) {
                    username.notFound((Player) sender);
                    return;
                }

                sendStats(sender, cs, UUIDUtils.name(uuid));

            });
        }

    }

    @Command(names = "stats top")
    public static void statsTopGeneric(Player sender) {
        new LeaderboardsMenu().openMenu(sender);
    }

    @Command(names = "stats top")
    public static void statsTop(Player sender, @Parameter(name = "stat") StatSlot val) {
        String[] data = Style.box("§c§l" + val.getFriendlyName(), 45);

        Map.Entry<UUID, Double>[] top = Leaderboards.retrieve(val);
        sender.sendMessage(data[0]);

        for (int i = 0; i < Math.min(10, top.length); i++) {
            int rank = i + 1;

            String base = (rank == 1 ? "§6§l1§7. §a⚝" : "§7" + rank + ".") + " %s{"
                    + (rank == 1 ? "d" : "c") + "} §7-§e "
                    + Style.DOUBLE_FORMAT.format(top[i].getValue()) + " " + val.getFriendlyName().toLowerCase();

            TextComponent formatted = Style.format(base, top[i].getKey());

            sender.spigot().sendMessage(formatted);


        }

        sender.sendMessage(data[1]);


    }


    private static void sendStats(CommandSender sender, Stats stats, String name) {

        String[] data = Style.box("§c§l" + name, 45);

        sender.sendMessage(data[0]);

        for (TextComponent tc : stats.createTooltipStatArray(false)) {
            String legacy = tc.toLegacyText();

            if (sender instanceof Player) {
                ((Player) sender).spigot().sendMessage(tc);
            } else {
                sender.sendMessage(legacy);
            }
        }

        sender.sendMessage(data[1]);

    }
}
