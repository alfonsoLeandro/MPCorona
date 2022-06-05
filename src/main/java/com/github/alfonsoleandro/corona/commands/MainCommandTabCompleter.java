package com.github.alfonsoleandro.corona.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class MainCommandTabCompleter implements TabCompleter {

    /**
     * Checks if a string is partially or completely equal to another string
     *
     * @param input  The given string.
     * @param string The base string.
     * @return true if the first string is partially or completely equal to the second/base string.
     */
    public boolean equalsToStringUnCompleted(String input, String string) {
        for (int i = 0; i < string.length(); i++) {
            if(input.equalsIgnoreCase(string.substring(0, i))) {
                return true;
            }
        }
        return false;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        final List<String> possibilities = new ArrayList<>();

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("")) {
                possibilities.add("help");
                possibilities.add("version");
                possibilities.add("reload");
                possibilities.add("infect");
                possibilities.add("cure");
                possibilities.add("giveMask");
                possibilities.add("givePotion");
                possibilities.add("check");

            } else if(equalsToStringUnCompleted(args[0], "help")) {
                possibilities.add("help");

            } else if(equalsToStringUnCompleted(args[0], "version")) {
                possibilities.add("version");

            } else if(equalsToStringUnCompleted(args[0], "reload")) {
                possibilities.add("reload");

            } else if(equalsToStringUnCompleted(args[0], "infect")) {
                possibilities.add("infect");

            } else if(equalsToStringUnCompleted(args[0], "cure")) {
                possibilities.add("cure");

            } else if(equalsToStringUnCompleted(args[0], "give")) {
                possibilities.add("giveMask");
                possibilities.add("givePotion");

            } else if(equalsToStringUnCompleted(args[0], "giveMask")) {
                possibilities.add("giveMask");

            } else if(equalsToStringUnCompleted(args[0], "givePotion")) {
                possibilities.add("givePotion");

            } else if(equalsToStringUnCompleted(args[0], "check")) {
                possibilities.add("check");
            }

        }else{
            return null;
        }
        return possibilities;
    }
}
