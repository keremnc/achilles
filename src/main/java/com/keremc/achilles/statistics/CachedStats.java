package com.keremc.achilles.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CachedStats extends Stats {
    private long timeToLive;
}
