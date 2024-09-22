package com.Daniel.Main.Command;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.scheduler.BukkitRunnable;

import com.Daniel.Main.BannedPlayer;
import com.Daniel.Main.Main;
import com.earth2me.essentials.User;

public class BanCommand implements CommandExecutor, TabCompleter {

	private String removeLastChar(String str) {
		if (str == null || str.length() == 0) {
			return str;
		}
		return str.substring(0, str.length() - 1);
	}

	private static final String IPV4_REGEX = "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\."
			+ "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." + "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\."
			+ "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

	private static final Pattern IPV4_PATTERN = Pattern.compile(IPV4_REGEX);

	public static boolean isValidInet4Address(String ip) {
		if (ip == null) {
			return false;
		}

		Matcher matcher = IPV4_PATTERN.matcher(ip);

		return matcher.matches();
	}

	String banHelp = "§b§l[ §f§lServer §b§l] §f/MineBans ban <닉네임> <사유> : §7<닉네임>을 IP및 해당 계정 영구차단";
	String unbanHelp = "§b§l[ §f§lServer §b§l] §f/MineBans unban <닉네임/uuid> : §7<닉네임 또는 uuid> 계정 및 해당 IP 차단 해제, uuid는 - 포함";
	String banIPHelp = "§b§l[ §f§lServer §b§l] §f/MineBans banIP <IP> : §7<IP> 영구차단";
	String unbanIPHelp = "§b§l[ §f§lServer §b§l] §f/MineBans unBanIP <IP> : §7IP 차단 해제";
	String saveHelp = "§b§l[ §f§lServer §b§l] §f/MineBans save : §7밴목록 저장";
	String reloadConfigHelp = "§b§l[ §f§lServer §b§l] §f/MineBans 설정리로드 : §7설정 재로드";
	String reloadDataHelp = "§b§l[ §f§lServer §b§l] §f/MineBans 데이터리로드 : §7밴목록을 YML에서 다시 불러오기 (저장되지 않은 목록 사라짐";

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String commandLabel,
			final String[] args) {

		if (sender.hasPermission("MineBans.Admin")) {

			if (args.length >= 1) {

				if (args[0].equalsIgnoreCase("save")) {

					new BukkitRunnable() {

						@Override
						public void run() {
							Main.banConfig.saveBans();
							sender.sendMessage("§b§l[ §f§lServer §b§l] §f밴데이터 저장완료");

						}
					}.runTaskAsynchronously(Main.plugin);

				}

				else if (args[0].equalsIgnoreCase("설정리로드")) {

					new BukkitRunnable() {

						@Override
						public void run() {
							PluginDescriptionFile pdFile = Main.plugin.getDescription();
							Main.plugin.reloadConfiguration();
							sender.sendMessage(
									"§b§l[ §f§lServer §b§l] §c§l" + pdFile.getName() + "설정이 리로드 되었습니다.");
						}
					}.runTaskAsynchronously(Main.plugin);

				}

				else if (args[0].equalsIgnoreCase("데이터리로드")) {

					new BukkitRunnable() {

						@Override
						public void run() {
							Main.plugin.loadBansYML();
							sender.sendMessage("§b§l[ §f§lServer §b§l] §c§l밴데이터가 리로드 되었습니다.");
						}
					}.runTaskAsynchronously(Main.plugin);

				}

				else if (args[0].equalsIgnoreCase("ban")) {

					if (args.length > 2) {

						new BukkitRunnable() {

							@Override
							public void run() {

								String uuid;
								String IP;
								String name;
								String Reason;

								User essUser = Main.essentials.getOfflineUser(args[1]);

								if (essUser != null) {

									uuid = essUser.getConfigUUID().toString();
									IP = essUser.getLastLoginAddress();
									name = essUser.getName();

								} else {
									OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
									//getOfflinePlayer

									uuid = player.getUniqueId().toString();
									name = player.getName();

									IP = "알수없음";
									sender.sendMessage(
											"§b§l[ §f§lServer §b§l] §c해당 플레이어는 서버에 접속한 적이 없어 아이피가 알수없음으로 처리됨");

								}

								StringBuilder str = new StringBuilder();
								for (int i = 2; i < args.length; i++) {
									str.append(args[i] + " ");
								}

								String msg = str.toString();
								msg = removeLastChar(msg);
								msg = Matcher.quoteReplacement(msg);

								Reason = msg;

								BannedPlayer ban = new BannedPlayer();

								ban.setIP(IP);
								ban.setName(name);
								ban.setReason(Reason);
								ban.setUUID(uuid);

								if (Main.plugin.UseUUIDBan) {

									Main.UUIDbans.put(uuid, ban);

								}

								if (Main.plugin.UseNicknameBan) {
									Main.nickBans.put(name.toLowerCase(), ban);
								}

								if (!(IP.contains("알수없음") || Main.IPbans.contains(IP))) {
									Main.IPbans.add(IP);
								}

								sender.sendMessage(
										"§b§l[ §f§lServer §b§l] §c" + name + "(아이피:" + IP + ") 님이 영구차단됬습니다.");

							}

						}.runTaskAsynchronously(Main.plugin);
					} else {
						sender.sendMessage(banHelp);
					}

				}

				else if (args[0].equalsIgnoreCase("unban")) {

					new BukkitRunnable() {

						@Override
						public void run() {
							if (args.length == 2) {

								if (args[1].length() > 16) {

									BannedPlayer player = Main.UUIDbans.get(args[1]);

									if (player != null) {
										String IP = player.ip;

										Main.IPbans.remove(IP);

										Main.UUIDbans.remove(args[1]);

										sender.sendMessage("§b§l[ §f§lServer §b§l] §cUUID: " + args[1] + "(아이피:"
												+ IP + " Nick:" + player.name + ") 님이 차단해제됬습니다.");

									} else {
										sender.sendMessage(
												"§b§l[ §f§lServer §b§l] §c" + args[1] + " 는 UUID 차단목록에 없습니다.");

									}

								} else {
									if (Main.plugin.UseNicknameBan) {

										BannedPlayer player = Main.nickBans.get(args[1].toLowerCase());

										if (player != null) {
											String IP = player.ip;

											Main.IPbans.remove(IP);

											Main.nickBans.remove(args[1].toLowerCase());

											sender.sendMessage("§b§l[ §f§lServer §b§l] §c" + player.name + "(아이피:"
													+ IP + ") 님이 차단해제됬습니다.");
										} else {

											sender.sendMessage("§b§l[ §f§lServer §b§l] §c" + args[1]
													+ " 님은 닉네임 차단이 되어 있지 않습니다.");
										}

									}

									if (Main.plugin.UseUUIDBan) {

										User essUser = Main.essentials.getOfflineUser(args[1]);

										String uuid;

										if (essUser != null) {

											uuid = essUser.getConfigUUID().toString();

										} else {
											OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);

											uuid = player.getUniqueId().toString();

										}
										BannedPlayer player = Main.UUIDbans.get(uuid);

										if (player != null) {
											String IP = player.ip;

											Main.IPbans.remove(IP);

											Main.UUIDbans.remove(uuid);

											sender.sendMessage("§b§l[ §f§lServer §b§l] §c" + player.name + "(아이피:"
													+ IP + " UUID:" + player.uuid + ") 님이 차단해제됬습니다.");

										} else {
											sender.sendMessage("§b§l[ §f§lServer §b§l] §c" + args[1]
													+ " 님은 UUID 차단이 되어 있지 않습니다.");

										}

									}
								}

							} else {
								sender.sendMessage(unbanHelp);

							}

						}
					}.runTaskAsynchronously(Main.plugin);

				}

				else if (args[0].equalsIgnoreCase("banIP")) {

					new BukkitRunnable() {

						@Override
						public void run() {

							if (args.length == 2) {

								if (isValidInet4Address(args[1])) {
									if (Main.IPbans.contains(args[1])) {
										sender.sendMessage("§b§l[ §f§lServer §b§l] §c이미 차단되어 있음");
									} else {

										Main.IPbans.add(args[1]);

										sender.sendMessage(
												"§b§l[ §f§lServer §b§l] §cIP 주소 " + args[1] + " 를 영구차단함");

									}

								} else {
									sender.sendMessage("§b§l[ §f§lServer §b§l] §c유요한 아이피를 입력해주세요.");
								}

							} else {
								sender.sendMessage(banIPHelp);

							}

						}
					}.runTaskAsynchronously(Main.plugin);

				}

				else if (args[0].equalsIgnoreCase("unbanIP")) {

					new BukkitRunnable() {

						@Override
						public void run() {
							if (args.length == 2) {

								if (Main.IPbans.contains(args[1])) {

									sender.sendMessage("§b§l[ §f§lServer §b§l] §cIP 주소 " + args[1] + " 를 차단해제함");

									Main.IPbans.remove(args[1]);

								} else {

									sender.sendMessage(
											"§b§l[ §f§lServer §b§l] §cIP 주소 " + args[1] + " 는 차단되어 있지 않습니다.");
								}

							} else {
								sender.sendMessage(unbanIPHelp);

							}

						}
					}.runTaskAsynchronously(Main.plugin);

				}

			} else {

				sender.sendMessage(banHelp);
				sender.sendMessage(unbanHelp);
				sender.sendMessage(banIPHelp);
				sender.sendMessage(unbanIPHelp);
				sender.sendMessage(saveHelp);

				sender.sendMessage(reloadConfigHelp);
				sender.sendMessage(reloadDataHelp);

			}

		}

		return false;

	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> list = new ArrayList<>();

		if (args.length < 2) {

			list.add("ban");
			list.add("unban");
			list.add("unbanIP");
			list.add("banIP");
			list.add("데이터리로드");
			list.add("설정리로드");
			list.add("save");

	        String finalArg = args[args.length - 1];
	        Iterator<String> it = list.iterator();
	        while (it.hasNext()) {
	            if (!it.next().startsWith(finalArg)) {
	                it.remove();
	            }
	        }

	        return list;
		} else {
			return null; // Default completion

		}

	}
}
