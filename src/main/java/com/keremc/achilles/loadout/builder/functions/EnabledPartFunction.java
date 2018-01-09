package com.keremc.achilles.loadout.builder.functions;

import com.google.common.base.Function;
import com.keremc.achilles.loadout.builder.CustomPart;
import com.keremc.achilles.loadout.builder.values.CustomBooleanValue;
import com.keremc.achilles.loadout.builder.values.CustomValue;

public class EnabledPartFunction implements Function<CustomPart<Boolean>, Boolean> {

    @Override
    public Boolean apply(CustomPart customPart) {
        CustomValue cv = customPart.getCurrentPrimary();

        return cv instanceof CustomBooleanValue && ((CustomBooleanValue) cv).getData();
    }
}
