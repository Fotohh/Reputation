package me.xaxis.reputation.commands;

import me.clip.placeholderapi.PlaceholderAPI;
import me.xaxis.reputation.Lang;
import me.xaxis.reputation.Reputation;
import me.xaxis.reputation.colorchat.Chat;
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

            return true;
        }
        if(args.length == 1){

            Player target = Bukkit.getPlayer(args[0]);

            if(target == null || !target.isOnline()){
                player.sendMessage(args[0] + " is not online or is null!");
                return true;
            }

            String msg = PlaceholderAPI.setPlaceholders(target, "%player_name%'s total reputation is %reputation_total% (%reputation_likes% likes | %reputation_dislikes% dislikes)");

            player.sendMessage(Chat.color(msg));

            return true;

        }
        if(args.length == 2){

            Player target = Bukkit.getPlayer(args[0]);

            if(target == null || !target.isOnline()){
                player.sendMessage(Chat.color(args[0] + " is not online or is null!"));
                return true;
            }

            switch (args[1]){
                case "like" ->{
                    if(!plugin.getSqliteUtility().entryExists(target.getUniqueId())){
                        plugin.getSqliteUtility().createPlayerReputationEntry(target.getUniqueId());
                    }
                    int a = plugin.getConfig().getInt("like_amt");
                    int b = plugin.getSqliteUtility().getLikes(target);
                    plugin.getSqliteUtility().setLikes(target,a+b);
                    player.sendMessage(PlaceholderAPI.setPlaceholders(target,Chat.color("You have liked %player_name%!")));
                }
                case "dislike" ->{
                    if(!plugin.getSqliteUtility().entryExists(target.getUniqueId())){
                        plugin.getSqliteUtility().createPlayerReputationEntry(target.getUniqueId());
                    }
                    int a = plugin.getConfig().getInt("dislike_amt");
                    int b = plugin.getSqliteUtility().getDislikes(target);
                    plugin.getSqliteUtility().setLikes(target,a+b);
                    player.sendMessage(PlaceholderAPI.setPlaceholders(target,Chat.color("You have disliked %player_name%!")));
                }
            }

            return true;

        }
        if (args.length == 4 && args[1].equalsIgnoreCase("set")) {
            Player target = Bukkit.getPlayer(args[0]);

            //reputation player set likes 10 | 4 arguments

            if(target == null || !target.isOnline()){
                player.sendMessage(Chat.color(args[0] +" is not online or is null!"));
                return true;
            }

            if(!plugin.getSqliteUtility().entryExists(target.getUniqueId())){
                plugin.getSqliteUtility().createPlayerReputationEntry(target.getUniqueId());
            }

            int amount = 0;

            try{
                amount = Integer.parseInt(args[3]);
            }catch (Exception e){
                player.sendMessage(Chat.color("Argument must be an integer! Found " +args[3]));
                return true;
            }

            switch (args[2]){
                case "likes"->{
                    plugin.getSqliteUtility().setLikes(target, amount);
                    player.sendMessage(PlaceholderAPI.setPlaceholders(target,Chat.color("Successfully set %player_name%'s likes to "+amount)));
                }
                case "dislikes"->{
                    plugin.getSqliteUtility().setDislikes(target, amount);
                    player.sendMessage(PlaceholderAPI.setPlaceholders(target,Chat.color("Successfully set %player_name%'s dislikes "+amount)));
                }
            }

            return true;

        }

        player.sendMessage(Chat.color("Invalid usage!"));

        return true;
    }
}
