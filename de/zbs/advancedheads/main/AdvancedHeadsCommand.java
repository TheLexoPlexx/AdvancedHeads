package de.zbs.advancedheads.main;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdvancedHeadsCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if (cs instanceof Player) {
			final Player p = (Player) cs;
			if (p.hasPermission("heads.use")) {
				if (args.length >= 0) {
					
				} else {
					cs.sendMessage(AdvancedHeads.prefix_error + "Zu viele Argumente!");
				}
			} else {
				cs.sendMessage(AdvancedHeads.prefix_error + "Keine Berechtigungen!");
			}
		} else {
			cs.sendMessage(AdvancedHeads.prefix_error + "Du musst ein Spieler sein");
	    }
		return true;
	}
}