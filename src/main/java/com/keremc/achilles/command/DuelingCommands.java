package com.keremc.achilles.command;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.command.params.ChallengerParameterType;
import com.keremc.achilles.match.PlayerMatchInvite;
import com.keremc.core.command.Command;
import com.keremc.core.command.param.Parameter;
import com.keremc.core.util.UUIDUtils;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DuelingCommands {

    @Command(names = "accept")
    public static void accept(Player sender, @Parameter(name = "player", defaultValue = "§") ChallengerParameterType.Challenger challenger) {
        String name = challenger.getName();
        UUID uuid = name == null ? null : UUIDUtils.uuid(name);

        PlayerMatchInvite pmi = AchillesPlugin.getInstance().getMatchHandler().getPlayerInvite(uuid, sender.getUniqueId());

        AchillesPlugin.getInstance().getMatchHandler().acceptInvitation(pmi);


    }

}
