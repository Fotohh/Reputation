package me.xaxis.reputation.handle;

import me.xaxis.reputation.Reputation;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

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
        String FOLDER = plugin.getDataFolder() + "/Data/";

        if(!plugin.getDataFolder().exists()){

            plugin.getDataFolder().mkdirs();

        }

        File folder = new File(FOLDER);

        if(!folder.exists()){

            folder.mkdirs();

        }

        File file = new File(PATH);

        if(file.exists()){

            connect();

        }else{

            create();

        }
        createTable();
    }

    private void connect(){
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()->{
            try{
                connection = DriverManager.getConnection(URL);
                this.meta = connection.getMetaData();
                this.connection.setAutoCommit(false);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        });
    }

    public void disconnect() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createTable(){
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, ()->{
            try{
                Statement stmt = connection.createStatement();
                stmt.execute("""
                        CREATE TABLE IF NOT EXISTS reputation (
                         uuid text PRIMARY KEY,
                         likes INT NOT NULL,
                         dislikes INT NOT NULL
                        );""");
                stmt.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, 5);
    }

    public void createPlayerReputationEntry(UUID uuid){

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()->{
            try{
                PreparedStatement pstmt = connection.prepareStatement(
                        "INSERT INTO reputation(uuid, likes, dislikes) VALUES(?,?,?) on conflict do nothing;");
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

    public boolean entryExists(UUID uuid){

        AtomicBoolean bool = new AtomicBoolean(false);
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()->{
            try{
                PreparedStatement stmt = connection.prepareStatement(
                        "SELECT EXISTS(select * from reputation where uuid = ?);"
                );
                stmt.setString(1, uuid.toString());
                bool.set(stmt.executeQuery().getInt(1) == 1);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        return bool.get();
    }

    public void setLikes(Player player, int amt){
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()->{
            try{
                PreparedStatement stmt = connection.prepareStatement(
                        "UPDATE reputation SET likes = ? WHERE uuid = ?"
                );

                stmt.setInt(1, amt);
                stmt.setString(2, player.getUniqueId().toString());
                stmt.execute();
                stmt.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public int getLikes(Player player){

        try {
            plugin.getLogger().log(Level.INFO, String.valueOf(connection.isClosed()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        AtomicInteger likes = new AtomicInteger();

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()->{
            try{
                PreparedStatement stmt = connection.prepareStatement(
                        "SELECT likes from reputation where uuid = ?" //TODO error
                );
                stmt.setString(1, player.getUniqueId().toString()); //TODO error
                likes.set(stmt.executeQuery().getInt("likes"));
                stmt.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        return likes.get();
    }

    public int getDislikes(Player player){

        try {
            plugin.getLogger().log(Level.INFO, String.valueOf(connection.isClosed()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        AtomicInteger dislikes = new AtomicInteger();

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()->{
            try{
                PreparedStatement stmt = connection.prepareStatement(
                        "SELECT dislikes from reputation where uuid = ?" //TODO error
                );
                stmt.setString(1, player.getUniqueId().toString()); //TODO error
                dislikes.set(stmt.executeQuery().getInt("dislikes"));
                stmt.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        return dislikes.get();
    }

    public void setDislikes(Player player, int amt){
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()->{
            try{
                PreparedStatement stmt = connection.prepareStatement(
                        "UPDATE reputation SET dislikes = ? WHERE uuid = ?"
                );

                stmt.setString(2, player.getUniqueId().toString());
                stmt.setInt(1, amt);
                stmt.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void create() {

        File file = new File(PATH);
        if(file.exists()) return;

        try {
            file.createNewFile();
        }catch (IOException e){
            e.printStackTrace();
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()-> {

            try {
                this.connection = DriverManager.getConnection(URL);
                if(this.connection != null){
                    this.meta = connection.getMetaData();
                    this.connection.setAutoCommit(false);
                }
                plugin.getLogger().log(Level.INFO, meta.getURL());
                plugin.getLogger().log(Level.INFO,meta.getConnection().toString());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        });
    }

}
