package com.github.alfonsoleandro.corona.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.github.alfonsoleandro.corona.Corona;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;


public class RandomSneezes {

    final private Corona plugin;
    final private Random r;

    public RandomSneezes(Corona plugin){
        this.plugin = plugin;
        r = new Random();
    }



    List<Player> players;
    BukkitTask runnable;
    FileConfiguration config;
    FileConfiguration playersF;

    public void send(CommandSender sender, String msg){
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("config.prefix")+" "+msg));
    }

    public void broadcast(String msg){
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("config.prefix")+" "+msg));
    }

    public void randomSneezes() {
        players = new ArrayList<>();

        final int time = getTime();
        final String mode = plugin.getConfig().getString("config.infected.random sneezes.mode");


        runnable = new BukkitRunnable() {

            @Override
            public void run(){
                if(mode.equalsIgnoreCase("all") || mode.equalsIgnoreCase("random")) {
                    elegirRandom(mode);
                }else {
                    elegirRandom("all");
                    send(Bukkit.getConsoleSender(), "&cMode field in config is incorrect, by default mode was set to \"all\"");
                }

            }

        }.runTaskTimer(plugin, time, time);



    }


    public int getTime() {
        final String t = plugin.getConfig().getString("config.infected.random sneezes.interval");

        if(t.contains("s")) {
            return Integer.parseInt(t.replace("s", ""))*20;
        }else if(t.contains("m")){
            return Integer.parseInt(t.replace("m", ""))*1200;
        }else if(t.contains("h")) {
            return Integer.parseInt(t.replace("h", ""))*72000;
        }else {
            send(Bukkit.getConsoleSender(), "&cERROR in config.yml, please specify a valid delay");
            send(Bukkit.getConsoleSender(), "&cBy default, delay was set to &f1m");
            return 1200;
        }

    }



    public void elegirRandom(String mode) {
        config = plugin.getConfig();
        playersF = plugin.getPlayersYaml().getAccess();
        players.clear();
        if (!Bukkit.getOnlinePlayers().isEmpty()){
            final List<String> infected = playersF.getStringList("players.infected");
            if(infected.isEmpty()) return;
            final double radius = Math.pow(config.getInt("config.infected.random sneezes.radius"), 2);

            for(Player p: Bukkit.getOnlinePlayers()) {
                if(infected.contains(p.getName())) {
                    players.add(p);
                }
            }
            if(mode.equalsIgnoreCase("random")) {
                Player chosen = players.get(r.nextInt(players.size()));
                players.clear();
                players.add(chosen);
            }


            for(Player inf : players) {
                sound(inf);
                send(inf, config.getString("config.messages.you sneezed"));
                for(Player p :Bukkit.getOnlinePlayers()) {
                    if (p != inf && !infected.contains(p.getName())) {

                        if(p.getWorld().equals(inf.getWorld())) {
                            if(inf.getLocation().distanceSquared(p.getLocation()) < radius) {
                                sound(p);
                                send(p, config.getString("config.messages.sneezed").replace("%player%", inf.getName()));
                                if(hasMask(p) || hasMask(inf)) {
                                    final int maskEff = config.getInt("config.infected.random sneezes.mask effectiveness");

                                    if(!(r.nextInt(100) <= maskEff)) {
                                        infect(inf.getName(), p);
                                    }

                                }else {
                                    final int probToInf = config.getInt("config.infected.random sneezes.probability to infect");
                                    if(r.nextInt(100) <= probToInf) {
                                        infect(inf.getName(), p);
                                    }
                                }

                            }
                        }

                    }
                }
            }


        }

    }





    public ItemStack getMask() {
        final String path = "config.mask.";
        final ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        final SkullMeta skull = (SkullMeta) head.getItemMeta();

        skull.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString(path+".name")));
        final List <String> lore = new ArrayList<>();
        for (String linea : config.getStringList(path+".lore")) {
            lore.add(ChatColor.translateAlternateColorCodes('&', linea));
        }
        skull.setLore(lore);
        head.setItemMeta(skull);
        head.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 10);

        return head;
    }


    public boolean hasMask(Player p) {
        final ItemStack helmet = p.getInventory().getHelmet();
        if(helmet != null && helmet.getType().equals(getMask().getType())) {
            final ItemMeta meta1 = helmet.getItemMeta();
            final ItemMeta meta2 = getMask().getItemMeta();
            if(meta1 != null && meta2 != null && meta1.getDisplayName().equalsIgnoreCase(meta2.getDisplayName()) && meta1.getLore().equals(meta2.getLore()) && meta1.getEnchants().equals(meta2.getEnchants())) {
                return true;
            }

        }
        return false;
    }





    public void sound(Player player) {
        if(config.getBoolean("config.infected.random sneezes.sound.enabled")) {
            player.playSound(player.getLocation(), Sound.valueOf(config.getString("config.infected.random sneezes.sound.params.sound")),
                    (float)config.getDouble("config.infected.random sneezes.sound.params.volume"),
                    (float)config.getDouble("config.infected.random sneezes.sound.params.pitch"));
        }

    }


    public void infect(String infecter, Player infected) {
        final String nowinfec = config.getString("config.messages.now infected");
        final String justinfected = config.getString("config.messages.just infected someone");
        final List<String> disabledWorlds = config.getStringList("config.disabled worlds");

        if(!disabledWorlds.contains(infected.getWorld().getName())){
            final List<String> infectedS = playersF.getStringList("players.infected");
            infectedS.add(infected.getName());
            playersF.set("players.infected", infectedS);
            playersF.set("players.to infect."+infected.getName(), 0);
            plugin.getPlayersYaml().save(true);

            broadcast(justinfected.replace("%infecter%", infecter).replace("%infected%", infected.getName()));
            send(infected, nowinfec);
        }

    }

    public void cancel() {
        runnable.cancel();
    }

}
