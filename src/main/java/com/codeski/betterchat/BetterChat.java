package com.codeski.betterchat;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.base.Joiner;

public class BetterChat extends JavaPlugin implements Listener
{
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
				Player to = server.getPlayer(args[0]);
				if (from != null && to != null)
					reply.put(to, from);
				server.dispatchCommand(from, "tell " + Joiner.on(' ').join(args));
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
	public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
		String[] msg = event.getMessage().split("\\s+");
		if (msg.length > 2 && msg[0].equalsIgnoreCase("/tell") && server.getPlayer(msg[1]) != null)
			reply.put(server.getPlayer(msg[1]), event.getPlayer());
	}
}
