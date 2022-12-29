package me.xaxis.reputation;

public enum Lang {
    SENDER_NOT_PLAYER("Lang.SENDER_NOT_PLAYER"),
    ;

    Lang(String path){
        this.path = path;
    }

    private final String path;

    public String getMsg(Reputation plugin){
        return plugin.getConfig().getString(path);
    }

}
