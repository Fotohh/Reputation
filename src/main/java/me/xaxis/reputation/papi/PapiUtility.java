package me.xaxis.reputation.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.xaxis.reputation.Reputation;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.logging.Level;

public class PapiUtility extends PlaceholderExpansion {

    private final Reputation plugin;

    public PapiUtility(Reputation plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "reputation";
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
    public boolean canRegister() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        switch (params){
            case "total"->{
                return String.valueOf(plugin.getSqliteUtility().getLikes(player) + plugin.getSqliteUtility().getDislikes(player));
            }
            case "likes"->{
                return String.valueOf(plugin.getSqliteUtility().getLikes(player));
            }
            case "dislikes"->{
                return String.valueOf(plugin.getSqliteUtility().getDislikes(player));
            }
            case "percentage"->{
                //likes - dislikes / likes + dislikes (percentage ratio)
                int likes = plugin.getSqliteUtility().getLikes(player);
                int dislikes = plugin.getSqliteUtility().getDislikes(player);

                return String.valueOf((likes - dislikes) / (likes + dislikes));
            }
            case "color"->{

                ConfigurationSection section = plugin.getConfig().getConfigurationSection("thresholds");

                ArrayList<Integer> values = new ArrayList<>();

                int reputation = plugin.getSqliteUtility().getDislikes(player) + plugin.getSqliteUtility().getLikes(player);

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
                }

                for(int i = 0; i < values.size(); i++){

                    if(values.size() < 2){
                        String s = String.valueOf(values.get(i));
                        return section.getString(s);
                    }

                    if(reputation > values.get(i) && reputation < values.get(i+1)){
                        String s = String.valueOf(values.get(i));
                        return section.getString(s);
                    }

                }

                return "Error! Code: WNATFC | Send this to the developer!";

            }

        }
        return "Error! Code: NSFFPH | Send this to the developer!";
    }
}
