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

	String banHelp = "��b��l[ ��f��lServer ��b��l] ��f/MineBans ban <�г���> <����> : ��7<�г���>�� IP�� �ش� ���� ��������";
	String unbanHelp = "��b��l[ ��f��lServer ��b��l] ��f/MineBans unban <�г���/uuid> : ��7<�г��� �Ǵ� uuid> ���� �� �ش� IP ���� ����, uuid�� - ����";
	String banIPHelp = "��b��l[ ��f��lServer ��b��l] ��f/MineBans banIP <IP> : ��7<IP> ��������";
	String unbanIPHelp = "��b��l[ ��f��lServer ��b��l] ��f/MineBans unBanIP <IP> : ��7IP ���� ����";
	String saveHelp = "��b��l[ ��f��lServer ��b��l] ��f/MineBans save : ��7���� ����";
	String reloadConfigHelp = "��b��l[ ��f��lServer ��b��l] ��f/MineBans �������ε� : ��7���� ��ε�";
	String reloadDataHelp = "��b��l[ ��f��lServer ��b��l] ��f/MineBans �����͸��ε� : ��7������ YML���� �ٽ� �ҷ����� (������� ���� ��� �����";

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
							sender.sendMessage("��b��l[ ��f��lServer ��b��l] ��f�굥���� ����Ϸ�");

						}
					}.runTaskAsynchronously(Main.plugin);

				}

				else if (args[0].equalsIgnoreCase("�������ε�")) {

					new BukkitRunnable() {

						@Override
						public void run() {
							PluginDescriptionFile pdFile = Main.plugin.getDescription();
							Main.plugin.reloadConfiguration();
							sender.sendMessage(
									"��b��l[ ��f��lServer ��b��l] ��c��l" + pdFile.getName() + "������ ���ε� �Ǿ����ϴ�.");
						}
					}.runTaskAsynchronously(Main.plugin);

				}

				else if (args[0].equalsIgnoreCase("�����͸��ε�")) {

					new BukkitRunnable() {

						@Override
						public void run() {
							Main.plugin.loadBansYML();
							sender.sendMessage("��b��l[ ��f��lServer ��b��l] ��c��l�굥���Ͱ� ���ε� �Ǿ����ϴ�.");
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

									IP = "�˼�����";
									sender.sendMessage(
											"��b��l[ ��f��lServer ��b��l] ��c�ش� �÷��̾�� ������ ������ ���� ���� �����ǰ� �˼��������� ó����");

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

								if (!(IP.contains("�˼�����") || Main.IPbans.contains(IP))) {
									Main.IPbans.add(IP);
								}

								sender.sendMessage(
										"��b��l[ ��f��lServer ��b��l] ��c" + name + "(������:" + IP + ") ���� �������܉���ϴ�.");

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

										sender.sendMessage("��b��l[ ��f��lServer ��b��l] ��cUUID: " + args[1] + "(������:"
												+ IP + " Nick:" + player.name + ") ���� ������������ϴ�.");

									} else {
										sender.sendMessage(
												"��b��l[ ��f��lServer ��b��l] ��c" + args[1] + " �� UUID ���ܸ�Ͽ� �����ϴ�.");

									}

								} else {
									if (Main.plugin.UseNicknameBan) {

										BannedPlayer player = Main.nickBans.get(args[1].toLowerCase());

										if (player != null) {
											String IP = player.ip;

											Main.IPbans.remove(IP);

											Main.nickBans.remove(args[1].toLowerCase());

											sender.sendMessage("��b��l[ ��f��lServer ��b��l] ��c" + player.name + "(������:"
													+ IP + ") ���� ������������ϴ�.");
										} else {

											sender.sendMessage("��b��l[ ��f��lServer ��b��l] ��c" + args[1]
													+ " ���� �г��� ������ �Ǿ� ���� �ʽ��ϴ�.");
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

											sender.sendMessage("��b��l[ ��f��lServer ��b��l] ��c" + player.name + "(������:"
													+ IP + " UUID:" + player.uuid + ") ���� ������������ϴ�.");

										} else {
											sender.sendMessage("��b��l[ ��f��lServer ��b��l] ��c" + args[1]
													+ " ���� UUID ������ �Ǿ� ���� �ʽ��ϴ�.");

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
										sender.sendMessage("��b��l[ ��f��lServer ��b��l] ��c�̹� ���ܵǾ� ����");
									} else {

										Main.IPbans.add(args[1]);

										sender.sendMessage(
												"��b��l[ ��f��lServer ��b��l] ��cIP �ּ� " + args[1] + " �� ����������");

									}

								} else {
									sender.sendMessage("��b��l[ ��f��lServer ��b��l] ��c������ �����Ǹ� �Է����ּ���.");
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

									sender.sendMessage("��b��l[ ��f��lServer ��b��l] ��cIP �ּ� " + args[1] + " �� ����������");

									Main.IPbans.remove(args[1]);

								} else {

									sender.sendMessage(
											"��b��l[ ��f��lServer ��b��l] ��cIP �ּ� " + args[1] + " �� ���ܵǾ� ���� �ʽ��ϴ�.");
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
			list.add("�����͸��ε�");
			list.add("�������ε�");
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
