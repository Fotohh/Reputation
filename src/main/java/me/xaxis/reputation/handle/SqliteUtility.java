package me.xaxis.reputation.handle;

import me.xaxis.reputation.Reputation;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.*;
import java.util.UUID;

public class SqliteUtility {

    private static String URL;
    private static String PATH;
    private final Plugin plugin;
    private Connection connection;
    private DatabaseMetaData meta;

    public SqliteUtility(Reputation plugin){
        this.plugin = plugin;
        if(!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
        URL = "jdbc:sqlite:" + plugin.getDataFolder() + "/Data/reputation.db";
        PATH = plugin.getDataFolder() + "/Data/reputation.db";
        try {
            connect();
        }catch (Exception ignored){
            create();
        }
        createTable();
    }

    private void connect(){
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()->{
            Connection connection = null;
            try{
                connection = DriverManager.getConnection(URL);
                this.connection = connection;
                meta = connection.getMetaData();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        });
    }

    public void disconnect(){
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()->{
            try {
                if(connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void createTable(){
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()->{
            String sql =
                      "CREATE TABLE IF NOT EXISTS reputation ("
                    + " uuid text PRIMARY KEY,"
                    + " likes INT NOT NULL,"
                    + " dislikes INT NOT NULL"
                    + ");";
            try{
                Statement stmt = connection.createStatement();
                stmt.execute(sql);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void createPlayerReputationEntry(UUID uuid){
        String sql = "INSERT INTO reputation(uuid, likes, dislikes) VALUES(?,?,?)";

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()->{
            try{
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, uuid.toString());
                pstmt.setInt(2, 0);
                pstmt.setInt(3, 0);
                pstmt.execute();
                pstmt.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

    }

    public void setLikes(Player player, int amt){
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()->{
            try{
                PreparedStatement stmt = connection.prepareStatement(
                        "UPDATE likes SET likes = ? WHERE uuid = ?"
                );

                stmt.setInt(2, amt);
                stmt.setString(1, player.getUniqueId().toString());
                stmt.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
    public void setDislikes(Player player, int amt){
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()->{
            try{
                PreparedStatement stmt = connection.prepareStatement(
                        "UPDATE dislikes SET dislikes = ? WHERE uuid = ?"
                );

                stmt.setString(1, player.getUniqueId().toString());
                stmt.setInt(2, amt);
                stmt.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void create(){
        File file = new File(PATH);
        if(file.exists()) return;

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()-> {

            try {
                connection = DriverManager.getConnection(URL);
                if(connection != null){
                    meta = connection.getMetaData();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        });
    }

}
