package com.github.alfonsoleandro.corona.managers;

import com.github.alfonsoleandro.corona.Corona;
import com.github.alfonsoleandro.mputils.reloadable.Reloadable;
import org.bukkit.configuration.file.FileConfiguration;

public class Settings extends Reloadable {

    private final Corona plugin;

    //<editor-fold desc="Fields" default-state="collapsed">
    private boolean curePotionEnabled;
    private boolean curePotionRecipeEnabled;
    private boolean maskEnabled;
    private boolean maskRecipeEnabled;
    //</editor-fold>

    public Settings(Corona plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    private void loadFields() {
        FileConfiguration config = this.plugin.getConfigYaml().getAccess();

        this.curePotionEnabled = config.getBoolean("config.cure potion.enabled");
        this.curePotionRecipeEnabled = config.getBoolean("config.cure potion.recipe.enabled");

        this.maskEnabled = config.getBoolean("config.mask.enabled");
        this.maskRecipeEnabled = config.getBoolean("config.mask.recipe.enabled");

    }

    //<editor-fold desc="Getters" default-state="collapsed">
    public boolean isCurePotionEnabled() {
        return this.curePotionEnabled;
    }

    public boolean isCurePotionRecipeEnabled() {
        return this.curePotionRecipeEnabled;
    }

    public boolean isMaskEnabled() {
        return this.maskEnabled;
    }

    public boolean isMaskRecipeEnabled() {
        return this.maskRecipeEnabled;
    }

    //</editor-fold>

    @Override
    public void reload(boolean deep) {
        this.loadFields();
    }
}