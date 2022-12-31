package me.xaxis.reputation;

import me.xaxis.reputation.handle.SqliteUtility;
import org.bukkit.plugin.java.JavaPlugin;

public final class Reputation extends JavaPlugin {

    private SqliteUtility sqliteUtility;

    public SqliteUtility getSqliteUtility() {
        if(sqliteUtility != null) return sqliteUtility; else return null;
    }

    @Override
    public void onEnable() {
        sqliteUtility = new SqliteUtility(this);
        saveDefaultConfig();

    }


    @Override
    public void onDisable() {
        sqliteUtility.disconnect();
    }
}
