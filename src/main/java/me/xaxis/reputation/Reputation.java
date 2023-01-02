package me.xaxis.reputation;

import me.xaxis.reputation.commands.ReputationCommand;
import me.xaxis.reputation.handle.SqliteUtility;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class Reputation extends JavaPlugin {

    private SqliteUtility sqliteUtility;

    public SqliteUtility getSqliteUtility() {
        if(sqliteUtility != null) {
            return sqliteUtility;
        }
        return null;
    }

    @Override
    public void onEnable() {

        sqliteUtility = new SqliteUtility(this);

        if(getServer().getPluginManager().getPlugin("PlaceHolderAPI") == null){
                System.out.println("PlaceholderAPI does not exist!");
                getPluginLoader().disablePlugin(this);
        }

        saveDefaultConfig();

        new ReputationCommand(this);
    }


    @Override
    public void onDisable() {
        sqliteUtility.disconnect();
    }
}
