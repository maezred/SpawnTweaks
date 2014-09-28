package com.moltendorf.bukkit.spawntweaks;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.Map;

/**
 * Created by moltendorf on 14/09/10.
 */
public class Listeners implements Listener {

	final protected Plugin plugin;

	protected Listeners(final Plugin instance) {
		plugin = instance;
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void CreatureSpawnEventListener(CreatureSpawnEvent event) {
		EntityType type = event.getEntityType();

		if (plugin.configuration.global.limitedCreatures.contains(type)) {
			Location location = event.getLocation();
			World world = location.getWorld();

			if (location.getWorld().getLivingEntities().size() >= 1000) {
				event.setCancelled(true);

				return;
			}

			if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
				Block block = location.getBlock();

				if (plugin.configuration.global.blockLight < block.getLightFromBlocks()) {
					event.setCancelled(true);

					return;
				}

//				if (plugin.configuration.global.skyLight < block.getLightFromSky()) {
//					event.setCancelled(true);
//
//					return;
//				}

				Biome biome = world.getBiome(location.getBlockX(), location.getBlockZ());
				BiomeConfig biomeConfig = plugin.configuration.global.biomes.get(biome);

				if (biomeConfig != null && biomeConfig.search.contains(type)) {
					double roll = Math.random() * biomeConfig.maxRoll;

					for (Map.Entry<EntityType, Double> entry : biomeConfig.replace.entrySet()) {
						if (roll < entry.getValue()) {
							event.setCancelled(true);

							world.spawnEntity(location, entry.getKey());

							return;
						}
					}
				}
			}
		}
	}
}
