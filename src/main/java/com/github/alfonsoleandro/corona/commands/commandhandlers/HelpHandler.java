package com.github.alfonsoleandro.corona.commands.commandhandlers;

import com.github.alfonsoleandro.corona.Corona;
import org.bukkit.command.CommandSender;

public class HelpHandler extends AbstractHandler{

    public HelpHandler(Corona plugin, AbstractHandler successor) {
        super(plugin, successor);
    }

    @Override
    protected boolean meetsCondition(CommandSender sender, String label, String[] args) {
        return args.length == 0 || args[0].equalsIgnoreCase("help");
    }

    @Override
    protected void internalHandle(CommandSender sender, String label, String[] args) {
        this.messageSender.send(sender, "&6List of commands");
        this.messageSender.send(sender, "&f/"+label+" help");
        this.messageSender.send(sender, "&f/"+label+" version");
        this.messageSender.send(sender, "&f/"+label+" reload");
    }
}
