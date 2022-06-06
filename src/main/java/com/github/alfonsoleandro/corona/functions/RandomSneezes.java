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
import com.github.alfonsoleandro.mputils.sound.SoundUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;


public class RandomSneezes extends Reloadable {

    private final Corona plugin;
    private final Settings settings;
    private final InfectionManager infectionManager;
    private final RecipesManager recipesManager;
    private final MessageSender<Message> messageSender;
    private final Random random;
    private String mode;
    private int time;
    private int radiusSquared;
    private BukkitTask runnable;

    public RandomSneezes(Corona plugin) {
        super(plugin);
        this.plugin = plugin;
        this.settings = plugin.getSettings();
        this.infectionManager = plugin.getInfectionManager();
        this.recipesManager = plugin.getRecipesManager();
        this.messageSender = plugin.getMessageSender();
        this.random = new Random();
        this.time = this.settings.getSneezesIntervalTicks();
        this.mode = this.settings.getRandomSneezesMode();
        this.radiusSquared = this.settings.getSneezesRadiusSquared();
        if(this.settings.isRandomSneezesEnabled()) randomSneezes();
    }


    public void randomSneezes() {
        this.runnable = new BukkitRunnable() {

            @Override
            public void run() {
                if(Bukkit.getOnlinePlayers().size() < 2) return;
                if(RandomSneezes.this.infectionManager.getInfectedPlayers().isEmpty()) return;
                Set<String> infected = RandomSneezes.this.infectionManager.getInfectedPlayers();
                if(RandomSneezes.this.mode.equalsIgnoreCase("all")) {
                    for (Player inf : Bukkit.getOnlinePlayers()) {
                        sneeze(inf);
                    }
                } else {
                    List<Player> infectedPlayersOnline = new ArrayList<>();
                    for(Player p : Bukkit.getOnlinePlayers()){
                        if(infected.contains(p.getName())) infectedPlayersOnline.add(p);
                    }
                    Player inf = infectedPlayersOnline.get(RandomSneezes.this.random.nextInt(infectedPlayersOnline.size()));
                    sneeze(inf);
                }

            }

        }.runTaskTimer(this.plugin, this.time, this.time);

    }

    private void sneeze(Player player){
        Set<String> infected = RandomSneezes.this.infectionManager.getInfectedPlayers();

        if(!infected.contains(player.getName())) return;
        if(RandomSneezes.this.settings.isSneezesSoundEnabled()){
            SoundUtils.playSound(player.getLocation(),
                    RandomSneezes.this.settings.getSneezesSound());
        }
        RandomSneezes.this.messageSender.send(player, Message.YOU_SNEEZED);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if(p.equals(player) || infected.contains(p.getName())) continue;
            if(!p.getWorld().equals(player.getWorld())) continue;
            if(player.getLocation().distanceSquared(p.getLocation()) > this.radiusSquared) continue;
            RandomSneezes.this.messageSender.send(p, Message.SNEEZED,
                    "%player%", player.getName());
            int prob = hasMask(player) || hasMask(p) ?
                    RandomSneezes.this.settings.getProbToInfectUsingMask() :
                    RandomSneezes.this.settings.getProbToInfectWithoutMask();
            if(RandomSneezes.this.random.nextInt(100) <= prob) {
                RandomSneezes.this.infectionManager.infect(player, p.getName(), true);
            }
        }

    }


    public boolean hasMask(Player p) {
        ItemStack helmet = p.getInventory().getHelmet();
        ItemStack mask = this.recipesManager.getMaskItem(); // TODO
        return helmet != null && helmet.isSimilar(mask);
    }


    public void cancel() {
        if(this.runnable != null) this.runnable.cancel();
    }

    @Override
    public void reload(boolean deep) {
        this.time = this.settings.getSneezesIntervalTicks();
        this.mode = this.settings.getRandomSneezesMode();
        this.radiusSquared = this.settings.getSneezesRadiusSquared();
        cancel();
        if(this.settings.isRandomSneezesEnabled()) randomSneezes();

    }
}
