package com.github.alfonsoleandro.corona.utils;

import com.github.alfonsoleandro.mputils.misc.MessageEnum;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum Message implements MessageEnum {
    NO_PERMISSION("&cNo permission"),
    RELOADED("&aPlugin reloaded!"),
    UNKNOWN_COMMAND("&cUnknown command. Run &e/%command% help &cto see a list of commands"),
    INFECT_COMMAND_DISABLED("disabled","&cThat command is disabled!"),
    WORLD_DISABLED("&cThat feature is disabled in this world"),
    YOU_ARE_NOT_INFECTED("&cYou are not infected so you cannot infect anyone else"),
    TOO_MANY_INFECTED("&cYou have infected as many people as you could"),
    ALREADY_INFECTED("&cThat player is already infected"),
    NOW_INFECTED("&f&lYou are now infected with &4CoVID&f-&a19 &f&lbeware of any symptoms"),
    JUST_INFECTED_SOMEONE("&f%infecter% just infected %infected% with CoVID&f-&a19"),
    MUST_BE_IN_RADIUS("&cYou are too far from that player. you must be less than %radius% blocks apart"),

    ;

    private final String path;
    private final String dflt;

    Message(String dflt) {
        this.path = "config.messages."+this.toString().toLowerCase(Locale.ROOT).replace("_"," ");
        this.dflt = dflt;
    }

    Message(String path, String dflt) {
        this.path = "config.messages."+path;
        this.dflt = dflt;
    }

    @NotNull
    @Override
    public String getPath() {
        return this.path;
    }

    @NotNull
    @Override
    public String getDefault() {
        return this.dflt;
    }
}
