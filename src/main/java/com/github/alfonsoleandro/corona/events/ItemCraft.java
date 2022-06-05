package com.github.alfonsoleandro.corona.events;

import com.github.alfonsoleandro.corona.Corona;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class ItemCraft implements Listener {

    final private Corona plugin;

    public ItemCraft(Corona plugin) {
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


    public boolean isMask(ItemStack item) {

        if(item.getType().equals(getMask().getType())) {
            ItemMeta meta1 = item.getItemMeta();
            ItemMeta meta2 = getMask().getItemMeta();
            return meta1.getDisplayName().equalsIgnoreCase(meta2.getDisplayName()) && meta1.getLore().equals(meta2.getLore()) && meta1.getEnchants().equals(meta2.getEnchants());

        }
        return false;
    }

    public ItemStack getPotion(){
        String path = "config.cure potion";
        ItemStack potion = new ItemStack(Material.POTION);
        ItemMeta meta = potion.getItemMeta();
        ((PotionMeta)meta).addCustomEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1200, 4), true);
        ((PotionMeta)meta).addCustomEffect(new PotionEffect(PotionEffectType.WEAKNESS, 1200, 4), true);

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(path+".name")));
        List<String> lore = new ArrayList<>();
        for (String linea : plugin.getConfig().getStringList(path+".lore")) {
            lore.add(ChatColor.translateAlternateColorCodes('&', linea));
        }
        meta.setLore(lore);
        potion.setItemMeta(meta);

        return potion;
    }

    @EventHandler
    public void onMaskCraft(PrepareItemCraftEvent event) {
        FileConfiguration config = plugin.getConfig();

        if(event.getInventory().getResult() != null && config.getBoolean("config.mask.enabled") && config.getBoolean("config.mask.recipe.enabled")) {
            if(isMask(event.getInventory().getResult())) {
                Player player = (Player) event.getView().getPlayer();
                if(!player.hasPermission("corona.mask")) {
                    event.getInventory().setResult(new ItemStack(Material.AIR));
                }
            }
        }
    }
    @EventHandler
    public void onPotionCraft(PrepareItemCraftEvent event) {
        FileConfiguration config = plugin.getConfig();

        if(event.getInventory().getResult() != null && config.getBoolean("config.cure potion.enabled") && config.getBoolean("config.cure potion.recipe.enabled")) {
            if(event.getInventory().getResult().equals(getPotion())) {
                Player player = (Player) event.getView().getPlayer();
                if(!player.hasPermission("corona.curePotion")) {
                    event.getInventory().setResult(new ItemStack(Material.AIR));
                }
            }
        }
    }


}
