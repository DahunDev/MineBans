package com.Daniel.Main;


import org.bukkit.Bukkit;

public class AutoSaver implements Runnable {

	public AutoSaver() {
	}

	public static void register(Main plugin) {

		Bukkit.getScheduler().cancelTasks(plugin);

		long period = (Main.autoSave * 20L);
		Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new AutoSaver(), period, period);
	}

	@Override
	public void run() {
		try {

			Main.banConfig.saveBans();

		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
}
