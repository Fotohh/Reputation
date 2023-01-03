package me.xaxis.reputation.events;

import me.xaxis.reputation.Reputation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.logging.Level;

public class onJoin implements Listener {

    private final Reputation plugin;

    public onJoin(Reputation plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        plugin.getSqliteUtility().createPlayerReputationEntry(player.getUniqueId());
        player.getServer().getLogger().log(Level.INFO, "Umm...??");
    }
}
