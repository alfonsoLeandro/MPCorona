package com.github.alfonsoleandro.corona.commands.commandhandlers;

import com.github.alfonsoleandro.corona.Corona;
import com.github.alfonsoleandro.corona.utils.Message;
import org.bukkit.command.CommandSender;

public class ReloadHandler extends AbstractHandler{

    public ReloadHandler(Corona plugin, AbstractHandler successor) {
        super(plugin, successor);
    }

    @Override
    protected boolean meetsCondition(CommandSender sender, String label, String[] args) {
        return args[0].equalsIgnoreCase("reload");
    }

    @Override
    protected void internalHandle(CommandSender sender, String label, String[] args) {
        if(!sender.hasPermission("corona.reload")) {
            this.messageSender.send(sender, Message.NO_PERMISSION);
            return;
        }
        this.plugin.reload(false);
        this.messageSender.send(sender, Message.RELOADED);
    }
}
