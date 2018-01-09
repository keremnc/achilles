package com.keremc.achilles.loadout.builder.functions;

import com.google.common.base.Function;
import com.keremc.achilles.loadout.builder.CustomPart;

public class AmountPartFunction implements Function<CustomPart<Integer>, Integer> {

    @Override
    public Integer apply(CustomPart customPart) {
        return customPart.getCurrentPrimary().getIntData();
    }
}
