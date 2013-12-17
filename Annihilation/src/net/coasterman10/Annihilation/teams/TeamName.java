package net.coasterman10.Annihilation.teams;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;

public enum TeamName {
	RED, YELLOW, GREEN, BLUE;

	@Override
	public String toString() {
		return ChatColor.valueOf(name())
				+ WordUtils.capitalize(name().toLowerCase());
	}
}
