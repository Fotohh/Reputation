package me.xaxis.reputation.handle;

import me.xaxis.reputation.ReputationMain;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.*;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public class SqliteUtility {

    private static String URL;
    private final Plugin plugin;
    private Connection connection;
    private DatabaseMetaData meta;

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
        plugin.getLogger().log(Level.INFO, "Preparing to create table...");
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, ()->{
            plugin.getLogger().log(Level.INFO, "Creating table...");
            try{
                if(connection == null){
                    this.connection = DriverManager.getConnection(URL);
                    plugin.getLogger().log(Level.INFO, "Connection was null, resetting connection...");
                }
                Statement stmt = connection.createStatement();
                stmt.setQueryTimeout(100);
                stmt.execute("""
                        CREATE TABLE IF NOT EXISTS reputation (
                         uuid text PRIMARY KEY NOT NULL,
                         likes INT NOT NULL,
                         dislikes INT NOT NULL,
                         total INT NOT NULL,
                         ratio FLOAT NOT NULL,
                         capacity INT NOT NULL
                        );""");
                stmt.close();
            } catch (SQLException e) {
                throw new RuntimeException("Unable to create table", e);
            }
        },5);
    }

    public void createPlayerReputationEntry(UUID uuid){

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()->{
            try{
                PreparedStatement pstmt = connection.prepareStatement(
                        "insert into reputation (uuid, likes, dislikes, total, ratio, capacity) " +
                                "values (?,?,?,?,?,?) on conflict do nothing;");
                pstmt.setString(1, uuid.toString());
                pstmt.setInt(2, 0);
                pstmt.setInt(3, 0);
                pstmt.setInt(4, 0);
                pstmt.setFloat(5, 0);
                pstmt.setInt(6, 69696969);
                pstmt.executeUpdate();
                pstmt.close();
            } catch (SQLException e) {
                throw new RuntimeException("Unable to create player reputation entry",e);
            }
        });

    }

    public int getRatio(Player player){

        AtomicInteger atomicInteger = new AtomicInteger(0);

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()->{
            try{
                updateValues(player);
                PreparedStatement stmt = connection.prepareStatement(
                        "SELECT ratio from reputation where uuid = ?"
                );
                stmt.setString(1, player.getUniqueId().toString());
                stmt.execute();
                atomicInteger.set(Math.round(stmt.getResultSet().getFloat("ratio")));
                stmt.close();
            } catch (SQLException e) {
                throw new RuntimeException("Unable to retrieve player like-dislike ratio",e);
            }
        });

        return atomicInteger.get();
    }

    public int getTotalReputation(Player player){
        AtomicInteger integer = new AtomicInteger(0);

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()->{
            try{
                updateValues(player);
                PreparedStatement stmt = connection.prepareStatement(
                        "SELECT total from reputation where uuid = ?"
                );
                stmt.setString(1, player.getUniqueId().toString());
                stmt.execute();
                integer.set(stmt.getResultSet().getInt("total"));
                stmt.close();
            } catch (SQLException e) {
                throw new RuntimeException("Unable to retrieve player's total reputation",e);
            }
        });

        return integer.get();
    }

    private void updateValues(Player player) throws SQLException {

        PreparedStatement stmt = connection.prepareStatement(
                "UPDATE reputation set total = likes + dislikes where uuid = ?;"
        );
        stmt.setString(1, player.getUniqueId().toString());
        stmt.executeUpdate();
        stmt.close();

        PreparedStatement stmt2 = connection.prepareStatement(
                "SELECT likes from reputation where uuid = ?;"
        );
        stmt2.setString(1, player.getUniqueId().toString());
        stmt2.execute();
        int likes = stmt2.getResultSet().getInt("likes");
        plugin.getLogger().log(Level.INFO, "Likes : "+likes);
        stmt2.close();

        PreparedStatement stmt3 = connection.prepareStatement(
                "SELECT dislikes from reputation where uuid = ?;"
        );
        stmt3.setString(1, player.getUniqueId().toString());
        stmt3.execute();
        int dislikes = stmt3.getResultSet().getInt("dislikes");
        plugin.getLogger().log(Level.INFO, "Dislikes : "+dislikes);
        stmt3.close();

        double max = Math.max(likes,dislikes);
        double min = Math.min(likes,dislikes);
        PreparedStatement s = connection.prepareStatement(
                "UPDATE reputation set ratio = ?/?*100.0 where uuid = ?;"
        );
        s.setDouble(1, min);
        s.setDouble(2, max);
        s.setString(3, player.getUniqueId().toString());
        s.executeUpdate();
        s.close();

        plugin.getLogger().log(Level.INFO, "Ratio : "+min/max*100);
        plugin.getLogger().log(Level.INFO, "likes : "+likes + " dislikes : "+dislikes);
        plugin.getLogger().log(Level.INFO, "Min : " + min + " Max : "+max);

    }

    public void addLike(Player player, int i){
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()->{
            try{
                PreparedStatement stmt = connection.prepareStatement(
                        """
                                update reputation set likes = reputation.likes + ? where uuid = ?;"""
                );
                stmt.setInt(1, i);
                stmt.setString(2, player.getUniqueId().toString());
                stmt.executeUpdate();
                stmt.close();
                updateValues(player);
            }catch (SQLException e){
                throw new RuntimeException("Unable to add a like to "+player.getName(), e);
            }
        });
    }

    public void addDislike(Player player, int i){
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()->{
            try{
                PreparedStatement stmt = connection.prepareStatement(
                        """
                                update reputation set dislikes = reputation.dislikes + ? where uuid = ?;"""
                );
                stmt.setInt(1, i);
                stmt.setString(2, player.getUniqueId().toString());
                stmt.executeUpdate();
                stmt.close();
                updateValues(player);
            }catch (SQLException e){
                throw new RuntimeException("Unable to add a dislike to "+player.getName(), e);
            }
        });
    }

    public void setDislikes(Player player, int i){
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()->{
            try{
                PreparedStatement stmt = connection.prepareStatement(
                        """
                                UPDATE reputation SET dislikes = ? where uuid = ?;"""
                );
                stmt.setInt(1, i);
                stmt.setString(2, player.getUniqueId().toString());
                stmt.executeUpdate();
                updateValues(player);
            } catch (SQLException e) {
                throw new RuntimeException("Unable to set dislikes for "+player.getName(),e);
            }
        });
    }

    public void setLikes(Player player, int i){
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()->{
            try{
                PreparedStatement stmt = connection.prepareStatement(
                        """
                                update reputation set likes = ? where uuid = ?;
                                """
                );
                stmt.setInt(1, i);
                stmt.setString(2, player.getUniqueId().toString());
                stmt.executeUpdate();
                stmt.close();
                updateValues(player);
            } catch (SQLException e) {
                throw new RuntimeException("Unable to set likes for "+player.getName(),e);
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
                throw new RuntimeException("Unable to get likes for "+player.getName(),e);
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
                throw new RuntimeException("Unable to get dislikes for "+player.getName(),e);
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
                }
            } catch (SQLException e) {
                throw new RuntimeException("Unable to create a database",e);
            }
        });
    }

}
