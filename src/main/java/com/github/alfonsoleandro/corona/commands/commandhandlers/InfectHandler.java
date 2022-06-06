package com.github.alfonsoleandro.corona.commands.commandhandlers;

import com.github.alfonsoleandro.corona.Corona;
import com.github.alfonsoleandro.corona.managers.InfectionManager;
import com.github.alfonsoleandro.corona.managers.Settings;
import com.github.alfonsoleandro.corona.utils.Message;
import com.github.alfonsoleandro.mputils.managers.MessageSender;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InfectHandler extends AbstractHandler{

    private final InfectionManager infectionManager;
    private final MessageSender<Message> messageSender;
    private final Settings settings;

    public InfectHandler(Corona plugin, AbstractHandler successor) {
        super(plugin, successor);
        this.infectionManager = plugin.getInfectionManager();
        this.messageSender = plugin.getMessageSender();
        this.settings = plugin.getSettings();
    }

    @Override
    protected boolean meetsCondition(CommandSender sender, String label, String[] args) {
        return args[0].equalsIgnoreCase("infect") || args[0].equalsIgnoreCase("inf");
    }

    @Override
    protected void internalHandle(CommandSender sender, String label, String[] args) {
        if(this.settings.isInfectCommandDisabled()) {
            this.messageSender.send(sender, Message.INFECT_COMMAND_DISABLED);
            return;
        }
        if(!sender.hasPermission("corona.infect")) {
            this.messageSender.send(sender, Message.NO_PERMISSION);
            return;
        }
        if(args.length <= 1){
            this.messageSender.send(sender, "&cUse: &f/"+label+" infect (player)");
            return;
        }
        Player toInfect = Bukkit.getPlayer(args[1]);
        if(toInfect == null) {
            this.messageSender.send(sender, Message.NOT_ONLINE,
                    "%player%", args[1]);
            return;
        }
        if(this.infectionManager.isInfected(toInfect.getName())){
            this.messageSender.send(sender, Message.ALREADY_INFECTED);
            return;
        }


        if(sender instanceof Player) {
            Player infecter = (Player) sender;
            if(this.settings.getDisabledWorlds().contains(infecter.getWorld().getName())) {
                this.messageSender.send(sender, Message.WORLD_DISABLED);
                return;
            }
            if(!this.infectionManager.isInfected(sender.getName())) {
                this.messageSender.send(sender, Message.YOU_ARE_NOT_INFECTED);
                return;
            }
            if(this.infectionManager.getInfectedByPlayer(sender.getName()) >=
                    this.settings.getMaxInfectedPerPlayer()) {
                this.messageSender.send(sender, Message.TOO_MANY_INFECTED);
                return;
            }
            double radiusSquared = Math.pow(this.settings.getInfectRadius(), 2);
            if(infecter.getLocation().distanceSquared(toInfect.getLocation()) > radiusSquared){
                this.messageSender.send(sender, Message.MUST_BE_IN_RADIUS,
                        "%radius%");
                return;
            }

        }
        this.infectionManager.infect(toInfect, sender.getName(), sender instanceof Player);


    }
}
