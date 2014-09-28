package com.moltendorf.bukkit.spawntweaks;

import org.bukkit.entity.EntityType;

import java.util.*;

/**
 * Created by moltendorf on 14/09/27.
 */
public class BiomeConfig {
	final protected double maxRoll;

	final protected Set<EntityType> search;
	final protected Map<EntityType, Double> replace;

	public BiomeConfig(final Set<EntityType> search, final Map<EntityType, Double> replace) {
		this.search = search;
		this.replace = sortByValue(replace);

		double max = 0;

		for (Double chance : this.replace.values()) {
			chance += max;
			max = chance;
		}

		maxRoll = max;
	}

	/**
	 * I was lazy and copied this in.
	 * Credit: http://stackoverflow.com/questions/109383/how-to-sort-a-mapkey-value-on-the-values-in-java
	 */
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());

		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			@Override
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		Map<K, V> result = new LinkedHashMap<>();

		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}

		return result;
	}
}
