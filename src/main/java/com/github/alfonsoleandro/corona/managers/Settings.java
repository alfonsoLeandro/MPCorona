package com.github.alfonsoleandro.corona.managers;

import com.github.alfonsoleandro.corona.Corona;
import com.github.alfonsoleandro.mputils.reloadable.Reloadable;
import com.github.alfonsoleandro.mputils.sound.SoundSettings;
import com.github.alfonsoleandro.mputils.time.TimeUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class Settings extends Reloadable {

    private final Corona plugin;

    //<editor-fold desc="Fields" default-state="collapsed">
    private boolean curePotionDisabled;
    private boolean curePotionRecipeDisabled;
    private boolean maskDisabled;
    private boolean maskRecipeDisabled;
    private boolean infectCommandDisabled;
    private boolean cureCommandDisabled;
    private boolean symptomsDisabledInDisabledWorlds;
    private boolean symptomSoundEnabled;
    private boolean sneezesSoundEnabled;
    private boolean randomSneezesEnabled;

    private int maxInfectedPerPlayer;
    private int infectRadius;
    private int symptomsIntervalTicks;
    private int sneezesIntervalTicks;
    private int sneezesRadiusSquared;
    private int probToInfectUsingMask;
    private int probToInfectWithoutMask;
    private int probToMobInfectWithMask;
    private int probToMobInfectWithoutMask;
    private int probToInfectByFood;

    private double curePrice;

    private String maskSkinURL;
    private String maskItemName;
    private String curePotionItemName;
    private String randomSneezesMode;

    private List<String> disabledWorlds;
    private List<String> maskItemLore;
    private List<String> curePotionItemLore;
    private List<String> possibleSymptoms;
    private List<String> mobsThatInfect;
    private List<String> foodThatInfect;

    private SoundSettings symptomsSound;
    private SoundSettings sneezesSound;
    //</editor-fold>

    public Settings(Corona plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    private void loadFields() {
        FileConfiguration config = this.plugin.getConfigYaml().getAccess();

        this.curePotionDisabled = !config.getBoolean("config.cure potion.enabled");
        this.curePotionRecipeDisabled = !config.getBoolean("config.cure potion.recipe.enabled");
        this.maskDisabled = !config.getBoolean("config.mask.enabled");
        this.maskRecipeDisabled = !config.getBoolean("config.mask.recipe.enabled");
        this.infectCommandDisabled = !config.getBoolean("config.infect command.enabled");
        this.cureCommandDisabled = !config.getBoolean("config.cure.enabled");
        this.symptomsDisabledInDisabledWorlds = config.getBoolean("config.symptoms disabled in disabled worlds");
        this.symptomSoundEnabled = config.getBoolean("config.sound.enabled");
        this.sneezesSoundEnabled = config.getBoolean("config.infected.random sneezes.sound.enabled");
        this.randomSneezesEnabled = config.getBoolean("config.infected.random sneezes.enabled");

        this.maxInfectedPerPlayer = config.getInt("config.infect command.infected per player");
        this.infectRadius = config.getInt("config.infect command.radius");
        this.symptomsIntervalTicks = TimeUtils.getTicks(config.getString("config.infected.interval"));
        this.sneezesIntervalTicks = TimeUtils.getTicks(config.getString("config.infected.random sneezes.interval"));
        this.sneezesRadiusSquared = (int) Math.pow(config.getInt("config.infected.random sneezes.radius"), 2);
        this.probToInfectUsingMask = config.getInt("config.infected.random sneezes.mask effectiveness");
        this.probToInfectWithoutMask = config.getInt("config.infected.random sneezes.probability to infect");
        this.probToMobInfectWithMask = config.getInt("config.chance to infect.mob.with mask");
        this.probToMobInfectWithoutMask = config.getInt("config.chance to infect.mob.without mask");
        this.probToInfectByFood = config.getInt("config.chance to infect.food");


        this.curePrice = config.getDouble("config.cure.price");

        this.maskSkinURL = config.getString("config.mask.texture URL");
        this.maskItemName = config.getString("config.mask.name");
        this.curePotionItemName = config.getString("config.cure potion.name");
        this.randomSneezesMode = config.getString("config.infected.random sneezes.mode");

        this.disabledWorlds = config.getStringList("config.disabled worlds");
        this.maskItemLore = config.getStringList("config.mask.lore");
        this.curePotionItemLore = config.getStringList("config.cure potion.lore");
        this.possibleSymptoms = config.getStringList("config.infected.symptoms");
        this.mobsThatInfect = config.getStringList("config.mobs that can infect");
        this.foodThatInfect = config.getStringList("config.food that can infect");

        this.symptomsSound = new SoundSettings(config.getString("config.sound.sound"), .6F, 1F);
        this.sneezesSound = new SoundSettings(config.getString("config.infected.random sneezes.sound.params.sound"),
                config.getDouble("config.infected.random sneezes.sound.params.volume"),
                config.getDouble("config.infected.random sneezes.sound.params.pitch"));
    }

    //<editor-fold desc="Getters" default-state="collapsed">
    public boolean isCurePotionDisabled() {
        return this.curePotionDisabled;
    }

    public boolean isCurePotionRecipeDisabled() {
        return this.curePotionRecipeDisabled;
    }

    public boolean isMaskDisabled() {
        return this.maskDisabled;
    }

    public boolean isMaskRecipeDisabled() {
        return this.maskRecipeDisabled;
    }

    public boolean isInfectCommandDisabled() {
        return this.infectCommandDisabled;
    }

    public boolean isCureCommandDisabled() {
        return this.cureCommandDisabled;
    }

    public boolean isSymptomsDisabledInDisabledWorlds() {
        return this.symptomsDisabledInDisabledWorlds;
    }

    public boolean isSymptomSoundEnabled() {
        return this.symptomSoundEnabled;
    }

    public boolean isSneezesSoundEnabled() {
        return this.sneezesSoundEnabled;
    }

    public boolean isRandomSneezesEnabled() {
        return this.randomSneezesEnabled;
    }

    public int getMaxInfectedPerPlayer() {
        return this.maxInfectedPerPlayer;
    }

    public int getInfectRadius() {
        return this.infectRadius;
    }

    public int getSymptomsIntervalTicks() {
        return this.symptomsIntervalTicks;
    }

    public int getSneezesIntervalTicks() {
        return this.sneezesIntervalTicks;
    }

    public int getSneezesRadiusSquared() {
        return this.sneezesRadiusSquared;
    }

    public int getProbToInfectUsingMask() {
        return this.probToInfectUsingMask;
    }

    public int getProbToInfectWithoutMask() {
        return this.probToInfectWithoutMask;
    }

    public int getProbToMobInfectWithMask() {
        return this.probToMobInfectWithMask;
    }

    public int getProbToMobInfectWithoutMask() {
        return this.probToMobInfectWithoutMask;
    }

    public int getProbToInfectByFood() {
        return this.probToInfectByFood;
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

    public String getRandomSneezesMode() {
        return this.randomSneezesMode;
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

    public List<String> getPossibleSymptoms() {
        return this.possibleSymptoms;
    }

    public List<String> getMobsThatInfect() {
        return this.mobsThatInfect;
    }

    public List<String> getFoodThatInfect() {
        return this.foodThatInfect;
    }

    public SoundSettings getSymptomsSound() {
        return this.symptomsSound;
    }

    public SoundSettings getSneezesSound() {
        return this.sneezesSound;
    }

    //</editor-fold>

    @Override
    public void reload(boolean deep) {
        this.loadFields();
    }
}