package com.codeski.fixchat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.dynmap.DynmapCommonAPI;
import org.kitteh.vanish.VanishCheck;
import org.kitteh.vanish.VanishManager;
import org.kitteh.vanish.VanishPlugin;

import com.codeski.fixchat.FixChat.Achievements;
import com.codeski.fixchat.FixChat.Strings;
import com.google.common.base.Joiner;

public class FixChatCB extends JavaPlugin implements Listener {
	private final ArrayList<Player> away = new ArrayList<Player>();
	private FileConfiguration configuration;
	private final HashMap<Player, Long> idle = new HashMap<Player, Long>();
	private final HashMap<Player, Long> knockback = new HashMap<Player, Long>();
	private final HashMap<Player, Player> reply = new HashMap<Player, Player>();
	private Server server;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player from = null;
		if (Player.class.isInstance(sender))
			from = (Player) sender;
		if (cmd.getName().equalsIgnoreCase("t") || cmd.getName().equalsIgnoreCase("whisper") || cmd.getName().equalsIgnoreCase("w"))
			if (args.length < 2) {
				sender.sendMessage(ChatColor.RED + "Usage: /" + cmd.getName() + " <player> <message>");
				return true;
			} else {
				Player to = null;
				for (Player p : server.getOnlinePlayers())
					if (p.getName().equalsIgnoreCase(args[0]))
						to = p;
				if (from != null && to != null)
					reply.put(to, from);
				server.dispatchCommand(from != null ? from : sender, "tell " + Joiner.on(' ').join(args));
				return true;
			}
		else if (cmd.getName().equalsIgnoreCase("reply") || cmd.getName().equalsIgnoreCase("r"))
			if (from == null) {
				sender.sendMessage(Strings.NON_PLAYER_REPLY.toString());
				return true;
			} else if (args.length < 1) {
				sender.sendMessage(ChatColor.RED + "Usage: /" + cmd.getName() + " <message>");
				return true;
			} else if (reply.get(from) == null) {
				sender.sendMessage(Strings.NO_WHISPER_REPLY.toString());
				return true;
			} else {
				reply.put(reply.get(from), from);
				server.dispatchCommand(from, "tell " + reply.get(from).getName() + " " + Joiner.on(' ').join(args));
				return true;
			}
		else if (cmd.getName().equalsIgnoreCase("motd"))
			if (args.length < 1) {
				sender.sendMessage(ChatColor.RED + "Usage: /" + cmd.getName() + " <message>");
				return true;
			} else {
				configuration.set("motd", Joiner.on(' ').join(args));
				this.saveConfig();
				return true;
			}
		return false;
	}

	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		configuration = this.getConfig();
		configuration.options().copyDefaults(true);
		this.saveConfig();
		server = this.getServer();
		server.getPluginManager().registerEvents(this, this);
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers())
					if (idle.get(p) != null && !away.contains(p))
						if (System.currentTimeMillis() - idle.get(p) > FixChat.AWAY) {
							if (Bukkit.getPluginManager().isPluginEnabled("VanishNoPacket")) {
								VanishManager vanish = ((VanishPlugin) Bukkit.getPluginManager().getPlugin("VanishNoPacket")).getManager();
								if (vanish != null)
									if ((Boolean) new VanishCheck(vanish, p.getName()).call()) {
										Bukkit.broadcastMessage("Player is vanished.");
										return;
									}
							}
							away.add(p);
							Bukkit.broadcastMessage(ChatColor.YELLOW + p.getName() + Strings.AWAY);
							if (Bukkit.getPluginManager().isPluginEnabled("dynmap"))
								((DynmapCommonAPI) Bukkit.getPluginManager().getPlugin("dynmap")).sendBroadcastToWeb(null, p.getName() + " is away from keyboard.");
						}
			}
		}.runTaskTimer(this, FixChat.INTERVAL, FixChat.INTERVAL);
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getEntityType() == EntityType.PLAYER)
			knockback.put((Player) event.getEntity(), System.currentTimeMillis());
	}

	@EventHandler
	public void onPlayerAchievement(PlayerAchievementAwardedEvent event) {
		if (Bukkit.getPluginManager().isPluginEnabled("dynmap"))
			((DynmapCommonAPI) Bukkit.getPluginManager().getPlugin("dynmap")).sendBroadcastToWeb(null, event.getPlayer().getName() + " has just earned the achievement [" + Achievements.valueOf(event.getAchievement().name()) + "]");
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		idle.put(event.getPlayer(), System.currentTimeMillis());
		if (away.contains(event.getPlayer())) {
			away.remove(event.getPlayer());
			Bukkit.broadcastMessage(ChatColor.YELLOW + event.getPlayer().getName() + Strings.NOT_AWAY);
			if (Bukkit.getPluginManager().isPluginEnabled("dynmap"))
				((DynmapCommonAPI) Bukkit.getPluginManager().getPlugin("dynmap")).sendBroadcastToWeb(null, event.getPlayer().getName() + " is no longer away from keyboard.");
		}
	}

	@EventHandler
	public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
		String[] msg = event.getMessage().split("\\s+");
		if (msg.length > 2 && msg[0].equalsIgnoreCase("/tell")) {
			Player to = null;
			for (Player p : server.getOnlinePlayers())
				if (p.getName().equalsIgnoreCase(msg[1]))
					to = p;
			if (to != null)
				reply.put(to, event.getPlayer());
		} else if (Bukkit.getPluginManager().isPluginEnabled("dynmap") && msg.length > 1 && msg[0].equalsIgnoreCase("/say") && event.getPlayer().hasPermission("minecraft.command.say"))
			((DynmapCommonAPI) Bukkit.getPluginManager().getPlugin("dynmap")).sendBroadcastToWeb(event.getPlayer().getName(), Joiner.on(' ').join(Arrays.copyOfRange(msg, 1, msg.length)));
		else if (Bukkit.getPluginManager().isPluginEnabled("dynmap") && msg.length > 1 && msg[0].equalsIgnoreCase("/me") && event.getPlayer().hasPermission("minecraft.command.me"))
			((DynmapCommonAPI) Bukkit.getPluginManager().getPlugin("dynmap")).sendBroadcastToWeb(null, "* " + event.getPlayer().getName() + " " + Joiner.on(' ').join(Arrays.copyOfRange(msg, 1, msg.length)));
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (Bukkit.getPluginManager().isPluginEnabled("dynmap"))
			((DynmapCommonAPI) Bukkit.getPluginManager().getPlugin("dynmap")).sendBroadcastToWeb(null, event.getDeathMessage());
	}

	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent event) {
		idle.put(event.getPlayer(), System.currentTimeMillis());
		new BukkitRunnable() {
			@Override
			public void run() {
				if (event.getPlayer().hasPermission("minecraft.command.list"))
					server.dispatchCommand(event.getPlayer(), "list");
				if (configuration.getString("motd") != null && configuration.getString("motd").length() > 0) {
					event.getPlayer().sendMessage("Message of the day:");
					event.getPlayer().sendMessage(configuration.getString("motd"));
				}
			}
		}.runTaskLater(this, 1);
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		idle.put(event.getPlayer(), System.currentTimeMillis());
		if (away.contains(event.getPlayer()) && (knockback.get(event.getPlayer()) == null || knockback.get(event.getPlayer()) < System.currentTimeMillis() - 3000)) {
			away.remove(event.getPlayer());
			Bukkit.broadcastMessage(ChatColor.YELLOW + event.getPlayer().getName() + Strings.NOT_AWAY);
			if (Bukkit.getPluginManager().isPluginEnabled("dynmap"))
				((DynmapCommonAPI) Bukkit.getPluginManager().getPlugin("dynmap")).sendBroadcastToWeb(null, event.getPlayer().getName() + " is no longer away from keyboard.");
		}
	}

	@EventHandler
	public void onPlayerQuit(final PlayerQuitEvent event) {
		idle.remove(event.getPlayer());
		away.remove(event.getPlayer());
	}

	@EventHandler
	public void onServerCommand(ServerCommandEvent event) {
		String[] msg = event.getCommand().split("\\s+");
		if (Bukkit.getPluginManager().isPluginEnabled("dynmap") && msg.length > 1 && msg[0].equalsIgnoreCase("say"))
			((DynmapCommonAPI) Bukkit.getPluginManager().getPlugin("dynmap")).sendBroadcastToWeb("Server", Joiner.on(' ').join(Arrays.copyOfRange(msg, 1, msg.length)));
	}
}
