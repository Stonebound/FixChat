package com.codeski.betterchat;

import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.dynmap.DynmapCommonAPI;

import com.google.common.base.Joiner;

public class BetterChat extends JavaPlugin implements Listener
{
	public enum Achievement {
		ACQUIRE_IRON("Acquire Hardware"),
		BAKE_CAKE("The Lie"),
		BOOKCASE("Librarian"),
		BREED_COW("Repopulation"),
		BREW_POTION("Local Brewery"),
		BUILD_BETTER_PICKAXE("Getting an Upgrade"),
		BUILD_FURNACE("Hot Topic"),
		BUILD_HOE("Time to Farm!"),
		BUILD_PICKAXE("Time to Mine!"),
		BUILD_SWORD("Time to Strike!"),
		BUILD_WORKBENCH("Benchmarking"),
		COOK_FISH("Delicious Fish"),
		DIAMONDS_TO_YOU("Diamonds to you!"),
		ENCHANTMENTS("Enchanter"),
		END_PORTAL("The End?"),
		EXPLORE_ALL_BIOMES("Adventuring Time"),
		FLY_PIG("When Pigs Fly"),
		FULL_BEACON("Beaconator"),
		GET_BLAZE_ROD("Into Fire"),
		GET_DIAMONDS("DIAMONDS!"),
		GHAST_RETURN("Return to Sender"),
		KILL_COW("Cow Tipper"),
		KILL_ENEMY("Monster Hunter"),
		KILL_WITHER("The Beginning."),
		MAKE_BREAD("Bake Bread"),
		MINE_WOOD("Getting Wood"),
		NETHER_PORTAL("We Need to Go Deeper"),
		ON_A_RAIL("On A Rail"),
		OPEN_INVENTORY("Taking Inventory"),
		OVERKILL("Overkill"),
		SNIPE_SKELETON("Sniper Duel"),
		SPAWN_WITHER("The Beginning?"),
		THE_END("The End.");
		//
		private final String name;

		private Achievement(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

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
				sender.sendMessage("The console cannot receive whispers which means it cannot reply.");
				return true;
			} else if (args.length < 1) {
				sender.sendMessage(ChatColor.RED + "Usage: /" + cmd.getName() + " <message>");
				return true;
			} else if (reply.get(from) == null) {
				sender.sendMessage("There's no whisper to reply to.");
				return true;
			} else {
				reply.put(reply.get(from), from);
				server.dispatchCommand(from, "tell " + reply.get(from).getName() + " " + Joiner.on(' ').join(args));
				return true;
			}
		return false;
	}

	@Override
	public void onEnable() {
		server = this.getServer();
		server.getPluginManager().registerEvents(this, this);
	}

	@EventHandler
	public void onPlayerAchievement(PlayerAchievementAwardedEvent event) {
		if (Bukkit.getPluginManager().isPluginEnabled("dynmap"))
			((DynmapCommonAPI) Bukkit.getPluginManager().getPlugin("dynmap")).sendBroadcastToWeb(null, event.getPlayer().getName() + " has just earned the achievement [" + Achievement.valueOf(event.getAchievement().name()) + "]");
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
		} else if (Bukkit.getPluginManager().isPluginEnabled("dynmap") && msg.length > 1 && msg[0].equalsIgnoreCase("/say") && event.getPlayer().hasPermission("bukkit.command.say"))
			((DynmapCommonAPI) Bukkit.getPluginManager().getPlugin("dynmap")).sendBroadcastToWeb(event.getPlayer().getName(), Joiner.on(' ').join(Arrays.copyOfRange(msg, 1, msg.length)));
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (Bukkit.getPluginManager().isPluginEnabled("dynmap"))
			((DynmapCommonAPI) Bukkit.getPluginManager().getPlugin("dynmap")).sendBroadcastToWeb(null, event.getDeathMessage());
	}

	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent event) {
		if (event.getPlayer().hasPermission("bukkit.command.list"))
			new BukkitRunnable() {
				@Override
				public void run() {
					server.dispatchCommand(event.getPlayer(), "list");
				}
			}.runTaskLater(this, 1);
	}

	@EventHandler
	public void onServerCommand(ServerCommandEvent event) {
		String[] msg = event.getCommand().split("\\s+");
		if (Bukkit.getPluginManager().isPluginEnabled("dynmap") && msg.length > 1 && msg[0].equalsIgnoreCase("say"))
			((DynmapCommonAPI) Bukkit.getPluginManager().getPlugin("dynmap")).sendBroadcastToWeb("Server", Joiner.on(' ').join(Arrays.copyOfRange(msg, 1, msg.length)));
	}
}
