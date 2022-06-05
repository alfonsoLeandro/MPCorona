package com.github.alfonsoleandro.corona.events;

import com.github.alfonsoleandro.corona.Corona;
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


    final private Corona plugin;

    public PlaceEvent(Corona plugin) {
        this.plugin = plugin;
    }


    public ItemStack getMask() {

        String path = "config.mask.";
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skull = (SkullMeta) head.getItemMeta();

        skull.setDisplayName(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(path+".name")));
        List<String> lore = new ArrayList<>();
        for (String linea : plugin.getConfig().getStringList(path+".lore")) {
            lore.add(ChatColor.translateAlternateColorCodes('&', linea));
        }
        skull.setLore(lore);
        head.setItemMeta(skull);
        head.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 10);

        return head;
    }


    public boolean isMask(ItemStack b) {
        if(b != null && b.getType().equals(getMask().getType())) {
            ItemMeta meta1 = b.getItemMeta();
            ItemMeta meta2 = getMask().getItemMeta();
            return meta1.getDisplayName().equalsIgnoreCase(meta2.getDisplayName()) && meta1.getLore().equals(meta2.getLore()) && meta1.getEnchants().equals(meta2.getEnchants());
        }
        return false;
    }




    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if(isMask(event.getItemInHand())) {
            event.setCancelled(true);
            FileConfiguration config = plugin.getConfig();
            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("config.prefix")+" "+config.getString("config.messages.cannot place")));
        }
    }


}
