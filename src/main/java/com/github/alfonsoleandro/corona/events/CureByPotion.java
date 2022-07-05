package com.github.alfonsoleandro.corona.events;

import com.github.alfonsoleandro.corona.Corona;
import com.github.alfonsoleandro.corona.managers.InfectionManager;
import com.github.alfonsoleandro.corona.managers.RecipesManager;
import com.github.alfonsoleandro.corona.managers.Settings;
import com.github.alfonsoleandro.corona.utils.Message;
import com.github.alfonsoleandro.mputils.managers.MessageSender;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class CureByPotion implements Listener {

    private final Settings settings;
    private final MessageSender<Message> messageSender;
    private final InfectionManager infectionManager;
    private final RecipesManager recipesManager;

    public CureByPotion(Corona plugin){
        this.settings = plugin.getSettings();
        this.messageSender = plugin.getMessageSender();
        this.infectionManager = plugin.getInfectionManager();
        this.recipesManager = plugin.getRecipesManager();
    }

    @EventHandler
    public void onPotionDrink(PlayerItemConsumeEvent event){
        ItemStack item = event.getItem();
        if(item.getType().equals(Material.POTION) && item.isSimilar(this.recipesManager.getCurePotionItem())){
            if(this.settings.isCurePotionDisabled()) {
                event.setCancelled(true);
                this.messageSender.send(event.getPlayer(), Message.POTION_DISABLED);
            }else{
                if(this.infectionManager.isInfected(event.getPlayer().getName())){
                    this.infectionManager.cureByPotion(event.getPlayer());
                }else{
                    event.setCancelled(true);
                    this.messageSender.send(event.getPlayer(), Message.CANNOT_USE_POTION);
                }
            }

        }
    }

}
