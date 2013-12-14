package net.coasterman10.Annihilation;

import net.coasterman10.Annihilation.teams.Team;
import net.coasterman10.Annihilation.teams.TeamManager;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
    private final TeamManager teamManager;
    private final Annihilation plugin;;

    public ChatListener(Annihilation plugin, TeamManager teamManager) {
	plugin.getServer().getPluginManager().registerEvents(this, plugin);
	this.teamManager = teamManager;
	this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(final AsyncPlayerChatEvent e) {
	String GRAY = ChatColor.GRAY.toString();
	String WHITE = ChatColor.WHITE.toString();

	String username = e.getPlayer().getName();
	Team team = teamManager.getTeamWithPlayer(username);
	String color = team.getPrefix();
	String group;
	String message = e.getMessage();

	if (team.getName().equalsIgnoreCase("lobby")) {
	    group = GRAY + "[" + color + "Lobby" + GRAY + "]";

	    if (message.startsWith("!"))
		message = message.substring(1);
	} else {
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
	if (message.equals("NEXUS"))
	    plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
		public void run() {
		    e.getPlayer().getWorld()
			    .strikeLightning(e.getPlayer().getLocation());
		}
	    });
    }
}
