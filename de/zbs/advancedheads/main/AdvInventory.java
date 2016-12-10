package de.zbs.advancedheads.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class AdvInventory implements Listener {
	
	private Inventory inv;
	private static HashMap<String, AdvInventory> inventories = new HashMap<String, AdvInventory>();
	private HashMap<Integer, ClickRunnable> runs = new HashMap<Integer, ClickRunnable>();
	private int currentOpen = 0;
	private boolean registered = false;
	
	private AdvInventory() {}
	
	public static void register(JavaPlugin plugin) {
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new AdvInventory(), plugin);
	}
	
	public AdvInventory(String name, int size) {
		new AdvInventory(name, size, false);
	}
	
	@SuppressWarnings("deprecation")
	public AdvInventory(String name, int size, boolean placeHolder) {
		if (size == 0) {
			return;
		}
		inv = Bukkit.createInventory(null, size, ChatColor.BLUE + name);
		if (placeHolder) {
			ItemStack placeholder = new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.GRAY.getData());
			ItemMeta placeholdermeta = placeholder.getItemMeta();
			placeholdermeta.setDisplayName(ChatColor.YELLOW + "");
			placeholder.setItemMeta(placeholdermeta);
			
			for (int i = 0; i < size; i++) {
				inv.setItem(i, placeholder);
			}
		}
		register();
	}
	
	public Inventory getSourceInventory() {
		return inv;
	}
	
	public int getSize() {
		return inv.getSize();
	}
	
	public void setItem(ItemStack itemstack, String displayname, Integer slot, ClickRunnable executeOnClick, String... description) {
		ItemStack is = itemstack;
		ItemMeta im = is.getItemMeta();
		im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES,
				ItemFlag.HIDE_DESTROYS,
				ItemFlag.HIDE_ENCHANTS,
				ItemFlag.HIDE_PLACED_ON,
				ItemFlag.HIDE_POTION_EFFECTS,
				ItemFlag.HIDE_UNBREAKABLE);
		im.setDisplayName(ChatColor.BLUE + displayname);
		if (description != null) {
			List<String> lore = new ArrayList<String>();
			for (String s : description) {
				lore.add(ChatColor.GRAY + s);
			}
			im.setLore(lore);
		}
		is.setItemMeta(im);
		inv.setItem(slot, is);
		runs.put(slot, executeOnClick);
	}
	
	public void removeItem(int slot) {
		inv.setItem(slot, new ItemStack(Material.AIR));
	}

	public void setItem(ItemStack itemstack, Integer slot) {
		inv.setItem(slot, itemstack);
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (e.getWhoClicked() instanceof Player) {
			if (e.getCurrentItem() == null) {
				return;
			}
			if (inventories.containsKey(e.getClickedInventory().getName())) {
				AdvInventory current = inventories.get(e.getClickedInventory().getName());
				e.setCancelled(true);
				Player p = (Player) e.getWhoClicked();
				if (current.runs.get(e.getSlot()) == null) {
					p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1, 1);
				} else {
					p.playSound(p.getLocation(), Sound.WOOD_CLICK, 1, 1);
					if (current.runs.get(e.getSlot()) != null) {
						current.runs.get(e.getSlot()).run(e);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if (e.getPlayer() instanceof Player) {
			Inventory currentinv;
			if ((currentinv = e.getInventory()) == null) {
				return;
			}
			if (inventories.containsKey(currentinv.getName())) {
				AdvInventory current = inventories.get(currentinv.getName());
				current.currentOpen--;
				if (current.currentOpen == 0) {
					current.unRegister();
				}
			}
		}
	}
	
	public void openInventory(Player player) {
		currentOpen++;
		register();
		player.openInventory(getSourceInventory());
	}
	
	public void register() {
		if (!registered) {
			inventories.put(inv.getName(), this);
			registered = true;
		}
	}
	
	public void unRegister() {
		if (registered) {
			inventories.remove(inv.getName());
			registered = false;
		}
	}
	
	public static abstract class ClickRunnable {
		public abstract void run(InventoryClickEvent e);
	}
	
	public static abstract class CloseRunnable {
		public abstract void run(InventoryCloseEvent e);
	}
}