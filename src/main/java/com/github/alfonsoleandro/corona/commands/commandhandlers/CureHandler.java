package com.github.alfonsoleandro.corona.commands.commandhandlers;

import com.github.alfonsoleandro.corona.Corona;
import com.github.alfonsoleandro.corona.managers.InfectionManager;
import com.github.alfonsoleandro.corona.managers.Settings;
import com.github.alfonsoleandro.corona.utils.Message;
import com.github.alfonsoleandro.mputils.managers.MessageSender;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CureHandler extends AbstractHandler{

    private final InfectionManager infectionManager;
    private final MessageSender<Message> messageSender;
    private final Settings settings;

    public CureHandler(Corona plugin, AbstractHandler successor) {
        super(plugin, successor);
        this.infectionManager = plugin.getInfectionManager();
        this.messageSender = plugin.getMessageSender();
        this.settings = plugin.getSettings();
    }

    @Override
    protected boolean meetsCondition(CommandSender sender, String label, String[] args) {
        return args[0].equalsIgnoreCase("cure") || args[0].equalsIgnoreCase("c");
    }

    @Override
    protected void internalHandle(CommandSender sender, String label, String[] args) {
        if(this.settings.isCureCommandDisabled()) {
            this.messageSender.send(sender, Message.CURE_DISABLED);
            return;
        }
        if(!sender.hasPermission("corona.cure")) {
            this.messageSender.send(sender, Message.NO_PERMISSION);
            return;
        }
        if(args.length <= 1){
            this.messageSender.send(sender, "&cUse: &f/"+label+" cure (player)");
            return;
        }
        Player toInfect = Bukkit.getPlayer(args[1]);
        if(toInfect == null) {
            this.messageSender.send(sender, Message.NOT_ONLINE,
                    "%player%", args[1]);
            return;
        }
        if(!this.infectionManager.isInfected(toInfect.getName())){
            this.messageSender.send(sender, Message.NOT_INFECTED);
            return;
        }


        if(sender instanceof Player) {
            Player infecter = (Player) sender;
            Economy economy = this.plugin.getEconomy();
            if(economy == null){
                this.messageSender.send(sender, Message.CURE_COMMAND_DISABLED);
                return;
            }

            double price = this.settings.getCurePrice();

            if(economy.getBalance(infecter) >= price) {
                economy.withdrawPlayer(infecter, price);
            }else {
                this.messageSender.send(sender, Message.NOT_ENOUGH_MONEY,
                        "%price%", String.valueOf(price));
                return;
            }

        }
        this.infectionManager.cure(toInfect, sender);


    }
}
