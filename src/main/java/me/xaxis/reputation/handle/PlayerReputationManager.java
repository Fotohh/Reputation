package me.xaxis.reputation.handle;

import me.xaxis.reputation.ReputationMain;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

public class PlayerReputationManager{

    private final UUID uuid;
    private final ReputationMain plugin;
    private int likes;
    private int dislikes;
    private String color;
    private long timestamp;

    private static final HashMap<UUID,PlayerReputationManager> map = new HashMap<>();

    public static PlayerReputationManager getPlayerReputationManager(UUID uuid){
        return map.get(uuid);
    }
    public static boolean containsPlayer(UUID uuid){
        return map.containsKey(uuid);
    }

    public PlayerReputationManager(UUID uuid, ReputationMain plugin) throws SQLException {
        this.uuid = uuid;
        this.plugin = plugin;
        plugin.getSqliteUtility().createPlayerReputationEntry(uuid);
        map.putIfAbsent(uuid, this);
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }
    public void setLikes(int likes) {
        this.likes = likes;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getTotal() {
        return likes+dislikes;
    }
    public int getDislikes() {
        return dislikes;
    }
    public int getLikes() {
        return likes;
    }
    public double getPercentage() {
        double min = Math.min(likes,dislikes);
        double max = Math.max(likes,dislikes);
        return min/max*100;
    }
    public String getColor() {
        return color;
    }
    public long getPlayerTimestamp(){
        return timestamp;
    }

    public void cacheData() throws SQLException {
        cacheLikes();
        cacheDislikes();
        cacheColor();
        cacheTimestamp();
    }
    private void cacheLikes() throws SQLException {
        this.likes = plugin.getSqliteUtility().getLikes(uuid);
    }
    private void cacheDislikes() throws SQLException {
        this.dislikes = plugin.getSqliteUtility().getDislikes(uuid);
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
    private void cacheTimestamp() throws SQLException {
        this.timestamp = plugin.getSqliteUtility().getTimestamp(uuid);
    }
}
