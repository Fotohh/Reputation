package me.xaxis.reputation.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.xaxis.reputation.ReputationMain;
import me.xaxis.reputation.handle.PlayerReputationManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

@SuppressWarnings("all")
public class Reputation extends PlaceholderExpansion {

    private final ReputationMain plugin;

    public Reputation(ReputationMain plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "Reputation";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Xaxis";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public @Nullable String getRequiredPlugin() {
        return plugin.getName();
    }
    @Override
    public boolean canRegister() {
        return true;
    }
    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @NotNull List<String> getPlaceholders() {
        List<String> list = new ArrayList<>();
        list.add("total");
        list.add("likes");
        list.add("dislikes");
        list.add("percentage");
        list.add("color");
        return list;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if(player == null) return "";
        PlayerReputationManager info = PlayerReputationManager.getPlayerReputationManager(player);

        switch (params){
            case "total"->{
                player.getServer().getLogger().log(Level.INFO, "total "+info.getTotal());
                return String.valueOf(info.getTotal());
            }
            case "likes"->{
                player.getServer().getLogger().log(Level.INFO, "likes "+info.getLikes());
                return String.valueOf(info.getLikes());
            }
            case "dislikes"->{
                player.getServer().getLogger().log(Level.INFO, "dislikes "+info.getDislikes());
                return String.valueOf(info.getDislikes());
            }
            case "percentage"->{
                player.getServer().getLogger().log(Level.INFO, "percentage "+info.getPercentage());
                return String.valueOf(info.getPercentage());
            }
            case "color"->{
                player.getServer().getLogger().log(Level.INFO, "color "+info.getColor());
                return String.valueOf(info.getColor());
            }
        }
        return "Error! Code: NSFFPH | Send this to the developer!";
    }
}
