package com.github.alfonsoleandro.corona.commands.commandhandlers;

import com.github.alfonsoleandro.corona.Corona;
import com.github.alfonsoleandro.corona.managers.RecipesManager;
import com.github.alfonsoleandro.corona.managers.Settings;
import com.github.alfonsoleandro.corona.utils.Message;
import com.github.alfonsoleandro.mputils.itemstacks.InventoryUtils;
import com.github.alfonsoleandro.mputils.managers.MessageSender;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveMaskHandler extends AbstractHandler{

    private final RecipesManager recipesManager;
    private final MessageSender<Message> messageSender;
    private final Settings settings;

    public GiveMaskHandler(Corona plugin, AbstractHandler successor) {
        super(plugin, successor);
        this.recipesManager = plugin.getRecipesManager();
        this.messageSender = plugin.getMessageSender();
        this.settings = plugin.getSettings();
    }

    @Override
    protected boolean meetsCondition(CommandSender sender, String label, String[] args) {
        return args[0].equalsIgnoreCase("giveMask") || args[0].equalsIgnoreCase("gm");
    }

    @Override
    protected void internalHandle(CommandSender sender, String label, String[] args) {
        if(!sender.hasPermission("corona.givemask")) {
            this.messageSender.send(sender, Message.NO_PERMISSION);
            return;
        }
        if(this.settings.isMaskDisabled()){
            this.messageSender.send(sender, Message.MASK_DISABLED);
            return;
        }

        if(args.length <= 1){
            if(!(sender instanceof Player)){
                this.messageSender.send(sender, Message.CONSOLE_MASK);
                return;
            }
            if(!InventoryUtils.canAdd(this.recipesManager.getMaskItem(), ((Player) sender).getInventory())){
                this.messageSender.send(sender, Message.FULL_INV,
                        "%player%", sender.getName());
                return;
            }
            ((Player) sender).getInventory().addItem(this.recipesManager.getMaskItem());
            this.messageSender.send(sender, Message.SELF_RECEIVED_MASK);


        }else {
            Player toGive = Bukkit.getPlayer(args[1]);
            if(toGive == null) {
                this.messageSender.send(sender, Message.NOT_ONLINE,
                        "%player%", args[1]);
                return;
            }
            if(!InventoryUtils.canAdd(this.recipesManager.getMaskItem(), toGive.getInventory())){
                this.messageSender.send(sender, Message.FULL_INV,
                        "%player%", toGive.getName());
                return;
            }
            toGive.getInventory().addItem(this.recipesManager.getMaskItem());
            this.messageSender.send(sender, Message.GIVEN_MASK,
                    "%player%", toGive.getName());
            this.messageSender.send(toGive, Message.RECEIVED_MASK,
                    "%player%", sender.getName());

        }

    }
}
