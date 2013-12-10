package net.coasterman10.Annihilation.shop;

import net.coasterman10.Annihilation.Annihilation;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class Shop implements Listener {
    protected abstract Integer getCost(ItemStack item);

    protected abstract String getName(Material item);

    protected abstract Inventory getInventory();

    protected abstract String getTitle();

    public Shop(Annihilation plugin) {
	plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onSignInteract(PlayerInteractEvent e) {
	if (e.getClickedBlock() != null) {
	    Material type = e.getClickedBlock().getType();
	    Player player = e.getPlayer();
	    if (type == Material.WALL_SIGN || type == Material.SIGN_POST) {
		player.openInventory(getInventory());
	    }
	}
    }

    @EventHandler
    public void onShopInventoryInteract(InventoryClickEvent e) {
	if (e.getInventory().getTitle() == getTitle()) {
	    e.setCancelled(true);
	    if (e.getRawSlot() < 9 && pickedUpStack(e.getAction())) {
		Player player = (Player) e.getWhoClicked();
		Inventory inventory = player.getInventory();
		ItemStack item = e.getCurrentItem();
		ItemStack toGive = new ItemStack(item.getType(),
			item.getAmount());
		int cost = getCost(item);
		if (inventory.contains(Material.GOLD_INGOT, cost)) {
		    inventory.removeItem(new ItemStack(Material.GOLD_INGOT,
			    cost));
		    inventory.addItem(toGive);
		    String message = ChatColor.GREEN + "Bought "
			    + getName(item.getType());
		    player.sendMessage(message);
		}
	    }
	}
    }

    private boolean pickedUpStack(InventoryAction action) {
	return action == InventoryAction.PICKUP_ALL
		|| action == InventoryAction.PICKUP_HALF
		|| action == InventoryAction.PICKUP_ONE
		|| action == InventoryAction.PICKUP_SOME;
    }
}
