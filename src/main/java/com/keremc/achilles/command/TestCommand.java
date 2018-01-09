package com.keremc.achilles.command;

import com.keremc.achilles.chat.Style;
import com.keremc.core.command.Command;
import com.keremc.core.command.param.Parameter;
import org.bukkit.entity.Player;

public class TestCommand {

    @Command(names = "test")
    public static void test(Player sender, @Parameter(name = "header") String header, @Parameter(name = "width") int width) {
        String[] data = Style.box(header, width);

        sender.sendMessage(data[0]);
        sender.sendMessage(data[1]);
    }
}
