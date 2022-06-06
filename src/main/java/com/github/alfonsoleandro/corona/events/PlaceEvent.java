package com.github.alfonsoleandro.corona.events;

import com.github.alfonsoleandro.corona.Corona;
import com.github.alfonsoleandro.corona.utils.Message;
import com.github.alfonsoleandro.mputils.managers.MessageSender;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class PlaceEvent implements Listener {


    private final Corona plugin;
    private final MessageSender<Message> messageSender;

    public PlaceEvent(Corona plugin) {
        this.plugin = plugin;
        this.messageSender = plugin.getMessageSender();
    }


    private boolean isMask(ItemStack item) {
        return item != null &&  item.isSimilar(this.plugin.getRecipesManager().getMaskItem());
    }


    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if(isMask(event.getItemInHand())) {
            event.setCancelled(true);
            this.messageSender.send(event.getPlayer(), Message.CANNOT_PLACE);
        }
    }


}
