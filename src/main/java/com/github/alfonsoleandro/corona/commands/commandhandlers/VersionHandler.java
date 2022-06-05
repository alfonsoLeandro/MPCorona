package com.github.alfonsoleandro.corona.commands.commandhandlers;

import com.github.alfonsoleandro.corona.Corona;
import com.github.alfonsoleandro.corona.utils.Message;
import org.bukkit.command.CommandSender;

public class VersionHandler extends AbstractHandler{

    public VersionHandler(Corona plugin, AbstractHandler successor) {
        super(plugin, successor);
    }

    @Override
    protected boolean meetsCondition(CommandSender sender, String label, String[] args) {
        return args[0].equalsIgnoreCase("version");
    }

    @Override
    protected void internalHandle(CommandSender sender, String label, String[] args) {
        if(!sender.hasPermission("corona.version")) {
            this.messageSender.send(sender, Message.NO_PERMISSION);
            return;
        }
        this.messageSender.send(sender, "&fVersion: &e"+ this.plugin.getVersion());

        if(!this.plugin.getLatestVersion().equals(this.plugin.getVersion())){
            this.messageSender.send(sender, "&cNew version available!");
            this.messageSender.send(sender, "&fDownload: &ahttps://bit.ly/2XTzden");
        }else{
            this.messageSender.send(sender, "&aYou have the latest version!");
        }
    }
}
