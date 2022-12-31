package me.xaxis.reputation.handle;

import me.xaxis.reputation.Reputation;
import org.bukkit.entity.Player;

public class ReputationManager{

    private final Reputation plugin;
    private Player player;

    public ReputationManager(Reputation plugin) {
        this.plugin = plugin;


    }

    public ReputationManager(Reputation plugin, Player player){
        this.plugin = plugin;
        this.player = player;
        plugin.getSqliteUtility().
    }


}
