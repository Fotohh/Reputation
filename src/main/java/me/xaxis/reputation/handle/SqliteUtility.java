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

@SuppressWarnings("all")
public class SqliteUtility {

    private static String URL;
    private final Plugin plugin;
    private Connection connection;
    private DatabaseMetaData meta;

    public SqliteUtility(Reputation plugin){

        this.plugin = plugin;

        if(!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();

        URL = "jdbc:sqlite:" + plugin.getDataFolder() + "/Data/reputation.db";
        String FOLDER = plugin.getDataFolder() + "/Data/";

        plugin.getDataFolder().mkdirs();

        new File(FOLDER).mkdirs();

        try {
            connect();
        }catch (Exception e){
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
                e.printStackTrace();
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
                stmt.setQueryTimeout(100);
                stmt.execute("""
                        CREATE TABLE IF NOT EXISTS reputation (
                         uuid text PRIMARY KEY NOT NULL,
                         likes INT NOT NULL,
                         dislikes INT NOT NULL,
                         total INT NOT NULL,
                         ratio FLOAT NOT NULL
                        );""");
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        },5);
    }

    public void createPlayerReputationEntry(UUID uuid){

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()->{
            try{
                PreparedStatement pstmt = connection.prepareStatement(
                        "insert into reputation (uuid, likes, dislikes, total, ratio)\n" +
                                "values (?,?,?,?,?) on conflict do update set likes = likes + ? and dislikes = dislikes + ? and total = likes + dislikes and ratio = likes - dislikes / likes + dislikes;");
                pstmt.setString(1, uuid.toString());
                pstmt.setInt(2, 0);
                pstmt.setInt(3, 0);
                pstmt.setInt(4, 0);
                pstmt.setFloat(5, 0);
                pstmt.setInt(6, 0);
                pstmt.setInt(7, 0);
                pstmt.executeUpdate();
                pstmt.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

    }

    public int getRatio(Player player){

        AtomicInteger atomicInteger = new AtomicInteger(0);

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()->{
            try{
                PreparedStatement stmt = connection.prepareStatement(
                        "SELECT ratio from reputation where uuid = ?"
                );
                stmt.setString(1, player.getUniqueId().toString());
                stmt.execute();
                atomicInteger.set(Math.round(stmt.getResultSet().getFloat("ratio")));
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        return atomicInteger.get();
    }

    public int getTotalReputation(Player player){
        AtomicInteger integer = new AtomicInteger(0);

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()->{
            try{
                PreparedStatement stmt = connection.prepareStatement(
                        "SELECT total from reputation where uuid = ?"
                );
                stmt.setString(1, player.getUniqueId().toString());
                stmt.execute();
                integer.set(stmt.getResultSet().getInt("total"));
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        return integer.get();
    }

    public boolean entryExists(UUID uuid){

        AtomicBoolean bool = new AtomicBoolean(false);
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()->{
            try{
                PreparedStatement stmt = connection.prepareStatement(
                        "SELECT EXISTS(select * from reputation where uuid = ?);"
                );
                stmt.setString(1, uuid.toString());
                stmt.execute();
                bool.set(stmt.getResultSet().getInt(1) == 1);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        return bool.get();
    }

    public void addLike(Player player, int i){
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()->{
            try{
                PreparedStatement stmt = connection.prepareStatement(
                        "update reputation set likes = likes + ? and total = ? + dislikes and ratio = ? - dislikes / ? + dislikes where uuid = ?;"
                );
                for(int e = 1; e != 4; e++){
                    stmt.setInt(e, i);
                }
                stmt.setString(5, player.getUniqueId().toString());
                stmt.executeUpdate();
                stmt.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        });
    }

    public void addDislike(Player player, int i){
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()->{
            try{
                PreparedStatement stmt = connection.prepareStatement(
                        "update reputation set dislikes = dislikes + ? and total = likes + ?  and ratio = likes - ? / likes + ? where uuid = ?;"
                );
                for(int e = 1; e != 4; e++){
                    stmt.setInt(e, i);
                }
                stmt.setString(5, player.getUniqueId().toString());
                stmt.executeUpdate();
                stmt.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        });
    }

    public void setDislikes(Player player, int amt){
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()->{
            try{
                PreparedStatement stmt = connection.prepareStatement(
                        "UPDATE reputation SET dislikes = ? and total = likes + ? and ratio = likes - ? / likes + ? WHERE uuid = ?;"
                );
                for(int i = 1; i != 4; i++){
                    stmt.setInt(i, amt);
                }
                stmt.setString(5, player.getUniqueId().toString());
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void setLikes(Player player, int amt){
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()->{
            try{
                PreparedStatement stmt = connection.prepareStatement(
                        "update reputation set likes = ? and total = ? + dislikes and ratio = ? - dislikes / ? + dislikes where uuid = ?;"
                );
                for(int i = 1; i != 4; i++){
                    stmt.setInt(i, amt);
                }
                stmt.setString(2, player.getUniqueId().toString());
                stmt.executeUpdate();
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public int getLikes(Player player){

        AtomicInteger likes = new AtomicInteger();

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()->{
            try{
                PreparedStatement stmt = connection.prepareStatement(
                        "SELECT likes from reputation where uuid = ?" //TODO error
                );
                stmt.setString(1, player.getUniqueId().toString()); //TODO error
                stmt.execute();
                likes.set(stmt.getResultSet().getInt("likes")); //TODO error
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        return likes.get();
    }

    public int getDislikes(Player player){

        AtomicInteger dislikes = new AtomicInteger();

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()->{
            try{
                PreparedStatement stmt = connection.prepareStatement(
                        "SELECT dislikes from reputation where uuid = ?" //TODO error
                );
                stmt.setString(1, player.getUniqueId().toString()); //TODO error
                stmt.execute();
                dislikes.set(stmt.getResultSet().getInt("dislikes")); //TODO error
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        return dislikes.get();
    }

    private void create() {
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
