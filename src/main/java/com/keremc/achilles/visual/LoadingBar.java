package com.keremc.achilles.visual;

import com.keremc.achilles.AchillesPlugin;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

public class LoadingBar extends BukkitRunnable {
    private static final int CENTER_SHIFT = 19;

    private static final char BAR_CHAR = '▍';
    private static final int BAR_AMOUNT = 24;
    private static final String DONE_COLOR = ChatColor.GREEN + "" + ChatColor.BOLD;
    private static final String LEFT_COLOR = ChatColor.GRAY + "" + ChatColor.BOLD;
    private static final DecimalFormat FORMAT = new DecimalFormat("0.0");

    private static Map<UUID, LoadingBar> actionBarTasks = new HashMap<>();

    private String header;
    private String finished;
    private long expiration;
    private double seconds;
    private Predicate<Player>[] tests;

    private UUID owner;

    public LoadingBar(String header, String finished, double seconds, Predicate<Player>... tests) {
        this.header = header;
        this.finished = finished;
        this.seconds = seconds;
        this.expiration = (long) (System.currentTimeMillis() + (seconds * 1000L));
        this.tests = tests;

    }

    /**
     * Method called every tick
     *
     * @param second seconds remaining on clock
     */
    public void on(Player player, double second) {}

    /**
     * Method called when task has been completed
     */
    public void completed(Player player) {}

    public void display(Player player) {

        this.owner = player.getUniqueId();

        if (actionBarTasks.containsKey(player.getUniqueId())) {
            actionBarTasks.remove(player.getUniqueId()).cancel();

        }

        actionBarTasks.put(player.getUniqueId(), this);
        runTaskTimer(AchillesPlugin.getInstance(), 0L, 2L);
    }

    public boolean done() {
        return System.currentTimeMillis() >= expiration;
    }

    public String createMessage() {

        double secondsLeft = (expiration - System.currentTimeMillis()) / 1000D;
        double pctDone = 1 - (secondsLeft / seconds);
        int doneAmt = (int) Math.ceil(pctDone * BAR_AMOUNT);
        int leftAmt = BAR_AMOUNT - doneAmt;

        String seconds = FORMAT.format(secondsLeft);

        return "§f§l " + header + " "
                + DONE_COLOR + (StringUtils.repeat(String.valueOf(BAR_CHAR), doneAmt))
                + LEFT_COLOR + (StringUtils.repeat(String.valueOf(BAR_CHAR), leftAmt))
                + " §f§l" + seconds + "§r seconds";
    }

    @Override
    public void run() {
        Player player = Bukkit.getPlayer(owner);

        if (player == null) {
            cancel();
            return;
        }

        double secondsLeft = (expiration - System.currentTimeMillis()) / 1000D;
        String message = createMessage();

        on(player, Double.parseDouble(FORMAT.format(secondsLeft)));

        if (done()) {
            message = "§a§l" + finished;
            cancel();
            completed(player);
        }

        sendPacket(message, player);


    }

    public void sendPacket(String message, Player player) {
        PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(message), (byte) 2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }
}
