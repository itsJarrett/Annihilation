package net.coasterman10.Annihilation.shop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import net.coasterman10.Annihilation.Annihilation;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class WeaponShop extends Shop {
    private static final HashMap<Material, String> names = new HashMap<Material, String>();
    private static final HashMap<Material, Integer> costs = new HashMap<Material, Integer>();
    private static final ArrayList<ItemStack> items = new ArrayList<ItemStack>();

    static {
	addItem(Material.CHAINMAIL_HELMET, 1, "Chainmail Helmet", 10);
	addItem(Material.CHAINMAIL_CHESTPLATE, 1, "Chainmail Chestplate", 10);
	addItem(Material.CHAINMAIL_LEGGINGS, 1, "Chainmail Leggings", 10);
	addItem(Material.CHAINMAIL_BOOTS, 1, "Chainmail Boots", 10);
	addItem(Material.IRON_SWORD, 1, "Iron Sword", 5);
	addItem(Material.BOW, 1, "Bow", 5);
	addItem(Material.ARROW, 32, "32 Arrows", 5);
	addItem(Material.BREAD, 4, "4 Bread", 5);
    }

    public WeaponShop(Annihilation plugin) {
	super(plugin);
    }

    @Override
    protected Integer getCost(ItemStack item) {
	Material type = item.getType();
	if (costs.containsKey(type)) {
	    return costs.get(type);
	}
	return 0;
    }

    @Override
    protected String getName(Material item) {
	if (names.containsKey(item)) {
	    return names.get(item);
	}
	return ChatColor.DARK_RED + "NULL! BUG!";
    }

    @Override
    protected Inventory getInventory() {
	int slots = 9 * (int) (Math.ceil(items.size() / 9.0));
	Inventory inv = Bukkit.getServer().createInventory(null, slots,
		getTitle());
	for (ItemStack item : items)
	    inv.addItem(item);
	return inv;
    }

    @Override
    protected String getTitle() {
	return "Weapon Shop";
    }
    
    private static void addItem(Material type, Integer qty, String name,
	    Integer cost) {
	ItemStack stack = new ItemStack(type, qty);
	ItemMeta meta = stack.getItemMeta();
	String lore = ChatColor.GOLD.toString() + cost + " Gold";
	meta.setLore(Arrays.asList(lore));
	stack.setItemMeta(meta);

	items.add(stack);
	names.put(type, name);
	costs.put(type, cost);
    }
}
