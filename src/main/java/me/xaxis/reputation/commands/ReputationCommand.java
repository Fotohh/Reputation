package me.xaxis.reputation.commands;

import me.xaxis.reputation.Lang;
import me.xaxis.reputation.Reputation;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class ReputationCommand implements CommandExecutor {

    private final Reputation plugin;

    public ReputationCommand(Reputation plugin){
        this.plugin = plugin;
        plugin.getCommand("reputation").setExecutor(this);
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(plugin.getSqliteUtility() == null){
            sender.getServer().getLogger().log(Level.SEVERE, "SQLite is null! Contact developers about this error! Disabling plugin...");
            sender.getServer().getPluginManager().disablePlugin(plugin);
            return true;
        }

        if(!(sender instanceof Player)){
            sender.sendMessage(Lang.SENDER_NOT_PLAYER.getMsg(plugin));
            return true;
        }

        Player player = (Player) sender;

        if(args.length == 0){
            //TODO This will be a help cmd

        }else if(args.length == 1){

            Player target = Bukkit.getPlayer(args[0]);

            if(target == null || !target.isOnline()){
                player.sendMessage("That player is invalid!");
                return true;
            }

            //ChiboYen's total reputation is X (Y likes | Z dislikes)

            player.sendMessage("");

        }else if(args.length == 2){

            Player target = Bukkit.getPlayer(args[0]);

            if(target == null || !target.isOnline()){
                player.sendMessage("That player is invalid!");
                return true;
            }

            switch (args[1]){
                case "like" ->{
                    if(!plugin.getSqliteUtility().entryExists(target.getUniqueId())){
                        plugin.getSqliteUtility().createPlayerReputationEntry(target.getUniqueId());
                    }
                    plugin.getSqliteUtility().setLikes(target, plugin.getConfig().getInt("like_amt") + plugin.getSqliteUtility().getLikes(target));
                    player.sendMessage("You have liked this player!");
                }
                case "dislike" ->{
                    if(!plugin.getSqliteUtility().entryExists(target.getUniqueId())){
                        plugin.getSqliteUtility().createPlayerReputationEntry(target.getUniqueId());
                    }
                    plugin.getSqliteUtility().setLikes(target, plugin.getConfig().getInt("dislike_amt") + plugin.getSqliteUtility().getDislikes(target));
                    player.sendMessage("You have disliked this player!");
                }
            }

        } else if (args.length == 4 && args[1].equalsIgnoreCase("set")) {
            Player target = Bukkit.getPlayer(args[0]);

            //reputation player set likes 10 | 4 arguments

            if(target == null || !target.isOnline()){
                player.sendMessage("That player is invalid!");
                return true;
            }

            if(!plugin.getSqliteUtility().entryExists(target.getUniqueId())){
                plugin.getSqliteUtility().createPlayerReputationEntry(target.getUniqueId());
            }

            int amount = 0;

            try{
                amount = Integer.parseInt(args[3]);
            }catch (Exception e){
                player.sendMessage("argument must be an integer!");
            }

            switch (args[2]){
                case "likes"->{
                    plugin.getSqliteUtility().setLikes(target, amount);
                    player.sendMessage("Successfully set their likes");
                }
                case "dislikes"->{
                    plugin.getSqliteUtility().setDislikes(target, amount);
                    player.sendMessage("Successfully set their dislikes");
                }
            }

        } else{
            player.sendMessage("Invalid usage!");
        }

        return true;
    }
}
