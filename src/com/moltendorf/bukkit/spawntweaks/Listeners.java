package com.moltendorf.bukkit.spawntweaks;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitScheduler;

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

		final ConsoleCommandSender console = plugin.getServer().getConsoleSender();

		plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
			time = System.currentTimeMillis();

			plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
				final long currentTime = System.currentTimeMillis();
				final double currentTicks = 5.*20.*1000./(currentTime - time);

				if (currentTicks >= plugin.configuration.global.ticks && ticks < plugin.configuration.global.ticks) {
					console.sendMessage("§2Spawning has resumed! TPS is over " + plugin.configuration.global.ticks + ".");
				} else if (currentTicks < plugin.configuration.global.ticks && ticks >= plugin.configuration.global.ticks) {
					console.sendMessage("§4Spawning has been paused! TPS is below " + plugin.configuration.global.ticks + ".");
				}

				time = currentTime;
				ticks = currentTicks;
			}, 5*20, 5*20);
		}, 30*20);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void CreatureSpawnEventListener(final CreatureSpawnEvent event) {
		final Location location = event.getLocation();
		final CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();
		final EntityType type = event.getEntityType();
		final World world = location.getWorld();

		final HashSet spawnReasons = plugin.configuration.global.spawnReasons;

		if (spawnReasons.contains(reason)) {
			if (ticks < plugin.configuration.global.ticks) {
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

		if (reason == CreatureSpawnEvent.SpawnReason.NATURAL) {
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

		// 2.5x more spawner mobs.
		if (ticks > 18 && reason == CreatureSpawnEvent.SpawnReason.SPAWNER) {
			final int roll = (int) (Math.random() * 4.);

			final BukkitScheduler scheduler = plugin.getServer().getScheduler();

			for (int i = 0; i < roll; ++i) {
				scheduler.scheduleSyncDelayedTask(plugin, () -> world.spawnEntity(location, type), (i + 1) * 20);
			}
		}
	}
}
