package net.coasterman10.Annihilation;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Shop implements Listener {
    private static class ShopItem {
	private ItemStack item;
	private int price;

	public ShopItem(Material type, int price) {
	    item = new ItemStack(type);
	    this.price = price;
	}

	public ShopItem(Material type, int qty, int price) {
	    this(type, price);
	    item.setAmount(qty);
	}

	public ShopItem setName(String name) {
	    item.getItemMeta().setDisplayName(name);
	    return this;
	}

	public ItemStack getShopStack() {
	    ItemStack stack = item.clone();
	    String priceStr = ChatColor.GOLD.toString() + price + " Gold";
	    ItemMeta meta = stack.getItemMeta();
	    if (meta.hasLore())
		meta.getLore().add(priceStr);
	    else
		meta.setLore(Arrays.asList(priceStr));
	    stack.setItemMeta(meta);
	    return stack;
	}

	public ItemStack getItemStack() {
	    return item;
	}

	public int getPrice() {
	    return price;
	}

	public String getName() {
	    String name;
	    ItemMeta meta = item.getItemMeta();
	    if (meta.hasDisplayName()) {
		name = meta.getDisplayName();
	    } else {
		name = item.getType().name();
		name = name.replace("_", " ").toLowerCase();
		name = WordUtils.capitalize(name);
		name += ChatColor.WHITE; // In case it's a wand
	    }
	    if (item.getAmount() > 1)
		name += " x" + item.getAmount();
	    
	    // Fix the brewing stand name
	    name = name.replace(" Item", "");
	    return name;
	}
    }

    public static class Inventories {
	public static final ArrayList<ShopItem> weaponShopItems,
		brewingShopItems;

	static {
	    weaponShopItems = new ArrayList<ShopItem>();
	    brewingShopItems = new ArrayList<ShopItem>();

	    String wandPrefix = ChatColor.YELLOW.toString()
		    + ChatColor.BOLD.toString();

	    // I didn't feel like using a config for this and having to parse
	    // strings so I just initialized it here
	    addWeapon(new ShopItem(Material.CHAINMAIL_HELMET, 10));
	    addWeapon(new ShopItem(Material.CHAINMAIL_CHESTPLATE, 10));
	    addWeapon(new ShopItem(Material.CHAINMAIL_LEGGINGS, 10));
	    addWeapon(new ShopItem(Material.CHAINMAIL_BOOTS, 10));
	    addWeapon(new ShopItem(Material.IRON_SWORD, 5));
	    addWeapon(new ShopItem(Material.BOW, 5));
	    addWeapon(new ShopItem(Material.ARROW, 32, 5));
	    addWeapon(new ShopItem(Material.BLAZE_ROD, 16).setName(wandPrefix
		    + "Apprentice Wand"));
	    addWeapon(new ShopItem(Material.BLAZE_ROD, 32).setName(wandPrefix
		    + "Master Wand"));
	    addWeapon(new ShopItem(Material.BREAD, 4, 5));
	    addWeapon(new ShopItem(Material.MELON, 16, 5));
	    addWeapon(new ShopItem(Material.CAKE, 5));

	    addBrewing(new ShopItem(Material.BREWING_STAND_ITEM, 10));
	    addBrewing(new ShopItem(Material.GLASS_BOTTLE, 3, 1));
	    addBrewing(new ShopItem(Material.NETHER_WARTS, 5));
	    nextRow(brewingShopItems);
	    addBrewing(new ShopItem(Material.REDSTONE, 3));
	    addBrewing(new ShopItem(Material.GLOWSTONE, 3));
	    addBrewing(new ShopItem(Material.FERMENTED_SPIDER_EYE, 3));
	    addBrewing(new ShopItem(Material.SULPHUR, 3));
	    nextRow(brewingShopItems);
	    addBrewing(new ShopItem(Material.BLAZE_POWDER, 15));
	    addBrewing(new ShopItem(Material.GHAST_TEAR, 15));
	    addBrewing(new ShopItem(Material.GOLDEN_CARROT, 2));
	    addBrewing(new ShopItem(Material.MAGMA_CREAM, 2));
	    addBrewing(new ShopItem(Material.SUGAR, 2));
	    addBrewing(new ShopItem(Material.SPIDER_EYE, 2));
	    addBrewing(new ShopItem(Material.SPECKLED_MELON, 1));
	}

	private static void addWeapon(ShopItem item) {
	    weaponShopItems.add(item);
	}

	private static void addBrewing(ShopItem item) {
	    brewingShopItems.add(item);
	}

	private static void nextRow(ArrayList<ShopItem> list) {
	    int start = list.size();
	    int end = 9 * (int) Math.ceil(list.size() / 9.0);
	    for (int i = start; i < end; i++) {
		list.add(null);
	    }
	}

    }

    private final String name;
    private final ArrayList<ShopItem> items;

    public Shop(Annihilation plugin, String name, ArrayList<ShopItem> items) {
	this.name = name;
	this.items = items;
	plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onSignClick(PlayerInteractEvent e) {
	if (e.getClickedBlock() != null) {
	    Material type = e.getClickedBlock().getType();
	    if (type == Material.WALL_SIGN || type == Material.SIGN_POST) {
		Sign sign = (Sign) e.getClickedBlock().getState();
		String line0 = ChatColor.stripColor(sign.getLine(0));
		String line1 = ChatColor.stripColor(sign.getLine(1));
		if (line0.equals("[Shop]") && line1.equals(name)) {
		    openShop(e.getPlayer());
		}
	    }
	}
    }

    @EventHandler
    public void onShopInventoryClick(InventoryClickEvent e) {
	Player buyer = (Player) e.getWhoClicked();
	if (e.getInventory().getName().equals(name + " Shop")) {
	    int slot = e.getRawSlot();
	    if (slot < e.getInventory().getSize() && slot >= 0) {
		e.setCancelled(true);
		if (slot < items.size() && items.get(slot) != null) {
		    sellItem(buyer, items.get(slot));
		    Inventory temp = e.getView().getTopInventory();
		    buyer.closeInventory();
		    buyer.openInventory(temp);
		}
	    }
	}
    }

    private void openShop(Player player) {
	int size = 9 * (int) Math.ceil(items.size() / 9.0);
	Inventory shopInv = Bukkit.getServer().createInventory(null, size,
		name + " Shop");
	for (int i = 0; i < items.size(); i++) {
	    ShopItem item = items.get(i);
	    if (item != null)
		shopInv.setItem(i, item.getShopStack());
	    else
		shopInv.setItem(i, null);
	}
	player.openInventory(shopInv);
    }

    private void sellItem(Player buyer, ShopItem item) {
	Inventory buyerInv = buyer.getInventory();
	ItemStack stackToGive = item.getItemStack();
	int price = item.getPrice();

	String stackName = ChatColor.WHITE + item.getName();

	if (buyerInv.contains(Material.GOLD_INGOT, price)) {
	    buyerInv.removeItem(new ItemStack(Material.GOLD_INGOT, price));
	    buyerInv.addItem(stackToGive);
	    buyer.sendMessage(ChatColor.GREEN + "Purchased " + stackName);
	} else {
	    buyer.sendMessage(ChatColor.RED + "Insufficient gold to purchase "
		    + stackName);
	}
    }
}
