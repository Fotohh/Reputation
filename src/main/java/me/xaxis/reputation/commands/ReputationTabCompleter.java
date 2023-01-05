package me.xaxis.reputation.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReputationTabCompleter implements TabCompleter {
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        //reputation <name> set like/dislike 0
        //reputation <name> like|dislikes

        if(sender instanceof Player){

            Player p = (Player) sender;

            if(args.length == 2){
                commands.add("like");
                commands.add("dislike");
                StringUtil.copyPartialMatches(args[1], commands, completions);
                Collections.sort(completions);
            }if(p.hasPermission("reputation.admin") && args.length == 2){
                commands.add("set");
                StringUtil.copyPartialMatches(args[1],commands,completions);
                Collections.sort(completions);
            }
            if(args.length == 3 && p.hasPermission("reputation.admin")){
                commands.add("like");
                commands.add("dislike");
                StringUtil.copyPartialMatches(args[2],commands,completions);
                Collections.sort(completions);
            }
        }

        return completions.isEmpty() ? null : completions;
    }
}
