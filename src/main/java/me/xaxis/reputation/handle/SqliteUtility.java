package me.xaxis.reputation.handle;

import me.xaxis.reputation.ReputationMain;

import java.io.File;
import java.sql.*;
import java.util.UUID;
import java.util.logging.Level;

public class SqliteUtility {

    private static String URL;
    private final ReputationMain plugin;
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
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()->{
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
        });
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

    public long getTimestamp(UUID uuid) throws SQLException {
        PlayerReputationManager m = PlayerReputationManager.containsPlayer(uuid) ? PlayerReputationManager.getPlayerReputationManager(uuid) : new PlayerReputationManager(uuid, plugin);
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()->{
            try{
                PreparedStatement stmt = connection.prepareStatement(
                        "SELECT reputation.plts from reputation where uuid = ?"
                );
                stmt.setString(1, uuid.toString());
                ResultSet s = stmt.executeQuery();
                m.setTimestamp(s.getLong("plts"));
                stmt.close();
            } catch (SQLException e) {
                throw new RuntimeException("Unable to get timestamp from database ",e);
            }
        });
        return m.getPlayerTimestamp();
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

    public int getLikes(UUID uuid) throws SQLException {

        PlayerReputationManager m = PlayerReputationManager.containsPlayer(uuid) ? PlayerReputationManager.getPlayerReputationManager(uuid) : new PlayerReputationManager(uuid, plugin);

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()->{
            try{
                PreparedStatement stmt = connection.prepareStatement(
                        "SELECT reputation.likes from reputation where uuid = ?"
                );
                stmt.setString(1, uuid.toString());

                ResultSet s = stmt.executeQuery();

                m.setLikes(s.getInt("likes"));
                plugin.getLogger().log(Level.INFO,String.valueOf(m.getLikes()));

                stmt.close();
            } catch (SQLException e) {
                throw new RuntimeException("Unable to get likes from database ",e);
            }
        });

        return m.getLikes();
    }

    public int getDislikes(UUID uuid) throws SQLException {

        PlayerReputationManager m = PlayerReputationManager.containsPlayer(uuid) ? PlayerReputationManager.getPlayerReputationManager(uuid) : new PlayerReputationManager(uuid, plugin);

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()->{
            try{
                PreparedStatement stmt = connection.prepareStatement(
                        "select reputation.dislikes from reputation where uuid = ?"
                );
                stmt.setString(1, uuid.toString());

                ResultSet s = stmt.executeQuery();

                m.setDislikes(s.getInt("dislikes"));

                stmt.close();
            } catch (SQLException e) {
                throw new RuntimeException("Unable to get dislikes from SQL Database",e);
            }
        });

        return m.getDislikes();
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
