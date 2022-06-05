package com.github.alfonsoleandro.corona.events;

import com.github.alfonsoleandro.corona.functions.FeelSymptoms;
import com.github.alfonsoleandro.corona.Corona;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class CureByPotion implements Listener {

    final private Corona plugin;
    final private FeelSymptoms symp;

    public CureByPotion(Corona plugin, FeelSymptoms symp){
        this.plugin = plugin;
        this.symp = symp;
    }

    public void send(CommandSender sender, String msg){
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("config.prefix")+" "+msg));
    }

    public void cure(Player toCure){
        FileConfiguration config = plugin.getConfig();
        FileConfiguration players = plugin.getPlayersYaml().getAccess();
        String curedByPotion = config.getString("config.messages.cured by potion");
        String hasCured = config.getString("config.messages.someone cured by potion");
        List<String> infected = players.getStringList("players.infected");


        infected.remove(toCure.getName());
        players.set("players.infected", infected);
        players.set("players.to infect."+toCure.getName(), 0);
        plugin.getPlayersYaml().save(true);
        symp.cancel();
        symp.start();
        send(toCure, curedByPotion);
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', config.getString("config.prefix")+" "+hasCured.replace("%player%", toCure.getName())));
    }

    @EventHandler
    public void onPotionDrink(PlayerItemConsumeEvent event){
        ItemStack item = event.getItem();
        if(item.getType().equals(Material.POTION) && item.isSimilar(getCuringPotion())){
            FileConfiguration config = plugin.getConfig();
            if(config.getBoolean("config.cure potion.enabled")){
                if(plugin.getPlayersYaml().getAccess().getStringList("players.infected").contains(event.getPlayer().getName())) {
                    cure(event.getPlayer());
                }else{
                    event.setCancelled(true);
                    send(event.getPlayer(), config.getString("config.messages.cannot use potion"));
                }
            }else{
                event.setCancelled(true);
                send(event.getPlayer(), config.getString("config.messages.potion disabled"));
            }

        }
    }


    // Gets the curing potion item from config.
    public ItemStack getCuringPotion(){
        FileConfiguration config = plugin.getConfig();

        ItemStack potion = new ItemStack(Material.POTION);
        ItemMeta meta = potion.getItemMeta();
        assert meta != null;
        ((PotionMeta)meta).addCustomEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1200, 4), true);
        ((PotionMeta)meta).addCustomEffect(new PotionEffect(PotionEffectType.WEAKNESS, 1200, 4), true);



        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString("config.cure potion.name")));


        List<String> lore = new ArrayList<>();
        for(String line : config.getStringList("config.cure potion.lore")){
            lore.add(ChatColor.translateAlternateColorCodes('&', line));
        }

        meta.setLore(lore);
        potion.setItemMeta(meta);

        return potion;
    }
}
