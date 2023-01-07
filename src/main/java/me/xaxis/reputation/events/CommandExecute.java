package me.xaxis.reputation.events;

import me.xaxis.reputation.ReputationMain;
import me.xaxis.reputation.handle.PlayerReputationManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Level;

public class CommandExecute implements Listener {
    private final ReputationMain plugin;

    public CommandExecute(ReputationMain plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void commandExecuteEvent(PlayerCommandPreprocessEvent event){
        String s = event.getMessage();
        if(!s.contains("reputation")) return;
        String[] list = s.split(" ");
        if(list.length > 2) {
            Player player = Bukkit.getPlayer(list[1]);
            if(player == null) return;
            plugin.getServer().getScheduler().runTaskLater(plugin,()-> {
                try {
                    PlayerReputationManager.getPlayerReputationManager(player.getUniqueId()).cacheData();
                } catch (SQLException e) {
                    throw new RuntimeException("Unable to cache player data",e);
                }
            }, 40);
        }
    }
}
