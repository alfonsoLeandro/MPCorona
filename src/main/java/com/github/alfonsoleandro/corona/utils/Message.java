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
    NOT_INFECTED("&cThat player is not infected"),
    NOT_ONLINE("&cThat player does not exist or is not online"),
    CURE_DISABLED("&cThe cure has not been found yet"),
    CURE_COMMAND_DISABLED("&cEconomy is disabled, therefore the cure command is also disabled"),
    NOT_ENOUGH_MONEY("&cYou do not have enough money, you need: &f%price%"),
    CURED_SOMEONE("&aYou just cured &f%cured%"),
    CURED_YOU("&f%curer% &ajust cured you"),
    HAS_CURED("&f%curer% &ahas cured &f%cured%"),
    MASK_DISABLED("&f%curer% &ahas cured &f%cured%"),
    CONSOLE_MASK("&cThe console is already protected from coronavirus, it does not need a mask &f(try /corona givemask (player))"),
    FULL_INV("&c%player%s inventory is full"),
    SELF_RECEIVED_MASK("&aReceived &lMask"),
    GIVEN_MASK("&aJust gave %player% a &lmask"),
    RECEIVED_MASK("&a%player% just sent you a &lMask&a. stay home!"),
    POTION_DISABLED("&cThe &5potion &cis disabled in config."),
    CONSOLE_POTION("&cThe console is already protected from coronavirus, it does not need a cure &f(try /corona givePotion (player))"),
    SELF_RECEIVED_POTION("&aReceived &5potion"),
    GIVEN_POTION("&aJust gave %player% a &5potion"),
    RECEIVED_POTION("&a%player% just sent you a &5potion&a. stay home!"),
    CONSOLE_CANNOT_GET_INFECTED("&cThe console cannot get infected"),
    CHECK_SELF("check.self", "&fYou are %infected%"),
    CHECK_OTHERS("check.others", "&f%player% is %infected%"),
    CHECK_INFECTED("&cInfected"),
    CHECK_NOT_INFECTED("&aHealthy"),
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
