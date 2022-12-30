package me.xaxis.reputation.commands;

import me.xaxis.reputation.Lang;
import me.xaxis.reputation.Reputation;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReputationCommand implements CommandExecutor {

    private final Reputation plugin;

    public ReputationCommand(Reputation plugin){
        this.plugin = plugin;
        plugin.getCommand("reputation").setExecutor(this);
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

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

            //TODO add functionality

        }else if(args.length == 2){

            Player target = Bukkit.getPlayer(args[0]);

            if(target == null || !target.isOnline()){
                player.sendMessage("That player is invalid!");
                return true;
            }

            switch (args[1]){
                case "like" ->{
                    //TODO add functionality | increase or decrease by configurable amount
                }
                case "dislike" ->{

                }
            }

        }else{
            player.sendMessage("Invalid usage!");
        }


        return true;
    }
}
