package net.coasterman10.Annihilation.listeners;

import net.coasterman10.Annihilation.Annihilation;
import net.coasterman10.Annihilation.teams.Team;
import net.coasterman10.Annihilation.teams.TeamManager;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
	private final TeamManager teamManager;
	private final Annihilation plugin;

	public ChatListener(Annihilation plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
		teamManager = plugin.getTeamManager();
	}

	@EventHandler
	public void onPlayerChat(final AsyncPlayerChatEvent e) {
		String GRAY = ChatColor.GRAY.toString();
		String WHITE = ChatColor.WHITE.toString();

		String username = e.getPlayer().getName();
		Team team = teamManager.getTeamWithPlayer(username);
		String group;
		String message = e.getMessage();

		if (team == null) {
			String color = ChatColor.DARK_PURPLE.toString();
			group = GRAY + "[" + color + "Lobby" + GRAY + "]";

			if (message.startsWith("!"))
				message = message.substring(1);
		} else {
			String color = team.getPrefix();
			if (message.startsWith("!")) {
				message = message.substring(1);
				group = GRAY + "[" + color + "All" + GRAY + "]";
				username = color + username;
			} else {
				group = GRAY + "[" + color + "Team" + GRAY + "]";
				e.getRecipients().clear();
				e.getRecipients().addAll(team.getPlayers());
			}
		}

		e.setFormat(group + " " + WHITE + username + WHITE + ": " + message);

		// HA HA HA HA HA!!!!!
		if (message.equalsIgnoreCase("NEXUS")) {
			plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
				public void run() {
					Player player = e.getPlayer();
					player.getWorld().strikeLightning(
							e.getPlayer().getLocation());
					player.sendMessage(ChatColor.DARK_RED
							+ "Instead of shouting at your team, why don't you go defend?");
				}
			});
		}
	}
}
