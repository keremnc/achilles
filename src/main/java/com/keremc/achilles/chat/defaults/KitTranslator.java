package com.keremc.achilles.chat.defaults;

import com.keremc.achilles.chat.ChatFlags;
import com.keremc.achilles.chat.TextTranslator;
import com.keremc.achilles.kit.Kit;
import com.keremc.core.item.ItemBuilder;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;

public class KitTranslator extends TextTranslator<Kit> {

    @Override
    public TextComponent format(Kit kit, ChatFlags flags) {
        TextComponent hover[];
        if (kit != null) {
            List<String> lore = ItemBuilder.wrap(kit.getDescription(), "§f");
            hover = new TextComponent[lore.size() + 2];
            hover[0] = new TextComponent("§e§l" + kit.getName());
            hover[1] = new TextComponent("\n");

            for (int i = 0; i < lore.size(); i++) {
                hover[i + 2] = new TextComponent("\n" + lore.get(i));
            }
        } else {
            hover = new TextComponent[]{
                    new TextComponent("§7§lNone"),
                    new TextComponent("\n"),
                    new TextComponent("\n§fThis mighty warrior fights without the"),
                    new TextComponent("\n§fuse of a kit!")
            };
        }


        TextComponent tc = new TextComponent(kit == null ? "None" : kit.getName());
        tc.setColor(ChatColor.GOLD);
        tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                hover
        ));

        return tc;
    }
}
