package me.xaxis.reputation;

import me.xaxis.reputation.commands.ReputationCommand;
import me.xaxis.reputation.events.onJoin;
import me.xaxis.reputation.handle.SqliteUtility;
import me.xaxis.reputation.papi.Reputation;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

public final class ReputationMain extends JavaPlugin {

    //d9f6734f-85f7-4133-9f10-8aa54542b06c

    //CORRECT SOLUTION: Score = Lower bound of Wilson score confidence interval for a Bernoulli parameter (for ratio)
    //https://en.wikipedia.org/wiki/Binomial_proportion_confidence_interval
    //https://www.evanmiller.org/how-not-to-sort-by-average-rating.html

    private SqliteUtility sqliteUtility;

    public SqliteUtility getSqliteUtility() {
        if(sqliteUtility != null) return sqliteUtility; else return new SqliteUtility(this);
    }

    @Override
    public void onEnable() {

        sqliteUtility = new SqliteUtility(this);

        if(getServer().getPluginManager().getPlugin("PlaceHolderAPI") == null){
                getLogger().log(Level.SEVERE,"PlaceholderAPI does not exist!");
                getPluginLoader().disablePlugin(this);
        }

        saveDefaultConfig();

        new ReputationCommand(this);
        new Reputation(this).register();
        new onJoin(this);

    }


    @Override
    public void onDisable() {
        try {
            sqliteUtility.disconnect();
        } catch (SQLException e) {
            getLogger().log(Level.SEVERE, "Unable to disconnect from SQLite Server");
        }
    }
}
