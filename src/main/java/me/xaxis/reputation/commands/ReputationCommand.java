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

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
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

            return true;
        }
        if(args.length == 2){

            Player target = Bukkit.getPlayer(args[0]);

            if(target == null || !target.isOnline()){
                player.sendMessage(Chat.color(Lang.PLAYER_IS_NULL.getMsg(plugin).replace("%player_name_offline%",args[0]), target));
                return true;
            }
            PlayerReputationManager m = PlayerReputationManager.getPlayerReputationManager(target.getUniqueId());

            if(!TimeIsUp(player)){
                long timeInSeconds = Math.abs((System.currentTimeMillis() - m.getPlayerTimestamp()));
                Duration duration = Duration.between(new Date(System.currentTimeMillis()).toInstant(),new Date(timeInSeconds).toInstant());
                String msg = Lang.PLAYER_CMD_TIMEOUT.getMsg(plugin)
                        .replace( "%time_weeks%", String.valueOf(Math.abs( duration.get(ChronoUnit.WEEKS)) ) )
                            .replace("%time_days%",String.valueOf(Math.abs(duration.get(ChronoUnit.DAYS))))
                                .replace("%time_hours%",String.valueOf(Math.abs(duration.get(ChronoUnit.HOURS))))
                                    .replace("%time_minutes%",String.valueOf(Math.abs(duration.get(ChronoUnit.MINUTES))))
                                        .replace("%time_seconds%",String.valueOf(Math.abs(duration.get(ChronoUnit.SECONDS))));
                player.sendMessage(Chat.color(msg, player));
                return true;
            }

            createPlayerTimestamp(player);

            switch (args[1]){
                case "like" ->{
                    plugin.getSqliteUtility().addLike(target.getUniqueId(), plugin.getConfig().getInt("like_amt"));
                    player.sendMessage(Chat.color(Lang.LIKED_PLAYER.getMsg(plugin),target));
                }
                case "dislike" ->{
                    plugin.getSqliteUtility().addDislike(target.getUniqueId(), plugin.getConfig().getInt("dislike_amt"));
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
        plugin.getSqliteUtility().setTimestamp(player.getUniqueId(), totalTime);
    }

    /**
     * @param player Player you want to check
     * @return true if time is up, false if it isn't
     */
    private boolean TimeIsUp(Player player){
        if(PlayerReputationManager.getPlayerReputationManager(player.getUniqueId()).getPlayerTimestamp() == 0) return true;
        long currentTime = System.currentTimeMillis();
        long totalTime = PlayerReputationManager.getPlayerReputationManager(player.getUniqueId()).getPlayerTimestamp();
        if(currentTime>=totalTime){
            plugin.getSqliteUtility().setTimestamp(player.getUniqueId(), 0L);
            return true;
        }else return false;
    }
}
