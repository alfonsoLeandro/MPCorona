package com.github.alfonsoleandro.corona.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.github.alfonsoleandro.corona.Corona;
import com.github.alfonsoleandro.corona.managers.InfectionManager;
import com.github.alfonsoleandro.corona.managers.RecipesManager;
import com.github.alfonsoleandro.corona.managers.Settings;
import com.github.alfonsoleandro.corona.utils.Message;
import com.github.alfonsoleandro.mputils.managers.MessageSender;
import com.github.alfonsoleandro.mputils.reloadable.Reloadable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;


public class RandomSneezes extends Reloadable {

    private final Corona plugin;
    private final Settings settings;
    private final InfectionManager infectionManager;
    private final RecipesManager recipesManager;
    private final MessageSender<Message> messageSender;
    private final Random r;
    private BukkitTask runnable;

    public RandomSneezes(Corona plugin) {
        super(plugin);
        this.plugin = plugin;
        this.settings = plugin.getSettings();
        this.infectionManager = plugin.getInfectionManager();
        this.recipesManager = plugin.getRecipesManager();
        this.messageSender = plugin.getMessageSender();
        this.r = new Random();
    }
    public void send(CommandSender sender, String msg) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("config.prefix") + " " + msg));
    }


    public void randomSneezes() {
        int time = this.settings.getSneezesIntervalTicks();
        String mode = this.settings.getRandomSneezesMode();
        int radiusSquared = this.settings.getSneezesRadiusSquared();

        this.runnable = new BukkitRunnable() {

            @Override
            public void run() {
                if(Bukkit.getOnlinePlayers().size() < 2) return;
                if(infectionManager.getInfectedPlayers().isEmpty()) return;
                if(mode.equalsIgnoreCase("all")) {
                    Set<String> infected = infectionManager.getInfectedPlayers();


                    for (Player inf : Bukkit.getOnlinePlayers()) {
                        if(!infected.contains(inf.getName())) continue;
                        if(settings.isSneezesSoundEnabled()){
                            SoundUtils.playSound(inf.getLocation(),
                                    settings.getSneezesSound());
                        }
                        messageSender.send(inf, Message.YOU_SNEEZED);
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if(p.equals(inf) || infected.contains(p.getName())) continue;
                            if(!p.getWorld().equals(inf.getWorld())) continue;
                            if(inf.getLocation().distanceSquared(p.getLocation()) > radiusSquared) continue;
                            messageSender.send(p, Message.SNEEZED,
                                    "%player%", inf.getName());
                            int prob = hasMask(inf) || hasMask(p) ? settings.getProbToInfectUsingMask() : settings.getProbToInfectWithoutMask();
                            if(r.nextInt(100) <= prob) {
                                infectionManager.infect(inf, p);
                            }
                        }
                    }


                } else {
                    elegirRandom("all");
                }

            }

        }.runTaskTimer(plugin, time, time);


    }


    public boolean hasMask(Player p) {
        ItemStack helmet = p.getInventory().getHelmet();
        ItemStack mask = this.recipesManager.getMaskItem(); // TODO
        return helmet != null && helmet.equals(mask);
    }


    public void cancel() {
        runnable.cancel();
    }

    @Override
    public void reload(boolean b) {

    }
}
