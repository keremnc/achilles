package com.keremc.achilles.command;

import com.keremc.achilles.AchillesPlugin;
import com.keremc.achilles.chat.Style;
import com.keremc.achilles.region.Region;
import com.keremc.achilles.region.Tag;
import com.keremc.achilles.region.select.Selection;
import com.keremc.core.command.Command;
import com.keremc.core.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RegioningCommands {

    @Command(names = {"rg d", "region del"}, permissionNode = "achilles.region.create")
    public static void del(Player sender, @Parameter(name = "name") String name) {
        if (AchillesPlugin.getInstance().getRegionHandler().getRegion(name) == null) {
            sender.sendMessage(ChatColor.RED + "A region with that name does not exist.");

            return;
        }

        AchillesPlugin.getInstance().getRegionHandler().deleteRegion(name);
        sender.sendMessage(Style.header(ChatColor.YELLOW + "Region has been deleted"));

        AchillesPlugin.getInstance().getDataHandler().saveRegions();
    }

    @Command(names = {"rg tool", "rg wand", "region tool", "rg wand"}, permissionNode = "terrafirma.use")
    public static void regionWand(Player sender) {
        if (sender.getInventory().contains(Selection.SELECTION_WAND)) {
            sender.sendMessage(ChatColor.RED + "You already have a §bregion tool §cin your inventory!");
            return;
        }

        sender.getInventory().addItem(Selection.SELECTION_WAND);
        sender.sendMessage(ChatColor.YELLOW + "You have been given the Region Tool!");
        sender.sendMessage(ChatColor.GREEN + "Left-click" + ChatColor.YELLOW + ": Set corner 1");
        sender.sendMessage(ChatColor.RED + "Right-click" + ChatColor.YELLOW + ": Set corner 2");

    }

    @Command(names = {"rg c", "region create"}, permissionNode = "achilles.region.create")
    public static void create(Player sender, @Parameter(name = "name") String name, @Parameter(name = "type") Tag type) {
        Selection sel = Selection.createOrGetSelection(sender);

        if (!sel.isFullObject()) {
            sender.sendMessage(ChatColor.RED + "Please select a region.");
            return;
        }

        Region region = sel.getCuboid();
        region.setName(name);
        region.setType(type);

        if (AchillesPlugin.getInstance().getRegionHandler().getRegion(name) != null) {
            sender.sendMessage(ChatColor.RED + "A region with that name exists already...");

            return;
        }

        AchillesPlugin.getInstance().getRegionHandler().addRegion(region);
        sender.sendMessage(Style.header(ChatColor.YELLOW + "Region has been created"));
        AchillesPlugin.getInstance().getDataHandler().saveRegions();

    }

    @Command(names = {"rg l", "region list"})
    public static void list(Player sender) {
        for (Region reg : AchillesPlugin.getInstance().getRegionHandler().getRegions()) {
            String msg = String.format("§e%s§b(§c%s§b) §7- (§e%s§7, §e%s§7, §e%s§7)§f, §7(§e%s§7, §e%s§7, §e%s§7)",
                    reg.getName(),
                    reg.getType(),
                    reg.getLowerCorner().getBlockX(),
                    reg.getLowerCorner().getBlockY(),
                    reg.getLowerCorner().getBlockZ(),

                    reg.getUpperCorner().getBlockX(),
                    reg.getUpperCorner().getBlockY(),
                    reg.getUpperCorner().getBlockZ()

            );

            sender.sendMessage(msg);
        }
    }
}
