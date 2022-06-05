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
    private boolean cureCommandDisabled;

    private int maxInfectedPerPlayer;
    private int infectRadius;

    private double curePrice;

    private String maskSkinURL;
    private String maskItemName;
    private String curePotionItemName;

    private List<String> disabledWorlds;
    private List<String> maskItemLore;
    private List<String> curePotionItemLore;
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
        this.cureCommandDisabled = !config.getBoolean("config.cure.enabled");

        this.maxInfectedPerPlayer = config.getInt("config.infect command.infected per player");
        this.infectRadius = config.getInt("config.infect command.radius");

        this.curePrice = config.getDouble("config.cure.price");

        this.maskSkinURL = config.getString("config.mask.texture URL");
        this.maskItemName = config.getString("config.mask.name");
        this.curePotionItemName = config.getString("config.cure potion.name");

        this.disabledWorlds = config.getStringList("config.disabled worlds");
        this.maskItemLore = config.getStringList("config.mask.lore");
        this.curePotionItemLore = config.getStringList("config.cure potion.lore");

    }

    //<editor-fold desc="Getters" default-state="collapsed">
    public boolean isCurePotionDisabled() {
        return !this.curePotionEnabled;
    }

    public boolean isCurePotionRecipeEnabled() {
        return this.curePotionRecipeEnabled;
    }

    public boolean isMaskDisabled() {
        return !this.maskEnabled;
    }

    public boolean isMaskRecipeEnabled() {
        return this.maskRecipeEnabled;
    }

    public boolean isInfectCommandDisabled() {
        return this.infectCommandDisabled;
    }

    public boolean isCureCommandDisabled() {
        return this.cureCommandDisabled;
    }


    public int getMaxInfectedPerPlayer() {
        return this.maxInfectedPerPlayer;
    }

    public int getInfectRadius() {
        return this.infectRadius;
    }

    public double getCurePrice() {
        return this.curePrice;
    }

    public String getMaskSkinURL() {
        return this.maskSkinURL;
    }

    public String getMaskItemName() {
        return this.maskItemName;
    }

    public String getCurePotionItemName() {
        return this.curePotionItemName;
    }

    public List<String> getDisabledWorlds() {
        return this.disabledWorlds;
    }

    public List<String> getMaskItemLore() {
        return this.maskItemLore;
    }

    public List<String> getCurePotionItemLore() {
        return this.curePotionItemLore;
    }

    //</editor-fold>

    @Override
    public void reload(boolean deep) {
        this.loadFields();
    }
}