package com.github.alfonsoleandro.corona;

import com.github.alfonsoleandro.corona.commands.MainCommand;
import com.github.alfonsoleandro.corona.commands.MainCommandTabCompleter;
import com.github.alfonsoleandro.corona.events.*;
import com.github.alfonsoleandro.corona.functions.FeelSymptoms;
import com.github.alfonsoleandro.corona.functions.RandomSneezes;
import com.github.alfonsoleandro.corona.managers.InfectionManager;
import com.github.alfonsoleandro.corona.managers.RecipesManager;
import com.github.alfonsoleandro.corona.managers.Settings;
import com.github.alfonsoleandro.corona.utils.Message;
import com.github.alfonsoleandro.corona.utils.PAPI;
import com.github.alfonsoleandro.mputils.files.YamlFile;
import com.github.alfonsoleandro.mputils.managers.MessageSender;
import com.github.alfonsoleandro.mputils.reloadable.ReloaderPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public final class Corona extends ReloaderPlugin {

    private final String version = getDescription().getVersion();
    private String latestVersion;
    //Managers
    private MessageSender<Message> messageSender;
    private Settings settings;
    private InfectionManager infectionManager;
    private RecipesManager recipesManager;
    //Configuration files
    private YamlFile configYaml;
    private YamlFile playersYaml;
    //3rd party plugin hooks
    private Economy economy;
    private PAPI papiExpansion;


    @Override
    public void onEnable() {
        registerFiles();
        checkConfigFields();
        this.messageSender = new MessageSender<>(this, Message.values(), this.configYaml, "config.prefix");
        this.messageSender.send("&aEnabled&f. Version: &e" + this.version);
        this.messageSender.send("&fThank you for using my plugin! &c" + getDescription().getName() + "&f By " + getDescription().getAuthors().get(0));
        this.messageSender.send("&fJoin my discord server at &chttps://discordapp.com/invite/ZznhQud");
        this.messageSender.send("Please consider subscribing to my yt channel: &c" + getDescription().getWebsite());
        this.settings = new Settings(this);
        this.infectionManager = new InfectionManager(this);
        this.recipesManager = new RecipesManager(this);
        if(setupEconomy()){
            this.messageSender.send("&aPlugin VAULT found");
        }else {
            this.messageSender.send("&cPlugin VAULT not found, disabling economy");
        }
        new FeelSymptoms(this).start();
        new RandomSneezes(this);
        registerEvents();
        registerCommands();
        registerPAPIExpansion();
        updateChecker();
    }

    @Override
    public void onDisable() {
        this.messageSender.send("&cDisabled&f. Version: &e" + this.version);
        this.messageSender.send("&fThank you for using my plugin! &c" + getDescription().getName() + "&f By " + getDescription().getAuthors().get(0));
        this.messageSender.send("&fJoin my discord server at &chttps://discordapp.com/invite/ZznhQud");
        this.messageSender.send("Please consider subscribing to my yt channel: &c" + getDescription().getWebsite());
        unRegisterPAPIExpansion();
        this.infectionManager.saveToFile();
        this.recipesManager.unregisterRecipes();
    }


    /**
     * Prepares the economy to be used by this plugin.
     * @return true if the economy is correctly setup. False otherwise.
     */
    private boolean setupEconomy() {
        if(!getServer().getPluginManager().isPluginEnabled("Vault")) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if(rsp==null) {
            return false;
        }
        this.economy = rsp.getProvider();
        return true;
    }


    /**
     * Registers the PlaceholderAPI expansion.
     */
    private void registerPAPIExpansion(){
        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")){
            this.messageSender.send("&aPlaceholderAPI found, the placeholder has been registered successfully");
            this.papiExpansion = new PAPI(this);
            this.papiExpansion.register();
        }else{
            this.messageSender.send("&cPlaceholderAPI not found, the placeholder was not registered");
        }
    }

    /**
     * Unregisters the PlaceholderAPI expansion
     */
    private void unRegisterPAPIExpansion(){
        if(this.papiExpansion != null) this.papiExpansion.unregister();
    }


    /**
     * Checks for new versions in spigot.
     */
    private void updateChecker(){
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(
                    "https://api.spigotmc.org/legacy/update.php?resource=77694").openConnection();
            final int timed_out = 1250;
            con.setConnectTimeout(timed_out);
            con.setReadTimeout(timed_out);
            this.latestVersion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
            if (this.latestVersion.length() <= 7) {
                if(!this.version.equals(this.latestVersion)){
                    this.messageSender.send("&e(&c&l!&e)&c There is a new version available. &e(&7"+ this.latestVersion +"&e)");
                    this.messageSender.send("&e(&c&l!&e)&c Download it here:&f https://bit.ly/2XTzden");
                }
            }
        } catch (Exception ex) {
            this.messageSender.send("Error while checking updates.");
        }
    }

    /**
     * Checks for config fields that should exist
     */
    private void checkConfigFields(){
        FileConfiguration configEndFile = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));
        FileConfiguration config = getConfigYaml().getAccess();

        if(!configEndFile.contains("config.messages.check.self")){
            config.set("config.messages.check.self", "&fYou are %infected%");
            this.configYaml.save(true);

        }if(!configEndFile.contains("config.messages.check.others")){
            config.set("config.messages.check.others", "&f%player% is %infected%");
            this.configYaml.save(true);

        }if(!configEndFile.contains("config.messages.check infected")) {
            config.set("config.messages.check infected", "&cInfected");
            this.configYaml.save(true);

        }
        if(!configEndFile.contains("config.messages.check not infected")) {
            config.set("config.messages.check not infected", "&aHealthy");
            this.configYaml.save(true);
        }
        if(!configEndFile.contains("config.message.reloaded")) {
            config.set("config.messages.reloaded", "&aPlugin reloaded!");
            this.configYaml.save(true);
        }
        if(!configEndFile.contains("config.message.unknown command")) {
            config.set("config.messages.unknown command", "&cUnknown command. Run &e/%command% help &cto see a list of commands");
            this.configYaml.save(true);
        }
        if(!configEndFile.contains("config.message.console cannot get infected")) {
            config.set("config.messages.console cannot get infected", "&cThe console cannot get infected");
            this.configYaml.save(true);
        }

    }


    /**
     * Registers MPCorona's events.
     */
    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new DamageEvent(this), this);
        pm.registerEvents(new ItemCraft(this), this);
        pm.registerEvents(new PlaceEvent(this), this);
        pm.registerEvents(new JoinEvent(this), this);
        pm.registerEvents(new CureByPotion(this), this);
    }


    /**
     * Register MPCorona's commands.
     */
    private void registerCommands() {
        PluginCommand mainCommand = getCommand("corona");

        if(mainCommand == null){
            this.messageSender.send("&cThe main command has not been registered properly. Please check your plugin.yml is valid");
            this.setEnabled(false);
            return;
        }

        mainCommand.setExecutor(new MainCommand(this));
        mainCommand.setTabCompleter(new MainCommandTabCompleter());
    }

    /**
     * Registers the files needed for this plugin, associates the physical files
     * with variables in this plugin and creates those physical files if they don't already
     * exist.
     */
    private void registerFiles(){
        this.configYaml = new YamlFile(this, "config.yml");
        this.playersYaml = new YamlFile(this, "players.yml");
    }

    /**
     * Reloads the file for this plugin.
     */
    private void reloadFiles(){
        this.configYaml.loadFileConfiguration();
        this.playersYaml.loadFileConfiguration();
    }

    @Override
    public void reload(boolean deep){
        reloadFiles();
        this.settings.reload(false);
        super.reload(deep);
    }


    /**
     * Gets the plugin's current version.
     * @return The current version as a string.
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * Gets the plugin's latest version available in spigot.
     * @return The plugin's latest version available for download.
     */
    public String getLatestVersion() {
        return this.latestVersion;
    }

    public Economy getEconomy() {
        return this.economy;
    }

    public MessageSender<Message> getMessageSender() {
        return this.messageSender;
    }

    public Settings getSettings() {
        return this.settings;
    }

    public InfectionManager getInfectionManager() {
        return this.infectionManager;
    }

    public RecipesManager getRecipesManager() {
        return this.recipesManager;
    }

    @Override
    public @NotNull FileConfiguration getConfig(){
        return this.configYaml.getAccess();
    }

    public YamlFile getConfigYaml() {
        return this.configYaml;
    }

    public YamlFile getPlayersYaml() {
        return this.playersYaml;
    }


}
//TODO: deploy MPUtils before uploading
