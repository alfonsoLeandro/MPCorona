package com.github.alfonsoleandro.corona.commands.commandhandlers;

import com.github.alfonsoleandro.corona.Corona;
import com.github.alfonsoleandro.corona.managers.InfectionManager;
import com.github.alfonsoleandro.corona.utils.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheckHandler extends AbstractHandler{

    private final InfectionManager infectionManager;

    public CheckHandler(Corona plugin, AbstractHandler successor) {
        super(plugin, successor);
        this.infectionManager = plugin.getInfectionManager();
    }

    @Override
    protected boolean meetsCondition(CommandSender sender, String label, String[] args) {
        return args[0].equalsIgnoreCase("check");
    }

    @Override
    protected void internalHandle(CommandSender sender, String label, String[] args) {
        if(args.length < 2) {
            if(!sender.hasPermission("corona.check.self")) {
                this.messageSender.send(sender, Message.NO_PERMISSION);
                return;
            }
            if(!(sender instanceof Player)){
                this.messageSender.send(sender, Message.CONSOLE_CANNOT_GET_INFECTED);
                return;
            }
            this.messageSender.send(sender, Message.CHECK_SELF,
                    "%infected%",
                    this.infectionManager.isInfected(sender.getName()) ?
                            this.messageSender.getString(Message.CHECK_INFECTED) :
                            this.messageSender.getString(Message.CHECK_NOT_INFECTED));
        }else{
            if(!sender.hasPermission("corona.check.others")) {
                this.messageSender.send(sender, Message.NO_PERMISSION);
                return;
            }
            Player toCheck = Bukkit.getPlayer(args[1]);
            if(toCheck == null){
                this.messageSender.send(sender, Message.NOT_ONLINE);
                return;
            }

            this.messageSender.send(sender, Message.CHECK_OTHERS,
                    "%player%", toCheck.getName(),
                    "%infected%",
                    this.infectionManager.isInfected(toCheck.getName()) ?
                            this.messageSender.getString(Message.CHECK_INFECTED) :
                            this.messageSender.getString(Message.CHECK_NOT_INFECTED));

        }
    }
}
