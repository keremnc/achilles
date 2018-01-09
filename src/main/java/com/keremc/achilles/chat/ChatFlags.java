package com.keremc.achilles.chat;

import lombok.Data;
import net.md_5.bungee.api.ChatColor;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class ChatFlags {
    private ChatColor[] colors;


    public static class FlagParser {

        public ChatFlags parseObject(String entry) {
            ChatFlags cf = new ChatFlags();


            cf.colors = Stream.of(entry.split(",")).map(
                    s -> ChatColor.getByChar(s.trim().charAt(0))
            ).collect(Collectors.toList()).toArray(new ChatColor[]{});

            return cf;
        }
    }
}
