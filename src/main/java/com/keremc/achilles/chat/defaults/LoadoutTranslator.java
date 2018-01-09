package com.keremc.achilles.chat.defaults;

import com.keremc.achilles.chat.ChatFlags;
import com.keremc.achilles.chat.TextTranslator;
import com.keremc.achilles.loadout.MatchLoadout;
import com.keremc.core.util.BMComponentBuilder;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;

public class LoadoutTranslator extends TextTranslator<MatchLoadout> {

    @Override
    public TextComponent format(MatchLoadout loadout, ChatFlags flags) {
        BMComponentBuilder componentBuilder = new BMComponentBuilder(loadout.isCustom() ? "Custom" : loadout.getName());

        componentBuilder.color(ChatColor.RED);

        List<String> data = loadout.getData(true);

        componentBuilder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, data.stream().map(s -> new TextComponent(s)).toArray(i -> new TextComponent[i])));

        return new TextComponent(componentBuilder.create());
    }
}
