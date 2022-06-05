package com.github.alfonsoleandro.corona.events;

import com.github.alfonsoleandro.corona.Corona;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DamageEvent implements Listener {

    final private Corona plugin;
    Random r;

    public DamageEvent(Corona plugin) {
        this.plugin = plugin;
        r = new Random();
    }

    public ItemStack getMask() {
        String path = "config.mask.";
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skull = (SkullMeta) head.getItemMeta();

        skull.setDisplayName(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(path+".name")));
        List <String> lore = new ArrayList<>();
        for (String linea : plugin.getConfig().getStringList(path+".lore")) {
            lore.add(ChatColor.translateAlternateColorCodes('&', linea));
        }
        skull.setLore(lore);
        head.setItemMeta(skull);
        head.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 10);

        return head;
    }


    public String getHasMask(Player p) {
        ItemStack helmet = p.getInventory().getHelmet();
        if(helmet != null) {
            if(plugin.getConfig().getBoolean("config.mask.enabled") && helmet.getType().equals(getMask().getType())) {
                ItemMeta meta1 = helmet.getItemMeta();
                ItemMeta meta2 = getMask().getItemMeta();
                if(meta1 != null && meta2 != null && meta1.getDisplayName().equalsIgnoreCase(meta2.getDisplayName()) && meta1.getLore().equals(meta2.getLore()) && meta1.getEnchants().equals(meta2.getEnchants())) {
                    return ".with mask";
                }

            }
        }

        return ".without mask";
    }


    public void infect(Player player, String infected, String infecter) {
        FileConfiguration config = plugin.getConfig();
        FileConfiguration players = plugin.getPlayersYaml().getAccess();
        String prefix = config.getString("config.prefix")+" ";
        String nowinfec = config.getString("config.messages.now infected");
        String justinfected = config.getString("config.messages.just infected someone");


        List<String> infectedS = players.getStringList("players.infected");
        infectedS.add(player.getName());
        players.set("players.infected", infectedS);
        players.set("players.to infect."+player.getName(), 0);
        plugin.getPlayersYaml().save(true);
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', prefix+justinfected.replace("%infecter%", infecter).replace("%infected%", infected)));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+nowinfec));
    }




    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if(event.getEntity() instanceof Player) {
            FileConfiguration config = plugin.getConfig();
            if(config.getStringList("config.mobs that can infect").contains(event.getDamager().getType().toString())) {
                FileConfiguration players = plugin.getPlayersYaml().getAccess();
                Player player = (Player) event.getEntity();
                List<String> disabledWorlds = config.getStringList("config.disabled worlds");


                if(!disabledWorlds.contains(player.getWorld().getName())) {

                    if(!players.getStringList("players.infected").contains(player.getName())) {
                        int i = r.nextInt(100);

                        if(config.getInt("config.chance to infect.mob"+getHasMask(player)) >= i) {
                            String infecter;
                            if(event.getDamager().getType().equals(EntityType.PLAYER)) {
                                infecter = event.getDamager().getName();
                            }else {
                                infecter = event.getDamager().getType().toString();
                            }
                            infect(player, player.getName(), infecter);
                        }

                    }

                }

            }


        }
    }



    @EventHandler
    public void onEat(PlayerItemConsumeEvent event) {
        FileConfiguration config = plugin.getConfig();
        if(config.getStringList("config.food that can infect").contains(event.getItem().getType().toString())) {
            FileConfiguration players = plugin.getPlayersYaml().getAccess();
            Player player = event.getPlayer();
            List<String> disabledWorlds = config.getStringList("config.disabled worlds");


            if(!disabledWorlds.contains(player.getWorld().getName())) {

                if(!players.getStringList("players.infected").contains(player.getName())) {
                    int i = r.nextInt(100);

                    if(config.getInt("config.chance to infect.food") >= i) {
                        infect(player, player.getName(), event.getItem().getType().toString());
                    }


                }

            }



        }


    }
}
