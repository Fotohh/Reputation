package me.xaxis.reputation.commands;

import me.xaxis.reputation.Lang;
import me.xaxis.reputation.ReputationMain;
import me.xaxis.reputation.colorchat.Chat;
import me.xaxis.reputation.handle.PlayerReputationManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class ReputationCommand implements CommandExecutor {

    private final ReputationMain plugin;

    public ReputationCommand(ReputationMain plugin){
        this.plugin = plugin;
        plugin.getCommand("reputation").setExecutor(this);
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(plugin.getSqliteUtility() == null){
            sender.getServer().getLogger().log(Level.SEVERE, "SQLite is null! Contact developers about this error! Disabling plugin...");
            sender.getServer().getPluginManager().disablePlugin(plugin);
            return true;
        }
        if(!(sender instanceof Player player)){
            plugin.getLogger().log(Level.WARNING, Lang.SENDER_NOT_PLAYER.getMsg(plugin));
            return true;
        }

        PlayerReputationManager info = PlayerReputationManager.getPlayerReputationManager(player);

        plugin.getSqliteUtility().createPlayerReputationEntry(player.getUniqueId());

        if(args.length == 0){
            //TODO This will be a help cmd

            return true;
        }
        if(args.length == 1){
            Player target = Bukkit.getPlayer(args[0]);
            if(target == null || !target.isOnline()){
                player.sendMessage(Chat.color(Lang.PLAYER_IS_NULL.getMsg(plugin).replace("%player_name_offline%",args[0]), target));
                return true;
            }
            player.sendMessage(Chat.color(Lang.PLAYER_REPUTATION.getMsg(plugin), target));
            player.sendMessage(Chat.color("Total: "+info.getTotal() + " Likes: "+info.getLikes() +" Dislikes: "+info.getDislikes()+ " Ratio: "+info.getPercentage()+ " Color: "+info.getColor() + " Timestamp: "+info.getPlayerTimestamp(), target));
            player.sendMessage(Chat.color("Total: "+plugin.getSqliteUtility().getTotalReputation(player.getUniqueId()) +
                    " Likes: "+plugin.getSqliteUtility().getLikes(player.getUniqueId())
                    +" Dislikes: "+plugin.getSqliteUtility().getDislikes(player.getUniqueId())+
                    " Ratio: "+plugin.getSqliteUtility().getRatio(player.getUniqueId())+
                    " Timestamp: "+plugin.getSqliteUtility().getTimestamp(player.getUniqueId()), target));

            return true;
        }
        if(args.length == 2){

            Player target = Bukkit.getPlayer(args[0]);

            if(target == null || !target.isOnline()){
                player.sendMessage(Chat.color(Lang.PLAYER_IS_NULL.getMsg(plugin).replace("%player_name_offline%",args[0]), target));
                return true;
            }

            if(!TimeIsUp(player)){
                long timeInSeconds = Math.abs(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - info.getPlayerTimestamp()));
                String msg = Lang.PLAYER_CMD_TIMEOUT.getMsg(plugin).replace("%time_left%",new Date(timeInSeconds).toString());
                player.sendMessage(Chat.color(msg, player));
                return true;
            }

            createPlayerTimestamp(player);

            switch (args[1]){
                case "like" ->{
                    plugin.getSqliteUtility().addLike(player.getUniqueId(), plugin.getConfig().getInt("like_amt"));
                    player.sendMessage(Chat.color(Lang.LIKED_PLAYER.getMsg(plugin),target));
                }
                case "dislike" ->{
                    plugin.getSqliteUtility().addDislike(player.getUniqueId(), plugin.getConfig().getInt("dislike_amt"));
                    player.sendMessage(Chat.color(Lang.DISLIKED_PLAYER.getMsg(plugin), target));
                }
            }

            return true;

        }
        if (args.length == 4 && args[1].equalsIgnoreCase("set") && player.hasPermission("reputation.admin")) {
            Player target = Bukkit.getPlayer(args[0]);

            if(target == null || !target.isOnline()){
                player.sendMessage(Chat.color(Lang.PLAYER_IS_NULL.getMsg(plugin).replace("%player_name_offline%",args[0]), target));
                return true;
            }

            int amount;
            try{
                amount = Integer.parseInt(args[3]);
            }catch(Exception e){
                String msg = Lang.ARGUMENT_NOT_NUMBER.getMsg(plugin).replace("%argument_text%", args[3]);
                player.sendMessage(Chat.color(msg,player));
                return true;
            }

            switch (args[2]){
                case "like"->{
                    plugin.getSqliteUtility().setLikes(target.getUniqueId(), amount);
                    String msg = Lang.SET_PLAYER_LIKES.getMsg(plugin).replace("%amount_integer%",String.valueOf(amount));
                    player.sendMessage(Chat.color(msg,target));
                }
                case "dislike"->{
                    plugin.getSqliteUtility().setDislikes(target.getUniqueId(), amount);
                    String msg = Lang.SET_PLAYER_DISLIKES.getMsg(plugin).replace("%amount_integer%",String.valueOf(amount));
                    player.sendMessage(Chat.color(msg,target));
                }
            }

            return true;

        }
        else if (!player.hasPermission("reputation.admin") && args.length == 4 && args[1].equalsIgnoreCase("set")) {
            player.sendMessage(Chat.color(Lang.NO_PERMISSION.getMsg(plugin), player));
        }

        player.sendMessage(Chat.color(Lang.INVALID_USAGE.getMsg(plugin), player));

        return true;
    }

    private void createPlayerTimestamp(Player player){
        long currentTime = System.currentTimeMillis();
        long waitTime = TimeUnit.SECONDS.toMillis(plugin.getConfig().getLong("execute-cmd-timeout"));
        long totalTime = currentTime+waitTime;
        plugin.getSqliteUtility().createTimestamp(player.getUniqueId(), currentTime);
    }

    /**
     * @param player Player you want to check
     * @return true if time is up, false if it isn't
     */
    private boolean TimeIsUp(Player player){
        if(PlayerReputationManager.getPlayerReputationManager(player).getPlayerTimestamp() == 0) return true;
        long currentTime = System.currentTimeMillis();
        long totalTime = PlayerReputationManager.getPlayerReputationManager(player).getPlayerTimestamp();
        if(currentTime>=totalTime){
            plugin.getSqliteUtility().createTimestamp(player.getUniqueId(), 0L);
            return true;
        }else return false;
    }
}
