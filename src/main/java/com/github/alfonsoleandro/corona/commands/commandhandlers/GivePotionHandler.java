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

public class GivePotionHandler extends AbstractHandler{

    private final RecipesManager recipesManager;
    private final MessageSender<Message> messageSender;
    private final Settings settings;

    public GivePotionHandler(Corona plugin, AbstractHandler successor) {
        super(plugin, successor);
        this.recipesManager = plugin.getRecipesManager();
        this.messageSender = plugin.getMessageSender();
        this.settings = plugin.getSettings();
    }

    @Override
    protected boolean meetsCondition(CommandSender sender, String label, String[] args) {
        return args[0].equalsIgnoreCase("givePotion") || args[0].equalsIgnoreCase("gp");
    }

    @Override
    protected void internalHandle(CommandSender sender, String label, String[] args) {
        if(!sender.hasPermission("corona.givemask")) {
            this.messageSender.send(sender, Message.NO_PERMISSION);
            return;
        }
        if(this.settings.isCurePotionDisabled()){
            this.messageSender.send(sender, Message.POTION_DISABLED);
            return;
        }

        if(args.length <= 1){
            if(!(sender instanceof Player)){
                this.messageSender.send(sender, Message.CONSOLE_POTION);
                return;
            }
            if(!InventoryUtils.canAdd(this.recipesManager.getCurePotionItem(), ((Player) sender).getInventory())){
                this.messageSender.send(sender, Message.FULL_INV,
                        "%player%", sender.getName());
                return;
            }
            ((Player) sender).getInventory().addItem(this.recipesManager.getCurePotionItem());
            this.messageSender.send(sender, Message.SELF_RECEIVED_POTION);


        }else {
            Player toGive = Bukkit.getPlayer(args[1]);
            if(toGive == null) {
                this.messageSender.send(sender, Message.NOT_ONLINE,
                        "%player%", args[1]);
                return;
            }
            if(!InventoryUtils.canAdd(this.recipesManager.getCurePotionItem(), toGive.getInventory())){
                this.messageSender.send(sender, Message.FULL_INV,
                        "%player%", toGive.getName());
                return;
            }
            toGive.getInventory().addItem(this.recipesManager.getCurePotionItem());
            this.messageSender.send(sender, Message.GIVEN_POTION,
                    "%player%", toGive.getName());
            this.messageSender.send(toGive, Message.RECEIVED_POTION,
                    "%player%", sender.getName());

        }

    }
}
