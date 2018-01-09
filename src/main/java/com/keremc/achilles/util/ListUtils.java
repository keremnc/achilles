package com.keremc.achilles.util;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class ListUtils {

    public static String[] sortListBySearching(ArrayList<String> search, String s) {
        HashMap<String, Integer> leven = new HashMap<>();

        if (s.length() <= 2) {
            return new String[]{};
        }

        for (String str : search) {
            if (str.length() > 2 && str.substring(0, 2).equalsIgnoreCase(s.substring(0, 2))) {
                leven.put(str, StringUtils.getLevenshteinDistance(str, s));
            }
        }

        List<String> mapKeys = new ArrayList(leven.keySet());
        List<String> mapValues = new ArrayList(leven.values());

        Collections.sort(mapValues);
        Collections.sort(mapKeys);

        LinkedHashMap sortedMap = new LinkedHashMap();

        Iterator valueIt = mapValues.iterator();

        while (valueIt.hasNext()) {

            Object val = valueIt.next();
            Iterator keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                Object key = keyIt.next();

                String comp1 = leven.get(key).toString();
                String comp2 = val.toString();

                if (comp1.equals(comp2)) {

                    leven.remove(key);
                    mapKeys.remove(key);

                    sortedMap.put(key, val);

                    break;
                }

            }

        }

        return new ArrayList<String>(sortedMap.keySet()).subList(0, Math.min(sortedMap.size(), 5)).toArray(new String[]{});
    }

    public static List<String> getLines(File f) {
        try {
            final BufferedReader reader = new BufferedReader(new FileReader(f));
            try {
                final List<String> lines = new ArrayList<String>();
                do {
                    final String line = reader.readLine();
                    if (line == null) {
                        break;
                    } else {
                        lines.add(line);
                    }
                } while (true);
                return lines;
            } finally {
                reader.close();
            }
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
            return Collections.emptyList();
        }
    }
}
