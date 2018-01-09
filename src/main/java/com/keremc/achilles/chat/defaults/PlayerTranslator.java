package com.keremc.achilles.chat.defaults;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.chat.ChatFlags;
import com.keremc.achilles.data.PlayerData;
import com.keremc.achilles.chat.TextTranslator;
import com.keremc.achilles.kit.Kit;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class PlayerTranslator extends TextTranslator<Player> {

    @Override
    public TextComponent format(Player player, ChatFlags flags) {

        TextComponent tc = new TextComponent(player.getDisplayName());
        tc.setColor(ChatColor.AQUA);

        PlayerData pd = AchillesPlugin.getInstance().getPlayerHandler().getSession(player.getUniqueId());
        Kit kit = pd.getSelectedKit();

        TextComponent[] stats = pd.getStats().createTooltipStatArray(true);
        TextComponent[] hover = new TextComponent[stats.length + 2];

        hover[0] = new TextComponent("§l" + player.getDisplayName());
        hover[1] = new TextComponent("\n");

        for (int i = 0; i < stats.length; i++) {
            hover[i + 2] = stats[i];
        }

        tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                hover
        ));

        if (!pd.isDuelMode()) {
            tc.addExtra("§7[");
            tc.addExtra(TextTranslator.getTranslator(Kit.class).format(kit, null));
            tc.addExtra("§7]");
        }

        return tc;
    }
}
