package me.xaxis.reputation.events;

import me.xaxis.reputation.ReputationMain;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;

import java.util.logging.Level;

public class CommandExecute implements Listener {
    private final ReputationMain plugin;

    public CommandExecute(ReputationMain plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void commandExecuteEvent(PlayerCommandSendEvent event){
        Player executor = event.getPlayer();
        plugin.getLogger().log(Level.INFO,event.getCommands().toString());
        for(String s : event.getCommands()) {
            plugin.getLogger().log(Level.INFO, s);
        }
    }
}
