package net.coasterman10.Annihilation.bar;

import java.util.HashMap;

import net.coasterman10.Annihilation.Annihilation;
import net.coasterman10.Annihilation.util.SchedulerUtil;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class BarManager implements Listener {
    private HashMap<String, FakeDragon> players = new HashMap<String, FakeDragon>();

    public BarManager(Annihilation plugin) {
	plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    public void setMessageAndPercent(Player player, String message, float percent) {
	FakeDragon dragon = getDragon(player, message);
	if (message.length() > 64)
	    dragon.name = message.substring(0, 63);
	else
	    dragon.name = message;
	dragon.health = percent * FakeDragon.MAX_HEALTH;
	sendDragon(dragon, player);
    }

    public void setMessage(Player player, String message) {
	FakeDragon dragon = getDragon(player, message);
	if (message.length() > 64)
	    dragon.name = message.substring(0, 63);
	else
	    dragon.name = message;
	dragon.health = FakeDragon.MAX_HEALTH;
	sendDragon(dragon, player);
    }

    public void setPercent(Player player, float percent) {
	if (!hasBar(player))
	    return;
	
	FakeDragon dragon = getDragon(player, "");
	dragon.health = percent * FakeDragon.MAX_HEALTH;
	sendDragon(dragon, player);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void PlayerLoggout(PlayerQuitEvent event) {
	quit(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerKick(PlayerKickEvent event) {
	quit(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
	handleTeleport(event.getPlayer(), event.getTo().clone());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerRespawnEvent event) {
	handleTeleport(event.getPlayer(), event.getRespawnLocation().clone());
    }

    private void quit(Player player) {
	removeBar(player);
    }

    private void handleTeleport(final Player player, final Location loc) {
	if (!hasBar(player))
	    return;
	SchedulerUtil.runDelayed(new Runnable() {
	    @Override
	    public void run() {
		FakeDragon oldDragon = getDragon(player, "");
		Object destroyPacket = getDragon(player, "").getDestroyPacket();
		Util.sendPacket(player, destroyPacket);
		players.remove(player.getName());

		FakeDragon dragon = addDragon(player, loc, oldDragon.name);
		dragon.health = oldDragon.health;
		sendDragon(dragon, player);
	    }
	}, 2L);
    }

    private boolean hasBar(Player player) {
	return players.get(player.getName()) != null;
    }

    private void removeBar(Player player) {
	if (hasBar(player)) {
	    Util.sendPacket(player, getDragon(player, "").getDestroyPacket());
	    players.remove(player.getName());
	}
    }

    private void sendDragon(FakeDragon dragon, Player player) {
	Object metaPacket = dragon.getMetaPacket(dragon.getWatcher());
	Object teleportPacket = dragon.getTeleportPacket(player.getLocation()
		.add(0, -200, 0));

	Util.sendPacket(player, metaPacket);
	Util.sendPacket(player, teleportPacket);
    }

    private FakeDragon getDragon(Player player, String message) {
	if (hasBar(player))
	    return players.get(player.getName());
	else
	    return addDragon(player, message);
    }

    private FakeDragon addDragon(Player player, String message) {
	FakeDragon dragon = Util.newDragon(message,
		player.getLocation().add(0, -200, 0));

	Util.sendPacket(player, dragon.getSpawnPacket());

	players.put(player.getName(), dragon);

	return dragon;
    }

    private FakeDragon addDragon(Player player, Location loc, String message) {
	FakeDragon dragon = Util.newDragon(message, loc.add(0, -200, 0));

	Util.sendPacket(player, dragon.getSpawnPacket());

	players.put(player.getName(), dragon);

	return dragon;
    }
}
