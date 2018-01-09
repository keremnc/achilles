package com.keremc.achilles.chat.defaults;

import com.keremc.achilles.chat.ChatFlags;
import com.keremc.achilles.chat.Style;
import com.keremc.achilles.chat.TextTranslator;
import com.keremc.core.util.BMComponentBuilder;
import com.keremc.core.util.UUIDUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Arrays;
import java.util.UUID;

public class UUIDTranslator extends TextTranslator<UUID> {

    @Override
    public TextComponent format(UUID player, ChatFlags flags) {

        String name = UUIDUtils.name(player);

        TextComponent tc = new TextComponent(name);

        if (flags != null && flags.getColors() != null) {
            Style.color(tc, Arrays.asList(flags.getColors()));
        }

        tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BMComponentBuilder("Click to view stats!").create()));
        tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/stats " + name));
        return tc;
    }
}
