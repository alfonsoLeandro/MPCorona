package com.github.alfonsoleandro.corona;

import com.github.alfonsoleandro.corona.commands.MainCommand;
import com.github.alfonsoleandro.corona.commands.MainCommandTabCompleter;
import com.github.alfonsoleandro.corona.events.*;
import com.github.alfonsoleandro.corona.functions.FeelSymptoms;
import com.github.alfonsoleandro.corona.functions.RandomSneezes;
import com.github.alfonsoleandro.corona.utils.Message;
import com.github.alfonsoleandro.corona.utils.PAPI;
import com.github.alfonsoleandro.mputils.files.YamlFile;
import com.github.alfonsoleandro.mputils.managers.MessageSender;
import com.github.alfonsoleandro.mputils.reloadable.ReloaderPlugin;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public final class Corona extends ReloaderPlugin {

    private final String version = getDescription().getVersion();
    private MessageSender<Message> messageSender;
    private Economy economy;
    private String latestVersion;
    private PAPI papiExpansion;
    private YamlFile configYaml;
    private YamlFile playersYaml;


    @Override
    public void onEnable() {
        registerFiles();
        checkConfigFields();
        this.messageSender = new MessageSender<>(this, Message.values(), this.configYaml, "config.prefix");
        this.messageSender.send("&aEnabled&f. Version: &e" + this.version);
        this.messageSender.send("&fThank you for using my plugin! &c" + getDescription().getName() + "&f By " + getDescription().getAuthors().get(0));
        this.messageSender.send("&fJoin my discord server at &chttps://discordapp.com/invite/ZznhQud");
        this.messageSender.send("Please consider subscribing to my yt channel: &c" + getDescription().getWebsite());
        if(setupEconomy()){
            this.messageSender.send("&aPlugin VAULT found");
        }else {
            this.messageSender.send("&cPlugin VAULT not found, disabling economy");
        }
        arrancarFeelSymptoms();
        registerEvents();
        registerCommands();
        registerMaskRecipe();
        registerPotionRecipe();
        firstRun();
        arrancarRandomSneezes();
        registerPAPIExpansion();
        updateChecker();
    }

    @Override
    public void onDisable() {
        this.messageSender.send("&cDisabled&f. Version: &e" + this.version);
        this.messageSender.send("&fThank you for using my plugin! &c" + getDescription().getName() + "&f By " + getDescription().getAuthors().get(0));
        this.messageSender.send("&fJoin my discord server at &chttps://discordapp.com/invite/ZznhQud");
        this.messageSender.send("Please consider subscribing to my yt channel: &c" + getDescription().getWebsite());
        clearRecipes();
        unRegisterPAPIExpansion();
    }



    public boolean setupEconomy() {
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

    public Economy getEconomy() {
        return this.economy;
    }


    public void registerPAPIExpansion(){
        Plugin papi = getServer().getPluginManager().getPlugin("PlaceholderAPI");
        if(papi != null && papi.isEnabled()){
            this.messageSender.send("&aPlaceholderAPI found, the placeholder has been registered successfully");
            this.papiExpansion = new PAPI(this);
            this.papiExpansion.register();
        }else{
            this.messageSender.send("&cPlaceholderAPI not found, the placeholder was not registered");
        }
    }

    public void unRegisterPAPIExpansion(){
        if(this.papiExpansion != null) this.papiExpansion.unregister();
    }



    //
    //updates
    //
    public void updateChecker(){
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


    //
    //getversion
    //
    public String getVersion() {
        return this.version;
    }
    public String getLatestVersion() {
        return this.latestVersion;
    }

    FeelSymptoms symptoms;

    public void arrancarFeelSymptoms() {
        this.symptoms = new FeelSymptoms(this);
        this.symptoms.start();
    }

    RandomSneezes random;

    public void firstRun(){
        this.random = new RandomSneezes(this);
    }

    public void arrancarRandomSneezes() {
        if(getConfig().getBoolean("config.infected.random sneezes.enabled")) {
            this.random.randomSneezes();
        }else {
            this.messageSender.send("&cRandom sneezes is disabled, to enable it enable it in your config.yml and reload the plugin");
        }
    }

    public void cancelRandomSneezes(){
        this.random.cancel();
    }

    /**
     * Checks for config fields that should exist
     */
    private void checkConfigFields(){
        FileConfiguration configEndFile = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));
        FileConfiguration config = getConfig();

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

    }


    /**
     * Registers MPCorona's events.
     */
    public void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new DamageEvent(this), this);
        pm.registerEvents(new ItemCraft(this), this);
        pm.registerEvents(new PlaceEvent(this), this);
        pm.registerEvents(new JoinEvent(this), this);
        pm.registerEvents(new CureByPotion(this, this.symptoms), this);
    }


    /**
     * Register MPCorona's commands.
     */
    public void registerCommands() {
        PluginCommand mainCommand = getCommand("corona");

        if(mainCommand == null){
            this.messageSender.send("&cThe main command has not been registered properly. Please check your plugin.yml is valid");
            this.setEnabled(false);
            return;
        }

        mainCommand.setExecutor(new MainCommand(this, this.symptoms));
        mainCommand.setTabCompleter(new MainCommandTabCompleter());
    }

    private void registerFiles(){
        this.configYaml = new YamlFile(this, "config.yml");
        this.playersYaml = new YamlFile(this, "players.yml");
    }

    public void reloadFiles(){
        this.configYaml.loadFileConfiguration();
        this.playersYaml.loadFileConfiguration();
    }

    @Override
    public void reload(boolean deep){
        reloadFiles();
        super.reload(deep);
    }




    //
    //REGISTER CURE POTION
    //

    public void registerPotionRecipe() {

        if(getConfig().getBoolean("config.cure potion.enabled") && getConfig().getBoolean("config.cure potion.recipe.enabled")) {

            String path = "config.cure potion";
            ItemStack potion = new ItemStack(Material.POTION);
            ItemMeta meta = potion.getItemMeta();
            assert meta != null;
            ((PotionMeta)meta).addCustomEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1200, 4), true);
            ((PotionMeta)meta).addCustomEffect(new PotionEffect(PotionEffectType.WEAKNESS, 1200, 4), true);

            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', getConfig().getString(path+".name")));
            List<String> lore = new ArrayList<>();
            for (String linea : getConfig().getStringList(path+".lore")) {
                lore.add(ChatColor.translateAlternateColorCodes('&', linea));
            }
            meta.setLore(lore);
            potion.setItemMeta(meta);

            NamespacedKey key = new NamespacedKey(this, "Potion");
            ShapedRecipe autof1 = new ShapedRecipe(key, potion);
            autof1.shape("ABC","DEF","GHI");
            autof1.setIngredient('A', Material.valueOf(getConfig().getString(path+".recipe.A")));
            autof1.setIngredient('B', Material.valueOf(getConfig().getString(path+".recipe.B")));
            autof1.setIngredient('C', Material.valueOf(getConfig().getString(path+".recipe.C")));
            autof1.setIngredient('D', Material.valueOf(getConfig().getString(path+".recipe.D")));
            autof1.setIngredient('E', Material.valueOf(getConfig().getString(path+".recipe.E")));
            autof1.setIngredient('F', Material.valueOf(getConfig().getString(path+".recipe.F")));
            autof1.setIngredient('G', Material.valueOf(getConfig().getString(path+".recipe.G")));
            autof1.setIngredient('H', Material.valueOf(getConfig().getString(path+".recipe.H")));
            autof1.setIngredient('I', Material.valueOf(getConfig().getString(path+".recipe.I")));

            Bukkit.addRecipe(autof1);
        }
    }



    //
    //REGISTRAR MASK
    //

    public void registerMaskRecipe() {

        if(getConfig().getBoolean("config.mask.enabled") && getConfig().getBoolean("config.mask.recipe.enabled")) {

            String path = "config.mask";
            String playerSkullTextureURL = getConfig().getString(path+".texture URL");
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skull = (SkullMeta) head.getItemMeta();

            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", playerSkullTextureURL).getBytes());
            profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
            Field profileField;
            try {
                assert skull != null;
                profileField = skull.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(skull, profile);
            } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
                e1.printStackTrace();
            }

            skull.setDisplayName(ChatColor.translateAlternateColorCodes('&', getConfig().getString(path+".name")));
            List<String> lore = new ArrayList<>();
            for (String linea : getConfig().getStringList(path+".lore")) {
                lore.add(ChatColor.translateAlternateColorCodes('&', linea));
            }
            skull.setLore(lore);
            head.setItemMeta(skull);
            head.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 10);

            NamespacedKey key = new NamespacedKey(this, "Mask");
            ShapedRecipe maskRecipe = new ShapedRecipe(key, head);
            maskRecipe.shape("ABC","DEF","GHI");
            maskRecipe.setIngredient('A', Material.valueOf(getConfig().getString(path+".recipe.A")));
            maskRecipe.setIngredient('B', Material.valueOf(getConfig().getString(path+".recipe.B")));
            maskRecipe.setIngredient('C', Material.valueOf(getConfig().getString(path+".recipe.C")));
            maskRecipe.setIngredient('D', Material.valueOf(getConfig().getString(path+".recipe.D")));
            maskRecipe.setIngredient('E', Material.valueOf(getConfig().getString(path+".recipe.E")));
            maskRecipe.setIngredient('F', Material.valueOf(getConfig().getString(path+".recipe.F")));
            maskRecipe.setIngredient('G', Material.valueOf(getConfig().getString(path+".recipe.G")));
            maskRecipe.setIngredient('H', Material.valueOf(getConfig().getString(path+".recipe.H")));
            maskRecipe.setIngredient('I', Material.valueOf(getConfig().getString(path+".recipe.I")));

            Bukkit.addRecipe(maskRecipe);
        }
    }

    public MessageSender<Message> getMessageSender() {
        return this.messageSender;
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

    //limpiar recipes y checkear si esta registrada

    public void clearRecipes() {
        Iterator<Recipe> it = getServer().recipeIterator();
        Recipe recipe;
        while(it.hasNext()){
            recipe = it.next();
            if(((Keyed)recipe).getKey().getNamespace().contains("mpcorona")){
                it.remove();
            }
        }
    }


}
