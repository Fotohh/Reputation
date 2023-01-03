package me.xaxis.reputation;

import me.xaxis.reputation.commands.ReputationCommand;
import me.xaxis.reputation.events.onJoin;
import me.xaxis.reputation.handle.SqliteUtility;
import me.xaxis.reputation.papi.PapiUtility;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class Reputation extends JavaPlugin {

    //d9f6734f-85f7-4133-9f10-8aa54542b06c

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
                getLogger().log(Level.SEVERE,"PlaceholderAPI does not exist!");
                getPluginLoader().disablePlugin(this);
        }

        saveDefaultConfig();

        new ReputationCommand(this);
        new PapiUtility(this).register();
        new onJoin(this);

    }


    @Override
    public void onDisable() {
        sqliteUtility.disconnect();
    }
}
