package com.keremc.achilles.visual;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.chat.Style;
import com.keremc.achilles.data.PlayerData;
import com.keremc.achilles.loadout.MatchLoadout;
import com.keremc.achilles.match.MatchHandler;
import com.keremc.achilles.statistics.StatSlot;
import com.keremc.core.scoreboard.ScoreGetter;
import com.keremc.core.scoreboard.ScoreboardConfiguration;
import com.keremc.core.scoreboard.TitleGetter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScoreboardHandler implements ScoreGetter {

    public static ScoreboardConfiguration create() {
        ScoreboardConfiguration sc = new ScoreboardConfiguration();
        sc.setTitleGetter(new TitleGetter("§e§lStats    "));
        sc.setScoreGetter(new ScoreboardHandler());

        return sc;

    }

    @Override
    public String[] getScores(Player player) {
        List<String> scores = new ArrayList<>();
        scores.add("§a" + ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 25));
        PlayerData pd = AchillesPlugin.getInstance().getPlayerHandler().getSession(player.getUniqueId());

        if (!pd.isDuelMode()) {
            add(scores, "Kit", pd.getSelectedKit() != null ? pd.getSelectedKit().getName() : "None");
        } else {
            MatchHandler mh = AchillesPlugin.getInstance().getMatchHandler();
            MatchLoadout queued = mh.getMatchSoloQueue().entrySet().stream().filter(e -> e.getValue() == player.getUniqueId()).map(Map.Entry::getKey)
                    .findFirst().orElse(null);

            add(scores, "Queued", queued == null ? "None" : (queued.isCustom() ? "Custom" : queued.getName()));
        }

        add(scores, "Tokens", Style.DOUBLE_FORMAT.format(pd.getBalance()));
        scores.add("§a");


        for (StatSlot ss : StatSlot.values()) {
            if (ss.isScoreboard() && ss.shouldDisplay(pd)) {
                add(scores, ss.getFriendlyName(), Style.DOUBLE_FORMAT.format(pd.getStats().get(ss)) + "");
                if (ss.isDynamic()) {
                    scores.add("§e" + StringUtils.repeat(" ", ss.ordinal()));
                }
            }
        }

        scores.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 25));


        return scores.toArray(new String[]{});
    }

    private static void add(List list, String key, String val) {
        list.add("§e" + key + ": §f*" + val);
    }
}
