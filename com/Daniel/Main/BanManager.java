package com.Daniel.Main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class BanManager {

	public FileConfiguration banData;
	public File banFile;
	
	
	

	public void setup() {
		if (!Main.plugin.getDataFolder().exists()) {
			Main.plugin.getDataFolder().mkdir();
		}

		banFile = new File(Main.plugin.getDataFolder(), "bans.yml");

		if (!banFile.exists()) {
			try {
				banFile.createNewFile();
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "The bans.yml file has been created");
			} catch (IOException e) {
				Bukkit.getServer().getConsoleSender()
						.sendMessage(ChatColor.RED + "Could not create the bans.yml file");
			}
		}

		banData = YamlConfiguration.loadConfiguration(banFile);
	}

	public void reloadBans() {
		banData = YamlConfiguration.loadConfiguration(banFile);
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.BLUE + "The bans.yml file has been reload");

	}
	
	
	
	
	public void saveBans() {
		List<String> ipbans = new ArrayList<String>();
		for(String IP : Main.IPbans) {
			ipbans.add(IP);
		}
		
		banData.set("Banned-IPs", ipbans);
		
		
		
		banData.set("UUID-Ban", null);

		banData.set("Nick-Ban", null);
		
		for(String uuid : Main.UUIDbans.keySet()) {
	
			BannedPlayer ban = Main.UUIDbans.get(uuid);
			
			banData.set("UUID-Ban." + uuid + ".IP" , ban.ip);
			banData.set("UUID-Ban." + uuid + ".Nick" , ban.name);
			banData.set("UUID-Ban." + uuid + ".Reason" , ban.reason);

		}
		
		
		for(String nick : Main.nickBans.keySet()) {
			
			BannedPlayer ban = Main.nickBans.get(nick);
			
			banData.set("Nick-Ban." + nick + ".IP" , ban.ip);
			banData.set("Nick-Ban." + nick + ".UUID" , ban.uuid);
			banData.set("Nick-Ban." + nick + ".Reason" , ban.reason);

		}
		
		
		
		try {
			banData.save(banFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
