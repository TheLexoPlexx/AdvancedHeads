package de.zbs.advancedheads.main;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AdvancedHeads extends JavaPlugin {
	
	public static AdvancedHeads plugin;
	
	public static String line = ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "------>---------------------------------------<------";
	public static String prefix = ChatColor.DARK_GRAY + "Head " + ChatColor.BLUE + "� " + ChatColor.DARK_AQUA + "";
	public static String prefix_error = ChatColor.DARK_GRAY + "Head " + ChatColor.BLUE + "� " + ChatColor.RED + "";
	
	String path = "plugins/AdvancedHeads";

	@Override
	public void onEnable() {
		plugin = this;
		
		//Commands
		this.getCommand("head").setExecutor(new AdvancedHeadsCommand());
		
		if (!(new File(path).exists())) {
			downloadDatabase();
		}
		AdvInventory.register(plugin);
		
		this.getLogger().info("Successfully enabled");
	}
	
	@Override
	public void onDisable() {
		this.getLogger().info("Successfully disabled.");
	}
	
	private boolean downloadDatabase() {
		new File(path).mkdirs();
		try {
			Document doc = Jsoup.connect("http://heads.freshcoal.com/maincollection.php").get();
//			File f = new File(path, "database.yml");
//			FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);

			Elements head = doc.select(".heads");
			System.out.println(head);
			for (Element e : head) {
				Attributes att = e.attributes();
				System.out.println(att);
				String command = att.get("data-clipboard-text");
				System.out.println(command);
				String remover = command.replace("/give @p skull 1 3 {display:{Name:\"", "");
				String name = remover.substring(0, 5);
				Bukkit.broadcastMessage(name);
				String texture = remover.substring(30, 50);
				Bukkit.broadcastMessage(texture);
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}