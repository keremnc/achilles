package com.keremc.achilles.command;

import com.keremc.achilles.visual.LoadingBar;
import com.keremc.core.command.Command;
import com.keremc.core.command.param.Parameter;
import org.bukkit.entity.Player;

public class LoadingBarTestCommand {

    @Command(names = "lbtc")
    public static void lbtc(Player sender, @Parameter(name = "duration") double dura, @Parameter(name = "header") String header, @Parameter(name = "final") String finished) {
        LoadingBar lb = new LoadingBar(header, finished, dura);
        lb.display(sender);

    }
}
