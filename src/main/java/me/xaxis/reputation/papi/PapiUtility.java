package me.xaxis.reputation.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.xaxis.reputation.Reputation;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PapiUtility extends PlaceholderExpansion {

    private final Reputation plugin;

    public PapiUtility(Reputation plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "reputation";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Xaxis";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        switch (params){
            case "total"->{

            }
            case "likes"->{

            }
            case "dislikes"->{

            }
            case "percentage"->{

            }
            case "color"->{

            }
        }
    }
}
