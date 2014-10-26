package com.moltendorf.bukkit.spawntweaks;

import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

/**
 * Configuration class.
 *
 * @author moltendorf
 */
public class Configuration {

	static protected class Global {

		// Final data.
		final protected boolean enabled = true; // Whether or not the plugin is enabled at all; useful for using it as an interface (default is true).

		final protected int blockLight = 0;
		final protected int skyLight = 4;

		final protected HashSet<EntityType> naturalCreatures = new HashSet<>(Arrays.asList(
			EntityType.SPIDER
		));

		final protected int limit = 1000;

		final protected HashSet<EntityType> limitedCreatures = new HashSet<>(Arrays.asList(
			EntityType.BLAZE,
			EntityType.CAVE_SPIDER,
			EntityType.CREEPER,
			EntityType.ENDERMAN,
			EntityType.GIANT,
			EntityType.PIG_ZOMBIE,
			EntityType.SILVERFISH,
			EntityType.SKELETON,
			EntityType.SLIME,
			EntityType.SPIDER,
			EntityType.WITCH,
			EntityType.ZOMBIE
		));

		final protected HashSet<CreatureSpawnEvent.SpawnReason> spawnReasons = new HashSet<>(Arrays.asList(
			CreatureSpawnEvent.SpawnReason.NATURAL,
			CreatureSpawnEvent.SpawnReason.NETHER_PORTAL,
			CreatureSpawnEvent.SpawnReason.VILLAGE_INVASION
		));

		final protected HashMap<Biome, BiomeConfig> biomes = new HashMap<Biome, BiomeConfig>() {{
			put(Biome.SWAMPLAND, new BiomeConfig(
				new HashSet<>(Arrays.asList(
					EntityType.CREEPER,
					EntityType.ENDERMAN,
					EntityType.SKELETON,
					EntityType.SPIDER,
					EntityType.ZOMBIE
				)),

				new LinkedHashMap<EntityType, Double>() {{
					put(EntityType.SLIME, 1.);
				}}
			));
		}};
	}

	// Final data.
	final protected Global global = new Global();

	public Configuration() {

		// Placeholder.
	}
}
