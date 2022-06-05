package com.github.alfonsoleandro.corona.utils;

import com.github.alfonsoleandro.mputils.misc.MessageEnum;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum Message implements MessageEnum {


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
