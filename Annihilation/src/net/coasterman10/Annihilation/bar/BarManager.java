package net.coasterman10.Annihilation.bar;

import java.util.HashMap;

import net.coasterman10.Annihilation.Annihilation;
import net.coasterman10.Annihilation.SchedulerUtil;

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
    private String message = "";
    private float percent = 0F;
    private boolean visible = false;

    private Annihilation plugin;

    public BarManager(Annihilation plugin) {
	this.plugin = plugin;
	plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void setMessage(String message) {
	if (message.length() > 64)
	    this.message = message.substring(0, 63);
	else
	    this.message = message;
	update();
    }

    public void setPercent(float percent) {
	this.percent = percent;
	update();
    }

    public void setVisible(boolean visible) {
	this.visible = visible;
	update();
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
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
	handleTeleport(event.getPlayer(), event.getTo().clone());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(final PlayerRespawnEvent event) {
	handleTeleport(event.getPlayer(), event.getRespawnLocation().clone());
    }

    private void quit(Player player) {
	removeBar(player);
    }

    private void handleTeleport(final Player player, final Location loc) {
	SchedulerUtil.runDelayed(new Runnable() {
	    @Override
	    public void run() {
		FakeDragon oldDragon = getDragon(player);
		Object destroyPacket = getDragon(player).getDestroyPacket();
		Util.sendPacket(player, destroyPacket);
		players.remove(player.getName());

		FakeDragon dragon = addDragon(player, loc);
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
	    Object destroyPacket = getDragon(player).getDestroyPacket();
	    Util.sendPacket(player, destroyPacket);
	    players.remove(player.getName());
	}
    }

    private void update() {
	for (Player player : plugin.getServer().getOnlinePlayers()) {
	    if (visible) {
		FakeDragon dragon = getDragon(player);
		dragon.name = message;
		dragon.health = FakeDragon.MAX_HEALTH * percent / 100;
		sendDragon(dragon, player);
	    } else {
		removeBar(player);
	    }
	}
    }

    private void sendDragon(FakeDragon dragon, Player player) {
	Object metaPacket = dragon.getMetaPacket(dragon.getWatcher());
	Object teleportPacket = dragon.getTeleportPacket(player.getLocation()
		.add(0, -200, 0));

	Util.sendPacket(player, metaPacket);
	Util.sendPacket(player, teleportPacket);
    }

    private FakeDragon getDragon(Player player) {
	if (hasBar(player))
	    return players.get(player.getName());
	else
	    return addDragon(player);
    }

    private FakeDragon addDragon(Player player) {
	FakeDragon dragon = Util.newDragon(message,
		player.getLocation().add(0, -200, 0));

	Util.sendPacket(player, dragon.getSpawnPacket());

	players.put(player.getName(), dragon);

	return dragon;
    }

    private FakeDragon addDragon(Player player, Location loc) {
	FakeDragon dragon = Util.newDragon(message, loc.add(0, -200, 0));

	Util.sendPacket(player, dragon.getSpawnPacket());

	players.put(player.getName(), dragon);

	return dragon;
    }
}
