package com.github.alfonsoleandro.corona.events;

import com.github.alfonsoleandro.corona.Corona;
import com.github.alfonsoleandro.corona.managers.RecipesManager;
import com.github.alfonsoleandro.corona.managers.Settings;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public class ItemCraft implements Listener {

    private final RecipesManager recipesManager;
    private final Settings settings;

    public ItemCraft(Corona plugin) {
        this.recipesManager = plugin.getRecipesManager();
        this.settings = plugin.getSettings();
    }


    private boolean isMask(ItemStack item) {
        return item != null &&  item.isSimilar(this.recipesManager.getMaskItem());
    }
    private boolean isCurePotion(ItemStack item) {
        return item != null &&  item.isSimilar(this.recipesManager.getCurePotionItem());
    }


    @EventHandler
    public void onMaskCraft(PrepareItemCraftEvent event) {
        if(event.getInventory().getResult() != null) {
            if(isMask(event.getInventory().getResult())) {
                Player player = (Player) event.getView().getPlayer();
                if(!player.hasPermission("corona.mask") ||
                        this.settings.isMaskDisabled() ||
                        this.settings.isMaskRecipeDisabled()) {
                    event.getInventory().setResult(new ItemStack(Material.AIR));
                }
            }
        }
    }
    @EventHandler
    public void onPotionCraft(PrepareItemCraftEvent event) {
        if(event.getInventory().getResult() != null) {
            if(isCurePotion(event.getInventory().getResult())) {
                Player player = (Player) event.getView().getPlayer();
                if(!player.hasPermission("corona.curePotion") ||
                        this.settings.isCurePotionDisabled() ||
                        this.settings.isCurePotionRecipeDisabled()) {
                    event.getInventory().setResult(new ItemStack(Material.AIR));
                }
            }
        }
    }


}
