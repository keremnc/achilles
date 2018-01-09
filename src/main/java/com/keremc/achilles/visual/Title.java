package com.keremc.achilles.visual;


import lombok.Data;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@Data
public class Title {
    private String title = "";
    private ChatColor titleColor = ChatColor.WHITE;

    private String subtitle = "";
    private ChatColor subtitleColor = ChatColor.WHITE;

    private int fadeInTime = -1;
    private int stayTime = -1;
    private int fadeOutTime = -1;
    private boolean ticks = false;

    private static final Map<Class<?>, Class<?>> CORRESPONDING_TYPES = new HashMap<Class<?>, Class<?>>();

    public Title(String title) {
        this.title = title;
    }

    public Title(String title, String subtitle) {
        this.title = title;
        this.subtitle = subtitle;
    }

    public Title(Title title) {
        // Copy title
        this.title = title.title;
        this.subtitle = title.subtitle;
        this.titleColor = title.titleColor;
        this.subtitleColor = title.subtitleColor;
        this.fadeInTime = title.fadeInTime;
        this.fadeOutTime = title.fadeOutTime;
        this.stayTime = title.stayTime;
        this.ticks = title.ticks;
    }

    public Title(String title, String subtitle, int fadeInTime, int stayTime,
                 int fadeOutTime) {
        this.title = title;
        this.subtitle = subtitle;
        this.fadeInTime = fadeInTime;
        this.stayTime = stayTime;
        this.fadeOutTime = fadeOutTime;
    }

    public Title fast() {
        fadeInTime = 10;
        stayTime = 20;
        fadeOutTime = 20;

        return this;
    }

    public void send(Player player) {
        // First reset previous settings
        resetTitle(player);

        PacketPlayOutTitle timings = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, null, fadeInTime, stayTime, fadeOutTime);

        PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, new ChatComponentText(title == null ? "" : title));
        PacketPlayOutTitle subPacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, new ChatComponentText(subtitle == null ? "" : subtitle));

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(timings);

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(titlePacket);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(subPacket);
    }


    public void broadcast() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            send(p);
        }
    }

    public void clearTitle(Player player) {
        PacketPlayOutTitle reset = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.CLEAR, null);

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(reset);
    }

    /**
     * Reset the title settings
     *
     * @param player Player
     */
    public void resetTitle(Player player) {
        PacketPlayOutTitle reset = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.RESET, null);

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(reset);
    }

}