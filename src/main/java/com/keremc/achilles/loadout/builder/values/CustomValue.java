package com.keremc.achilles.loadout.builder.values;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

@RequiredArgsConstructor
@Data
public abstract class CustomValue<T> {
    private @NonNull Material display;
    private @NonNull T data;
    private @NonNull String name;

    private short shortData = 0;
    private int intData = 1;
    private String strData = "";

}
