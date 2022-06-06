package com.github.alfonsoleandro.corona.events;

import com.github.alfonsoleandro.corona.Corona;
import com.github.alfonsoleandro.corona.managers.InfectionManager;
import com.github.alfonsoleandro.corona.managers.RecipesManager;
import com.github.alfonsoleandro.corona.managers.Settings;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Random;

public class DamageEvent implements Listener {

    private final Settings settings;
    private final InfectionManager infectionManager;
    private final RecipesManager recipesManager;
    private final Random random;

    public DamageEvent(Corona plugin) {
        this.settings = plugin.getSettings();
        this.infectionManager = plugin.getInfectionManager();
        this.recipesManager = plugin.getRecipesManager();
        this.random = new Random();
    }


    public boolean hasMask(Player p) {
        ItemStack helmet = p.getInventory().getHelmet();
        ItemStack mask = this.recipesManager.getMaskItem();
        return !this.settings.isMaskDisabled() &&
                helmet != null && helmet.equals(mask);
    }


    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Player)) {
            return;
        }
        if(!this.settings.getMobsThatInfect().contains(event.getDamager().getType().toString())){
            return;
        }
        Player player = (Player) event.getEntity();
        if(this.infectionManager.isInfected(player.getName())){
            return;
        }
        if(this.settings.getDisabledWorlds().contains(Objects.requireNonNull(player.getLocation().getWorld()).getName())){
            return;
        }
        int i = this.random.nextInt(100);
        int prob = hasMask(player) ? this.settings.getProbToMobInfectWithMask() :
                this.settings.getProbToMobInfectWithoutMask();

        if(prob >= i) {
            String infecter;
            if(event.getDamager().getType().equals(EntityType.PLAYER)) {
                infecter = event.getDamager().getName();
            } else {
                infecter = event.getDamager().getType().toString();
            }
            this.infectionManager.infect(player, infecter, false);
        }

    }


    @EventHandler
    public void onEat(PlayerItemConsumeEvent event) {
        if(!this.settings.getFoodThatInfect().contains(event.getItem().getType().toString())){
            return;
        }
        Player player = event.getPlayer();
        if(this.infectionManager.isInfected(player.getName())){
            return;
        }
        if(this.settings.getDisabledWorlds().contains(Objects.requireNonNull(player.getLocation().getWorld()).getName())){
            return;
        }
        int i = this.random.nextInt(100);
        int prob = this.settings.getProbToInfectByFood();

        if(prob >= i) {
            this.infectionManager.infect(player, event.getItem().getType().toString(), false);
        }

    }
}
