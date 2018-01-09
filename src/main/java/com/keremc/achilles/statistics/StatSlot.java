package com.keremc.achilles.statistics;

import com.keremc.achilles.data.PlayerData;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

@AllArgsConstructor
public enum StatSlot {
    KILLS("kills", "Kills", true, true, false),
    DEATHS("deaths", "Deaths", true, true, false),
    KDR("kd_ratio", "KDR", true, true, true),

    CURRENT_STREAK("current_streak", "Current Killstreak", true, true, false),
    MAX_STREAK("max_streak", "Max Killstreak", true, true, false),

    DUEL_WINS("duel_wins", "Duel Wins", true, false, false),
    DUEL_LOSSES("duel_losses", "Duel Losses", true, false, false),
    WLR("wl_ratio", "W/L Ratio", true, false, true),

    WINSTREAK("win_streak", "Winstreak", true, false, false),
    WINSTREAK_MAX("max_win_streak", "Max Winstreak", true, false, false);

    @Getter private String dbKey;
    @Getter private String friendlyName;
    @Getter private boolean scoreboard;
    @Getter private boolean ffa;
    @Getter private boolean dynamic;

    public boolean shouldDisplay(PlayerData pd) {

        return pd.isDuelMode() ? !ffa : ffa;
    }

    public static StatSlot parse(String input) {
        return Stream.of(values()).filter(s ->
                s.name().equalsIgnoreCase(input)
                        || s.dbKey.equalsIgnoreCase(input)
                        || s.friendlyName.replace(" ", "").equalsIgnoreCase(input.replace(" ", ""))
        ).map(Optional::ofNullable).findFirst().flatMap(Function.identity()).orElse(null);
    }
}