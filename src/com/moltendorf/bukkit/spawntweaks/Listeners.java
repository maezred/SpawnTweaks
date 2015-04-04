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

import java.util.HashSet;
import java.util.Map;

/**
 * Created by moltendorf on 14/09/10.
 *
 * @author moltendorf
 */
public class Listeners implements Listener {

	final protected Plugin plugin;

	protected double ticks = 20;
	protected long time;

	protected Listeners(final Plugin instance) {
		plugin = instance;

		time = System.currentTimeMillis();

		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
			long currentTime = System.currentTimeMillis();
			double currentTicks = (currentTime - time) / 20L;

			if (currentTicks >= 18 && ticks < 18) {
				System.out.println("§2Spawning has resumed!");
			} else if (currentTicks >= 18 && ticks < 18) {
				System.out.println("§4Spawning has been paused!");
			}

			time = currentTime;
			ticks = currentTicks;
		}, 5 * 20, 5 * 20);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void CreatureSpawnEventListener(final CreatureSpawnEvent event) {
		final Location location = event.getLocation();
		final CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();
		final EntityType type = event.getEntityType();
		final World world = location.getWorld();

		final HashSet spawnReasons = plugin.configuration.global.spawnReasons;

		if (spawnReasons.contains(reason)) {
			if (ticks >= 18) {
				final HashSet breedingCreatures = plugin.configuration.global.breedingCreatures;

				if (reason != CreatureSpawnEvent.SpawnReason.BREEDING || breedingCreatures.contains(type)) {
					event.setCancelled(true);

					return;
				}
			}

			final Block block = location.getBlock();

			final int blockLightLimit = plugin.configuration.global.blockLight;
			final HashSet limitedCreatures = plugin.configuration.global.limitedCreatures;

			if (blockLightLimit < block.getLightFromBlocks() && limitedCreatures.contains(type)) {
				event.setCancelled(true);

				return;
			}
		}

		final Biome biome = world.getBiome(location.getBlockX(), location.getBlockZ());
		final BiomeConfig biomeConfig = plugin.configuration.global.biomes.get(biome);

		if (biomeConfig != null && biomeConfig.search.contains(type)) {
			final double roll = Math.random() * biomeConfig.maxRoll;

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
