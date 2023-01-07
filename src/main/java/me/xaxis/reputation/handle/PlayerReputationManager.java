package me.xaxis.reputation.handle;

import me.xaxis.reputation.ReputationMain;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

public class PlayerReputationManager{

    private final Player player;
    private final ReputationMain plugin;
    private int total;
    private int likes;
    private int dislikes;
    private double percentage;
    private String color;
    private long timestamp;

    private static HashMap<UUID,PlayerReputationManager> map = new HashMap<>();

    public static PlayerReputationManager getPlayerReputationManager(Player player){
        return map.get(player.getUniqueId());
    }
    public static boolean containsPlayer(Player player){
        return map.containsKey(player.getUniqueId());
    }

    public PlayerReputationManager(Player player, ReputationMain plugin) {
        this.player = player;
        this.plugin = plugin;
        map.put(player.getUniqueId(), this);
        cacheData();
    }

    public int getTotal() {
        return total;
    }
    public int getDislikes() {
        return dislikes;
    }
    public int getLikes() {
        return likes;
    }
    public double getPercentage() {
        return percentage;
    }
    public String getColor() {
        return color;
    }
    public long getPlayerTimestamp(){
        return timestamp;
    }

    public void cacheData(){
        cacheTotal();
        cachePercentage();
        cacheLikes();
        cacheDislikes();
        cacheColor();
        cacheTimestamp();
    }

    private void cacheTotal(){
        this.total = plugin.getSqliteUtility().getTotalReputation(player);
    }
    private void cacheLikes(){
        this.likes = plugin.getSqliteUtility().getLikes(player);
    }
    private void cacheDislikes(){
        this.dislikes = plugin.getSqliteUtility().getDislikes(player);
    }
    private void cachePercentage(){
        this.percentage = plugin.getSqliteUtility().getRatio(player);
    }
    private void cacheColor(){
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("thresholds");

        ArrayList<Integer> values = new ArrayList<>();

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
            return;
        }

        for(int i = 0; i < values.size(); i++){

            if(values.size() < 2) {
                this.color = section.getString(String.valueOf(values.get(i)));
                return;
            }
                //20 > 5 && 4 == 5
            if(getTotal() > values.get(i) && values.size() -1 == i){
                this.color = section.getString(String.valueOf(values.get(i)));
                return;
            }

            if(getTotal() > values.get(i) && getTotal() < values.get(i+1)){
                this.color = section.getString(String.valueOf(values.get(i)));
                return;
            }

        }
    }
    private void cacheTimestamp(){
        this.timestamp = plugin.getSqliteUtility().getTimestamp(player);
    }
}
