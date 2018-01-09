package com.keremc.achilles.chat;

import com.google.common.primitives.Chars;
import com.keremc.achilles.AchillesPlugin;
import com.keremc.core.util.BMComponentBuilder;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class Style {
    private static final Map<Character, Integer> FONT_WIDTHS = new HashMap<>();

    public static final String HEADER = "§9§l» ";
    public static final DecimalFormat DOUBLE_FORMAT = new DecimalFormat("#,###.##");
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMMMM dd, h:mm a z");

    private static final String HELP_COLOR = "§7";

    public static final String BOX_LINE_COLOR = "§7§m";
    public static final char BOX_CHAR = '-';

    static {
        try {
            for (String line : IOUtils.readLines(AchillesPlugin.class.getClassLoader().getResourceAsStream("fontWidths.txt"))) {
                if (line.contains("=")) {
                    char c = line.charAt(0);
                    int width = Integer.parseInt(line.substring(line.lastIndexOf("=") + 1));
                    FONT_WIDTHS.put(c, width);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String header(String msg) {
        return HEADER + "§e" + msg;
    }

    public static void color(TextComponent tc, Collection<ChatColor> colors) {
        for (ChatColor color : colors) {
            if (color == ChatColor.BOLD) {
                tc.setBold(true);
            } else if (color == ChatColor.ITALIC) {
                tc.setItalic(true);
            } else if (color == ChatColor.STRIKETHROUGH) {
                tc.setStrikethrough(true);
            } else if (color == ChatColor.UNDERLINE) {
                tc.setUnderlined(true);
            } else if (color == ChatColor.MAGIC) {
                tc.setObfuscated(true);
            } else {
                tc.setColor(color);
            }
        }
    }

    public static void appendPart(TextComponent base, String part, Collection<ChatColor> colors, AtomicInteger formatIndex, Object... format) {
        String append = part;
        int formatAmt = format != null ? format.length : 0;

        TextComponent tc = new TextComponent();

        if (append.contains("%s") && formatIndex.get() < formatAmt) {

            String[] parts = append.split("%s", -1);

            int index = 0;
            for (String interval : parts) {
                ChatFlags cf = null;
                boolean last = true;

                if (parts.length > index + 1) {
                    last = false;
                    String next = parts[index + 1];

                    if (next.startsWith("{") && next.contains("}")) {
                        String payload = next.substring(1, next.indexOf('}'));
                        parts[index + 1] = next.substring(next.indexOf('}') + 1);

                        cf = new ChatFlags.FlagParser().parseObject(payload);
                    }
                }

                if (!interval.isEmpty()) {

                    TextComponent comp = new TextComponent(interval);
                    color(comp, colors);

                    base.addExtra(comp);
                }

                if (!last) {
                    Object fo = format[formatIndex.getAndAdd(1)];

                    base.addExtra(TextTranslator.translate(fo, cf));
                }
                index++;
            }

        } else {
            color(tc, colors);
            base.addExtra(tc);
            tc.setText(append);

        }


        colors.clear();
    }

    /**
     * Automatically uses registered TextTranslators to format a given string into JSON compliant Minecraft chat
     *
     * @param message legacy text format, contains %s for objects and section-symbol based colors
     * @param format  array of objects, ordered according to order or %s in message
     * @return formatted JSON chat object
     */
    public static TextComponent format(String message, Object... format) {
        String translated = org.bukkit.ChatColor.translateAlternateColorCodes('&', message);

        AtomicInteger formatIndex = new AtomicInteger(0);

        Set<ChatColor> colors = new HashSet<>();
        boolean newlyColored = false;
        String part = "";

        TextComponent base = new TextComponent(HEADER);

        for (char c : translated.toCharArray()) {
            if (c == '§') {

                if (part != null && !part.isEmpty()) {

                    appendPart(base, part, colors, formatIndex, format);
                    part = "";
                }

                newlyColored = true;
                continue;
            }

            if (newlyColored) {
                newlyColored = false;
                ChatColor cc = ChatColor.getByChar(c);
                if (cc != null) {
                    colors.add(cc);
                }
                continue;
            }

            part += c;
        }

        if (part != null && !part.isEmpty()) {
            appendPart(base, part, colors, formatIndex, format);
        }

        return base;

    }

    public static TextComponent killstreak(Player killer, int killstreak) {
        BMComponentBuilder cb = new BMComponentBuilder(HEADER);
        cb.append(TextTranslator.translate(killer, null));
        cb.append(" has reached a ").color(ChatColor.YELLOW);
        cb.append("killstreak").color(ChatColor.RED);
        cb.append(" of ").color(ChatColor.YELLOW);
        cb.append(killstreak + "").color(ChatColor.RED).bold(true).italic(true);
        cb.append("!").color(ChatColor.YELLOW).bold(false).italic(false);

        TextComponent deathMsg = new TextComponent(cb.create());

        return deathMsg;
    }

    public static TextComponent assisted(Player died, double tokens, double pct) {
        ComponentBuilder cb = new ComponentBuilder(HEADER);

        String tok = DOUBLE_FORMAT.format(tokens);
        String percent = DOUBLE_FORMAT.format(pct * 100D);

        cb.append("Earned ").color(ChatColor.YELLOW);
        cb.append(tok).color(ChatColor.AQUA).bold(true);
        cb.append(" tokens for dealing ").color(ChatColor.YELLOW).bold(false);
        cb.append(percent + "%").color(ChatColor.RED).bold(true);
        cb.append(" of the damage to ").color(ChatColor.YELLOW).bold(false);

        TextComponent deathMsg = new TextComponent(cb.create());
        deathMsg.addExtra(TextTranslator.translate(died, null));
        deathMsg.addExtra("§e!");

        return deathMsg;
    }

    public static TextComponent killed(Player died, double tokens) {
        String tok = DOUBLE_FORMAT.format(tokens);

        BMComponentBuilder cb = new BMComponentBuilder(HEADER);

        cb.append("Earned ").color(ChatColor.YELLOW);
        cb.append(tok).color(ChatColor.RED).bold(true);
        cb.append(" tokens for killing ").color(ChatColor.YELLOW).bold(false);

        cb.append(TextTranslator.translate(died, null));
        cb.append("!").color(ChatColor.YELLOW);

        TextComponent deathMsg = new TextComponent(cb.create());

        return deathMsg;
    }

    public static TextComponent endedKillstreak(Player killer, Player dead, int killstreak) {

        BMComponentBuilder cb = new BMComponentBuilder(HEADER);
        cb.append(TextTranslator.translate(killer, null));
        cb.append(" has ended ").color(ChatColor.YELLOW).bold(false);
        cb.append(TextTranslator.translate(dead, null));
        cb.append("'s ").color(ChatColor.YELLOW);
        cb.append(killstreak + " killstreak").color(ChatColor.RED).bold(true).italic(true);
        cb.append("!").color(ChatColor.YELLOW).bold(false).italic(false);

        TextComponent deathMsg = new TextComponent(cb.create());
        return deathMsg;
    }

    public static TextComponent deathMessage(Player killer, Player dead) {

        TextComponent deathMsg = new TextComponent(HEADER);

        deathMsg.addExtra(TextTranslator.translate(dead, null));

        if (killer == null) {
            deathMsg.addExtra(" died");
            deathMsg.setColor(ChatColor.YELLOW);
            return deathMsg;
        }

        deathMsg.addExtra(" was killed by ");
        deathMsg.setColor(ChatColor.YELLOW);

        deathMsg.addExtra(TextTranslator.translate(killer, null));

        return deathMsg;
    }


    public static int getCharLength(char c, boolean bold) {
        return (FONT_WIDTHS.containsKey(c) ? FONT_WIDTHS.get(c) : 4) + ((bold && c != ' ') ? 1 : 0);
    }

    public static String rightAlign(String base, String align, int buffer) {
        int betweenLength = buffer * 4;

        for (char c : base.toCharArray()) {
            betweenLength -= getCharLength(c, false);
        }

        return base + (StringUtils.repeat(" ", (int) Math.ceil(betweenLength / 4D))) + align;


    }

    public static int strSize(String str) {
        Map.Entry<String, org.bukkit.ChatColor[]> data = removeColors(str);
        boolean bold = Arrays.asList(data.getValue()).contains(org.bukkit.ChatColor.BOLD);
        return (data.getKey().chars().map(i -> getCharLength((char) i,
                bold
        )).sum());
    }

    public static Map.Entry<String, org.bukkit.ChatColor[]> removeColors(String message) {
        List<org.bukkit.ChatColor> colors = new ArrayList<>();

        String colorless = "";

        boolean sectionSymbol = false;
        for (char c : message.toCharArray()) {

            if (c == '§' || c == '&') {
                sectionSymbol = true;

            } else if (sectionSymbol) {
                org.bukkit.ChatColor cc = org.bukkit.ChatColor.getByChar(c);

                if (cc == null) {
                    colorless += "&" + c;
                } else {
                    colors.add(cc);
                }
                sectionSymbol = false;

            } else {
                colorless += c;
            }

        }

        return new AbstractMap.SimpleEntry<>(colorless, colors.toArray(new org.bukkit.ChatColor[colors.size()]));

    }

    public static String[] box(String header, int charAmt) {
        Map.Entry<String, org.bukkit.ChatColor[]> separated = removeColors(header);
        String[] box = new String[2];

        org.bukkit.ChatColor[] colors = separated.getValue();
        String message = separated.getKey();
        boolean bold = colors != null && Arrays.asList(colors).stream().anyMatch(c -> c == org.bukkit.ChatColor.BOLD);

        int len = getCharLength(BOX_CHAR, BOX_LINE_COLOR.contains("§l"));

        int bottomWidth = charAmt * len;
        int headerWidth = Chars.asList((" " + message + " ").toCharArray()).stream().mapToInt(c -> getCharLength(c, bold)).sum();

        int amtSide = (int) Math.round(((bottomWidth - headerWidth) / (2D * len)));

        final StringBuilder sb = new StringBuilder();
        Stream.of(colors).map(cc -> "§" + cc.getChar()).forEach(sb::append);

        String top = BOX_LINE_COLOR + StringUtils.repeat(String.valueOf(BOX_CHAR), amtSide) + "§r " +
                sb.toString() + message + " §r"
                + BOX_LINE_COLOR + StringUtils.repeat(String.valueOf(BOX_CHAR), amtSide);

        box[0] = top;
        box[1] = BOX_LINE_COLOR + StringUtils.repeat(String.valueOf(BOX_CHAR), charAmt);

        return box;

    }

    public static String[] getJoinMessages() {
        return new String[]{"§7§m-----------------------", "Sup", "Welcome", "How you doing?", "§7§m-----------------------"};
    }


    public static String[] getDuelHelp() {
        return new String[]{
                "§7§m---------------------§a Duel §7§m-------------------------",
                "§eQuickmatch Stick §c&§e Custom Challenge Stick",
                "     §6LEFT-CLICK" + HELP_COLOR + "  to accept first invite",
                "     §6RIGHT-CLICK" + HELP_COLOR + " §bplayer" + HELP_COLOR + " to invite to a match",
                "",
                "§ePending Invites",
                "     §6CLICK" + HELP_COLOR + " to view pending invites",
                "",
                "§eQueue Chest",
                "     §6OPEN" + HELP_COLOR + " to join a §bqueue" + HELP_COLOR + " or view §bcustom loadouts",
                "",
                "§eQuickmatch Button",
                "     §6CLICK" + HELP_COLOR + " to find the next available match",
                "§7§m--------------------------------------------------"};
    }
}
