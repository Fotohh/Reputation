package me.xaxis.reputation.colorchat;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Chat {
    public static String color(String msg, Player player){
        return PlaceholderAPI.setPlaceholders(player,ChatColor.translateAlternateColorCodes('&', msg));
    }
}
