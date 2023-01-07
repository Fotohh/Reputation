package me.xaxis.reputation.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.xaxis.reputation.ReputationMain;
import me.xaxis.reputation.handle.PlayerReputationManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

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
                return String.valueOf(info.getTotal());
            }
            case "likes"->{
                return String.valueOf(info.getLikes());
            }
            case "dislikes"->{
                return String.valueOf(info.getDislikes());
            }
            case "percentage"->{
                return String.valueOf(info.getPercentage());
            }
            case "color"->{
                return String.valueOf(info.getColor());
            }
        }
        return "Error! Code: NSFFPH | Send this to the developer!";
    }
}
