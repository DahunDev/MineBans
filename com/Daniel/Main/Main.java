package com.Daniel.Main;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.Daniel.Main.Command.BanCommand;
import com.earth2me.essentials.Essentials;

public class Main extends JavaPlugin

		implements Listener {
	public final Logger logger = Logger.getLogger("Minecraft");
	public static Main plugin;

	//public static File dataFile;
	// public static FileConfiguration data;
	public static HashMap<String, BannedPlayer> UUIDbans;
	public static HashMap<String, BannedPlayer> nickBans;

	public static HashSet<String> IPbans;

	public static BanManager banConfig;

	public static int autoSave;

	public boolean UseUUIDBan = true;
	public boolean UseNicknameBan = false;

	// FileConfiguration data;

	// protected boolean canUseBanCMD = false;
	public static Essentials essentials;

	protected String BannedMsg = "��8[��b�ý��ۡ�8] ��c��������, ���� ���� �Ұ����մϴ�. "
			+ "\n ��a������ ��ǰ�̽ð� ��Ī�� �ҹ����� ������ �� ��� ���� ī�信 ��ǰ ���� �� ������ ��û���� �÷��ּ���. \n ��6ī���ּ�: ��ahttps://cafe.naver.com/ju0625";
	// protected List<String> crashnicknames;

	public void onDisable() {
		PluginDescriptionFile pdFile = this.getDescription();
		banConfig.saveBans();

		System.out.println(
				String.valueOf(String.valueOf(pdFile.getName())) + " " + pdFile.getVersion() + " ��(��) ��Ȱ��ȭ �Ǿ����ϴ�.");
	}

	public void onEnable() {

		PluginDescriptionFile pdFile = this.getDescription();
		Bukkit.getPluginManager().registerEvents((Listener) this, (Plugin) this);

		plugin = this;

		banConfig = new BanManager();

		banConfig.setup();

		Plugin essentialsPlugin = Bukkit.getPluginManager().getPlugin("Essentials");

		Main.essentials = (Essentials) essentialsPlugin;

		UUIDbans = new HashMap<String, BannedPlayer>();
		nickBans = new HashMap<String, BannedPlayer>();

		reloadConfiguration();
		loadBansYML();

		getCommand("MineBans").setExecutor(new BanCommand());
		System.out.println(
				String.valueOf(String.valueOf(pdFile.getName())) + " " + pdFile.getVersion() + " ��(��) Ȱ��ȭ �Ǿ����ϴ�.");
	}

	public boolean isBannedIP(String args) {
		if (IPbans.contains(args)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isBannedNick(String args) {
		if (this.UseNicknameBan && nickBans.keySet().contains(args)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isBannedUUID(String args) {

		if (this.UseUUIDBan && UUIDbans.keySet().contains(args)) {
			return true;
		} else {
			return false;
		}
	}

	public void reloadConfiguration() {
		PluginDescriptionFile pdFile = this.getDescription();
		File config = new File("plugins/" + pdFile.getName() + "/config.yml");
		if (config.exists()) {
			YamlConfiguration cfg = YamlConfiguration.loadConfiguration(config);
			this.saveDefaultConfig();
			for (String key : cfg.getConfigurationSection("").getKeys(true)) {
				if (!this.getConfig().contains(key)) {
					this.getConfig().set(key, cfg.get(key));
				}
			}
		} else {
			this.saveDefaultConfig();
		}
		this.reloadConfig();

		this.UseNicknameBan = this.getConfig().getBoolean("UseNicknameBan");
		this.UseUUIDBan = this.getConfig().getBoolean("UseUUIDBan");

		// this.canUseBanCMD = this.getConfig().getBoolean("canUseBanCMD");

		if (!getConfig().getStringList("BannedMsg").isEmpty()) {
			BannedMsg = "";
			for (String line : getConfig().getStringList("BannedMsg")) {
				this.BannedMsg = (this.BannedMsg + ChatColor.translateAlternateColorCodes('&', line) + "\n");
			}

		}

		autoSave = getConfig().getInt("auto-save", 90);

		if (autoSave < 1) {
			System.out.println("auto-save ���� 1�̻��� �������� �մϴ�. (���� ��)");
			autoSave = 90;
		}
		AutoSaver.register(this);

	}

	// ���ε� ��ɾ� ����, IPBan ����� �������, UUID�� �����Ȱ��� �ٽ� ������
	public void loadBansYML() {

		banConfig.reloadBans();

		Main.UUIDbans.clear();

		Main.nickBans.clear();

		for(String IP : banConfig.banData.getStringList("Banned-IPs")) {
			Main.IPbans.add(IP);
		}
	

		if (banConfig.banData.getConfigurationSection("UUID-Ban") == null) {
			banConfig.banData.createSection("UUID-Ban");

		}

		if (banConfig.banData.getConfigurationSection("Nick-Ban") == null) {
			banConfig.banData.createSection("Nick-Ban");

		}

		if (banConfig.banData.getConfigurationSection("Banned-IPs") == null) {
			banConfig.banData.createSection("Banned-IPs");
		}

		ConfigurationSection uuidSection = banConfig.banData.getConfigurationSection("UUID-Ban");

		for (String uuid : uuidSection.getKeys(false)) {
			BannedPlayer ban = new BannedPlayer();
			ban.uuid = uuid;
			ban.ip = uuidSection.getString(uuid + ".IP");
			ban.name = uuidSection.getString(uuid + ".Nick").toLowerCase();
			ban.reason = uuidSection.getString(uuid + ".Reason");

			UUIDbans.put(uuid, ban);

		}

		ConfigurationSection nickSection = banConfig.banData.getConfigurationSection("Nick-Ban");

		for (String nick : nickSection.getKeys(false)) {
			BannedPlayer ban = new BannedPlayer();
			ban.uuid = nickSection.getString(nick + ".UUID");
			ban.ip = nickSection.getString(nick + ".IP");
			ban.name = nick.toLowerCase();
			ban.reason = nickSection.getString(nick + ".Reason");

			nickBans.put(nick.toLowerCase(), ban);

		}

		// IPbans = data.getStringList("Banned-IPs");

	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPreLogin(AsyncPlayerPreLoginEvent event) {
		String player = event.getName().toLowerCase();

		String ip = event.getAddress().toString().replaceAll("/", "");
		String uuid = event.getUniqueId().toString();

		System.out.println("[MineBans] " + player + " ������: " + ip);

		if (player.equalsIgnoreCase("con") || (player.equalsIgnoreCase("prn")) || player.equalsIgnoreCase("aux")
				|| (player.equalsIgnoreCase("nul")) || player.equalsIgnoreCase("com1")
				|| (player.equalsIgnoreCase("com2")) || player.equalsIgnoreCase("com3")
				|| (player.equalsIgnoreCase("com4")) || player.equalsIgnoreCase("com5")
				|| (player.equalsIgnoreCase("com6")) || player.equalsIgnoreCase("com7")
				|| (player.equalsIgnoreCase("com8")) || player.equalsIgnoreCase("com9")
				|| (player.equalsIgnoreCase("LPT1")) || player.equalsIgnoreCase("LPT2")
				|| (player.equalsIgnoreCase("LPT3")) || player.equalsIgnoreCase("LPT4")
				|| (player.equalsIgnoreCase("LPT5")) || player.equalsIgnoreCase("LPT6")
				|| (player.equalsIgnoreCase("LPT7")) || player.equalsIgnoreCase("LPT8")
				|| (player.equalsIgnoreCase("LPT9"))

		) {
			event.setKickMessage("��8[��b�ý��ۡ�8] ��c������ �߻��Ͽ����ϴ�. �ٸ��г������� �����Ͻñ� �ٶ��ϴ�.");
			event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);

		} else if (player.contains("_co_kr") || player.contains("_kro_kr") || player.contains("_org")
				|| player.contains("_or_kr") || player.contains("_pe_kr") || player.contains("rendogkr_")
				|| player.contains("_zz_am")) {
			event.setKickMessage("��8[��b�ý��ۡ�8] ��c�������� �г��� �Դϴ�. �ٸ��г������� �����Ͻñ� �ٶ��ϴ�.");
			event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
		} else if (isBannedIP(ip) || isBannedNick(player) || isBannedUUID(uuid)) {
			event.setKickMessage(BannedMsg);
			event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
		}
	}

}
