package com.github.alfonsoleandro.corona.managers;

import com.github.alfonsoleandro.corona.Corona;
import com.github.alfonsoleandro.mputils.reloadable.Reloadable;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class Settings extends Reloadable {

    private final Corona plugin;

    //<editor-fold desc="Fields" default-state="collapsed">
    private boolean curePotionEnabled;
    private boolean curePotionRecipeEnabled;
    private boolean maskEnabled;
    private boolean maskRecipeEnabled;
    private boolean infectCommandDisabled;

    private int maxInfectedPerPlayer;
    private int infectRadius;

    private List<String> disabledWorlds;
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
        this.infectCommandDisabled = !config.getBoolean("config.infect command.enabled");

        this.maxInfectedPerPlayer = config.getInt("config.infect command.infected per player");
        this.infectRadius = config.getInt("config.infect command.radius");

        this.disabledWorlds = config.getStringList("config.disabled worlds");

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

    public boolean isInfectCommandDisabled() {
        return this.infectCommandDisabled;
    }

    public int getMaxInfectedPerPlayer() {
        return this.maxInfectedPerPlayer;
    }

    public int getInfectRadius() {
        return this.infectRadius;
    }

    public List<String> getDisabledWorlds() {
        return this.disabledWorlds;
    }

    //</editor-fold>

    @Override
    public void reload(boolean deep) {
        this.loadFields();
    }
}