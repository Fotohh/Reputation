package me.xaxis.reputation.events;

import me.xaxis.reputation.ReputationMain;
import me.xaxis.reputation.handle.PlayerReputationManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;
import java.util.UUID;

public class onJoin implements Listener {

    private final ReputationMain plugin;

    public onJoin(ReputationMain plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        UUID uuid = event.getPlayer().getUniqueId();
        plugin.getSqliteUtility().createPlayerReputationEntry(uuid);

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()->{
            if(!PlayerReputationManager.containsPlayer(uuid)) {
                try {
                    new PlayerReputationManager(uuid, plugin).cacheData();
                } catch (SQLException e) {
                    throw new RuntimeException("Unable to register player reputation manager!",e);
                }
            }
        });
    }
}
