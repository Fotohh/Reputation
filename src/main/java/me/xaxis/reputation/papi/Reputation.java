package me.xaxis.reputation.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.xaxis.reputation.ReputationMain;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.logging.Level;

@SuppressWarnings("all")
public class Reputation extends PlaceholderExpansion {

    private final ReputationMain plugin;

    public Reputation(ReputationMain plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean register() {
        return true;
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
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        switch (params){
            case "total"->{
                return String.valueOf(plugin.getSqliteUtility().getTotalReputation(player));
            }
            case "likes"->{
                return String.valueOf(plugin.getSqliteUtility().getLikes(player));
            }
            case "dislikes"->{
                return String.valueOf(plugin.getSqliteUtility().getDislikes(player));
            }
            case "percentage"->{
                return String.valueOf(plugin.getSqliteUtility().getRatio(player));
            }
            case "color"->{

                ConfigurationSection section = plugin.getConfig().getConfigurationSection("thresholds");

                ArrayList<Integer> values = new ArrayList<>();

                int reputation = plugin.getSqliteUtility().getTotalReputation(player);

                for(String s : section.getKeys(false)){
                    try{
                        int a = Integer.parseInt(s);
                        values.add(a);
                    }catch (Exception e){
                        plugin.getLogger().log(Level.SEVERE, "Critical Error! Unable to register placeholder color!" +
                                "\nSeems that you formatted the identifiers in the section thresholds incorrectly...");
                    }
                }

                if(values.isEmpty()){
                    plugin.getLogger().log(Level.SEVERE, "ArrayList<Integer> values >> has no values! Contact developer with this error code: AHNC1");
                    return null;
                }

                for(int i = 0; i < values.size(); i++){

                    if(values.size() < 2) return section.getString(String.valueOf(values.get(i)));

                    if(reputation > values.get(i) && values.size() - 1 == i) return section.getString(String.valueOf(values.get(i)));

                    if(reputation > values.get(i) && reputation < values.get(i+1)) return section.getString(String.valueOf(values.get(i)));

                }

                return "Error! Code: WNATFC | Send this to the developer!";

            }
        }
        return "Error! Code: NSFFPH | Send this to the developer!";
    }
}
