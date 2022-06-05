package com.github.alfonsoleandro.corona.functions;

import com.github.alfonsoleandro.corona.Corona;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Random;

public class FeelSymptoms {

    private final Corona plugin;
    private final Random r;
    private BukkitTask runnable;

    public FeelSymptoms(Corona plugin) {
        this.plugin = plugin;
        r = new Random();
    }

    private void send(CommandSender sender, String message){
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("config.prefix")+" "+message));
    }


    public void start() {
        FileConfiguration config = plugin.getConfig();
        int time = getTime();
        List<String> efectos = config.getStringList("config.infected.symptoms");
        String gotSymptoms = config.getString("config.messages.feeling symptoms");
        List<String> disabledWorlds = config.getStringList("config.disabled worlds");
        boolean val = config.getBoolean("config.symptoms disabled in disabled worlds");

        runnable = new BukkitRunnable(){

            public void run() {
                List<String> infected = plugin.getPlayersYaml().getAccess().getStringList("players.infected");

                if(!infected.isEmpty()) {
                    for(String name : infected) {
                        Player player = Bukkit.getPlayer(name);

                        if(player != null) {

                            if(!(disabledWorlds.contains(player.getWorld().getName()) && val)) {

                                String[] efecto = efectos.get(r.nextInt(efectos.size()-1)).split(",");
                                PotionEffect effect = new PotionEffect(PotionEffectType.getByName(efecto[0]), Integer.parseInt(efecto[1])*20, Integer.parseInt(efecto[2]));
                                player.addPotionEffect(effect);
                                if(config.getBoolean("config.sound.enabled")) {
                                    player.playSound(player.getLocation(), Sound.valueOf(config.getString("config.sound.sound")), 1F, 1F);
                                }
                                send(player, gotSymptoms.replace("%symptom%", efecto[0]));

                            }

                        }


                    }
                }


            }
        }.runTaskTimer(plugin, time, time);
    }


    public int getTime() {
        String t = plugin.getConfig().getString("config.infected.interval");

        if(t.contains("s")) {
            return Integer.parseInt(t.replace("s", ""))*20;
        }else if(t.contains("m")){
            return Integer.parseInt(t.replace("m", ""))*1200;
        }else if(t.contains("h")) {
            return Integer.parseInt(t.replace("h", ""))*72000;
        }else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cERROR in config.yml, please specify a valid delay"));
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cBy default, delay was set to &f1m"));
            return 1200;
        }

    }



    public void cancel() {
        runnable.cancel();
    }




}
