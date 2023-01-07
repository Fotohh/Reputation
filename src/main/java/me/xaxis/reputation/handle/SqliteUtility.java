package me.xaxis.reputation.handle;

import me.xaxis.reputation.ReputationMain;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.*;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;

public class SqliteUtility {

    private static String URL;
    private final Plugin plugin;
    private Connection connection;

    public SqliteUtility(ReputationMain plugin){

        this.plugin = plugin;

        plugin.getDataFolder().mkdirs();

        URL = "jdbc:sqlite:" + plugin.getDataFolder() + "/Data/reputation.db";
        String FOLDER = plugin.getDataFolder() + "/Data/";

        new File(FOLDER).mkdirs();

        create();

        plugin.getLogger().log(Level.WARNING, "Connecting to database");

        createTable();
    }

    public void disconnect() throws SQLException {
        if (connection == null) return;
        connection.close();
    }

    private void createTable(){
        plugin.getLogger().log(Level.INFO, "Preparing to initialize table...");
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, ()->{
            plugin.getLogger().log(Level.INFO, "Initializing...");
            try{
                while(connection == null){
                    this.connection = DriverManager.getConnection(URL);
                    plugin.getLogger().log(Level.INFO, "Connection was null, resetting connection...");
                }
                Statement stmt = connection.createStatement();
                stmt.setQueryTimeout(100);
                stmt.execute("""
                        CREATE TABLE IF NOT EXISTS reputation (
                         uuid text PRIMARY KEY NOT NULL,
                         likes INTEGER,
                         dislikes INTEGER,
                         plts LONG
                        );""");
                stmt.close();
            } catch (SQLException e) {
                throw new RuntimeException("Unable to create table", e);
            }
        },2);
    }

    public void createPlayerReputationEntry(UUID uuid){

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()->{
            try{
                PreparedStatement pstmt = connection.prepareStatement(
                        "insert into reputation (uuid, likes, dislikes, plts) values (?,?,?,?) on conflict do nothing");
                pstmt.setString(1, uuid.toString());
                pstmt.setInt(2, 0);
                pstmt.setInt(3, 0);
                pstmt.setLong(4, 0L);
                pstmt.execute();
                pstmt.close();
            } catch (SQLException e) {
                throw new RuntimeException("Unable to create player reputation entry",e);
            }
        });

    }

    public long getTimestamp(UUID uuid){
        AtomicLong l = new AtomicLong(0);
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()->{
            try{
                PreparedStatement stmt = connection.prepareStatement(
                        "SELECT plts from table ( reputation ) where uuid = ?"
                );
                stmt.setString(1, uuid.toString());
                stmt.execute();
                ResultSet s = stmt.getResultSet();
                l.set(s.getLong("plts"));
                stmt.close();
            } catch (SQLException e) {
                throw new RuntimeException("Unable to get timestamp from database ",e);
            }
        });
        return l.get();
    }

    public void setTimestamp(UUID uuid, long currentTimestamp){
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()->{
            try{
                PreparedStatement stmt = connection.prepareStatement(
                        "UPDATE reputation set plts = ? where uuid = ?"
                );
                stmt.setLong(1, currentTimestamp);
                stmt.setString(2, uuid.toString());
                stmt.execute();
            } catch (SQLException e) {
                throw new RuntimeException("Unable to create timestamp in sql database", e);
            }
        });
    }

    public void addLike(UUID uuid, int i){
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()->{
            try{
                PreparedStatement stmt = connection.prepareStatement(
                        """
                                update reputation set likes = likes + ? where uuid = ?"""
                );
                stmt.setInt(1, i);
                stmt.setString(2, uuid.toString());
                stmt.execute();
                stmt.close();
            }catch (SQLException e){
                throw new RuntimeException("Unable to add a like to sql database", e);
            }
        });
    }

    public void addDislike(UUID uuid, int i){
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()->{
            try{
                PreparedStatement stmt = connection.prepareStatement(
                        """
                                update reputation set dislikes = dislikes + ? where uuid = ?"""
                );
                stmt.setInt(1, i);
                stmt.setString(2, uuid.toString());
                stmt.execute();
                stmt.close();
            }catch (SQLException e){
                throw new RuntimeException("Unable to add a dislike in sql database", e);
            }
        });
    }

    public void setDislikes(UUID uuid, int i){
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()->{
            try{
                PreparedStatement stmt = connection.prepareStatement(
                        """
                                UPDATE reputation SET dislikes = ? where uuid = ?"""
                );
                stmt.setInt(1, i);
                stmt.setString(2, uuid.toString());
                stmt.execute();
            } catch (SQLException e) {
                throw new RuntimeException("Unable to set dislikes in sql database",e);
            }
        });
    }

    public void setLikes(UUID uuid, int i){
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()->{
            try{
                PreparedStatement stmt = connection.prepareStatement(
                        """
                                update reputation set likes = ? where uuid = ?
                                """
                );
                stmt.setInt(1, i);
                stmt.setString(2, uuid.toString());
                stmt.execute();
                stmt.close();
            } catch (SQLException e) {
                throw new RuntimeException("Unable to set likes from database",e);
            }
        });
    }

    public int getLikes(UUID uuid){

        AtomicInteger likes = new AtomicInteger();

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()->{
            try{
                PreparedStatement stmt = connection.prepareStatement(
                        "SELECT likes from table ( reputation ) where uuid = ?" //TODO error
                );
                stmt.setString(1, uuid.toString()); //TODO error
                stmt.execute();
                likes.set(stmt.getResultSet().getInt("likes")); //TODO error
                stmt.close();
            } catch (SQLException e) {
                throw new RuntimeException("Unable to get likes from database ",e);
            }
        });

        return likes.get();
    }

    public int getDislikes(UUID uuid){

        AtomicInteger dislikes = new AtomicInteger();

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()->{
            try{
                PreparedStatement stmt = connection.prepareStatement(
                        "SELECT dislikes from table ( reputation ) where uuid = ?" //TODO error
                );
                stmt.setString(1, uuid.toString()); //TODO error
                stmt.execute();
                dislikes.set(stmt.getResultSet().getInt("dislikes")); //TODO error
                stmt.close();
            } catch (SQLException e) {
                throw new RuntimeException("Unable to get dislikes from SQL Database",e);
            }
        });

        return dislikes.get();
    }

    private void create() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()-> {
            try {
                this.connection = DriverManager.getConnection(URL);
            } catch (SQLException e) {
                throw new RuntimeException("Unable to create a database",e);
            }
        });
    }

}
