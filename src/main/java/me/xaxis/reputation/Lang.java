package me.xaxis.reputation;

public enum Lang {
    SENDER_NOT_PLAYER("Lang.SENDER_NOT_PLAYER"),
    PLAYER_CMD_TIMEOUT("Lang.PLAYER_CMD_TIMEOUT"),
    PLAYER_IS_NULL("Lang.PLAYER_IS_INVALID"),
    PLAYER_REPUTATION("Lang.PLAYER_REPUTATION"),
    LIKED_PLAYER("Lang.LIKED_PLAYER"),
    DISLIKED_PLAYER("Lang.DISLIKED_PLAYER"),
    SET_PLAYER_LIKES("Lang.SET_PLAYER_LIKES"),
    SET_PLAYER_DISLIKES("Lang.SET_PLAYER_DISLIKES"),
    ARGUMENT_NOT_NUMBER("Lang.ARGUMENT_NOT_NUMBER"),
    INVALID_USAGE("Lang.INVALID_USAGE"),
    NO_PERMISSION("Lang.NO_PERMISSION"),
    CANNOT_LIKE_SELF("Lang.CANNOT_LIKE_SELF"),
    CANNOT_DISLIKE_SELF("Lang.CANNOT_DISLIKE_SELF"),
    ;

    Lang(String path){
        this.path = path;
    }

    private final String path;

    public String getMsg(ReputationMain plugin){
        return plugin.getConfig().getString(path);
    }

}
