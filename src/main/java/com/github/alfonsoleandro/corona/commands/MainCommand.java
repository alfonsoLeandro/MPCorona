package com.github.alfonsoleandro.corona.commands;

import com.github.alfonsoleandro.corona.functions.FeelSymptoms;
import com.github.alfonsoleandro.corona.Corona;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.*;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MainCommand implements CommandExecutor {

    final private Corona plugin;
    final private FeelSymptoms feelSymptoms;
    //Messages
    private String noPerm;
    private String notInf;
    private String tooManyInf;
    private String mustBeInRadius;
    private String alreadyInf;
    private String worldDisabled;
    private String notCure;
    private String notInfected;
    private String notMoney;
    private String cureDisabled;
    private String disabled;
    private String notOnline;
    private String consoleMask;
    private String potionDisabled;
    private String consolePotion;
    private String checkSelf;
    private String checkOthers;
    private String checkInfected;
    private String checkHealthy;

    public MainCommand(Corona plugin, FeelSymptoms feelSymptoms){
        this.plugin = plugin;
        this.feelSymptoms = feelSymptoms;
        loadMessages();
    }

    private void send(CommandSender sender, String msg){
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("config.prefix")+" "+msg));
    }

    private void bCast(String msg){
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("config.prefix")+" "+msg));
    }

    private void loadMessages(){
        FileConfiguration config = plugin.getConfig();

        noPerm = config.getString("config.messages.no permission");
        notInf = config.getString("config.messages.you are not infected");
        tooManyInf = config.getString("config.messages.too many infected");
        mustBeInRadius = config.getString("config.messages.must be in radius");
        alreadyInf = config.getString("config.messages.already infected");
        worldDisabled = config.getString("config.messages.world disabled");
        notCure = config.getString("config.messages.cure disabled");
        notInfected = config.getString("config.messages.player not infected");
        notMoney = config.getString("config.messages.not enough money");
        cureDisabled = config.getString("config.messages.cure command disabled");
        disabled = config.getString("config.messages.mask disabled");
        notOnline = config.getString("config.messages.not online");
        consoleMask = config.getString("config.messages.console mask");
        potionDisabled = config.getString("config.messages.potion disabled");
        consolePotion = config.getString("config.messages.console potion");
        checkSelf = config.getString("config.messages.check.self");
        checkOthers = config.getString("config.messages.check.others");
        checkInfected = config.getString("config.messages.check infected");
        checkHealthy = config.getString("config.messages.check not infected");
    }




    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        FileConfiguration config = plugin.getConfig();


        if(args.length == 0 || args[0].equalsIgnoreCase("help")) {
            send(sender, "&6List of commands:");
            send(sender, "&f/" + label + " help");
            send(sender, "&f/" + label + " version");
            send(sender, "&f/" + label + " reload");
            send(sender, "&f/" + label + " infect (player)");
            send(sender, "&f/" + label + " cure (player)");
            send(sender, "&f/" + label + " giveMask <player>");
            send(sender, "&f/" + label + " givePotion <player>");
            send(sender, "&f/" + label + " check <player>");


        }else if(args[0].equalsIgnoreCase("reload")) {
            if(!sender.hasPermission("corona.reload")) {
                send(sender, noPerm);
                return true;
            }
            plugin.reloadConfig();
            plugin.reloadPlayers();
            feelSymptoms.cancel();
            feelSymptoms.start();
            plugin.cancelRandomSneezes();
            plugin.arrancarRandomSneezes();
            plugin.clearRecipes();
            plugin.registerMaskRecipe();
            plugin.registerPotionRecipe();
            loadMessages();
            send(sender, "&aFiles reloaded");





        }else if(args[0].equalsIgnoreCase("version")) {
            if(!sender.hasPermission("corona.version")) {
                send(sender, noPerm);
                return true;
            }
            send(sender, "&fVersion: "+plugin.getVersion());
            if(!plugin.getLatestVersion().equals(plugin.getVersion())){
                send(sender, "&cNew version available!");
                send(sender, "&fDownload: &ahttps://bit.ly/2XTzden");
            }else{
                send(sender, "&aYou have the latest version!");
            }






        }else if(args[0].equalsIgnoreCase("infect")) {
            FileConfiguration players = plugin.getPlayers();
            List<String> disabledWorlds = config.getStringList("config.disabled worlds");

            if(!config.getBoolean("config.infect command.enabled")) {
                send(sender, config.getString("config.messages.disabled"));
                return true;
            }

            if(sender instanceof Player){
                if(!sender.hasPermission("corona.infect")){
                    send(sender, noPerm);
                    return true;
                }
                if(disabledWorlds.contains(((Player) sender).getWorld().getName())) {
                    send(sender, worldDisabled);
                    return true;
                }
                if(!players.getStringList("players.infected").contains(sender.getName())) {
                    send(sender, notInf);
                    return true;
                }
                if(players.contains("players.to infect."+sender.getName())) {
                    if(players.getInt("players.to infect."+sender.getName()) >= config.getInt("config.infect command.infected per player")) {
                        send(sender, tooManyInf);
                        return true;
                    }
                }
            }

            if(args.length > 1 && Bukkit.getPlayer(args[1]) != null) {
                Player newInf = Bukkit.getPlayer(args[1]);
                double radius = Math.pow(config.getDouble("config.infect command.radius"), 2);

                if(!(sender instanceof Player) || ((Player)sender).getLocation().distanceSquared(newInf.getLocation()) <= radius) {

                    if(!players.getStringList("players.infected").contains(newInf.getName())) {

                        infect(newInf, sender);

                    }else {
                        send(sender, alreadyInf);
                    }


                }else {
                    send(sender, mustBeInRadius.replace("%radius%", String.valueOf(radius)));
                }



            }else {
                send(sender, "&cUse: &f/"+label+" infect (player)");
            }





        }else if(args[0].equalsIgnoreCase("cure")) {
            FileConfiguration players = plugin.getPlayers();

            if(!config.getBoolean("config.cure.enabled")){
                send(sender, notCure);
                return true;
            }

            if(!sender.hasPermission("corona.cure")){
                send(sender, noPerm);
                return true;
            }


            if(args.length <= 1 || Bukkit.getPlayer(args[1]) == null) {
                send(sender, "&cUse: &f/"+label+" cure (player)");
                return true;
            }
            Player toCure = Bukkit.getPlayer(args[1]);

            if(!players.getStringList("players.infected").contains(toCure.getName())) {
                send(sender, notInfected);
                return true;
            }


            if(sender instanceof Player){
                if(!plugin.setupEconomy()) {
                    send(sender, cureDisabled);
                    return true;
                }
                Economy econ = plugin.getEconomy();
                double price = config.getDouble("config.cure.price");

                if(econ.getBalance((Player) sender) >= price) {
                    econ.withdrawPlayer((Player) sender, price);
                }else {
                    send(sender, notMoney.replace("%price%", String.valueOf(price)));
                    return true;
                }

            }
            cure(toCure, sender);



        }else if(args[0].equalsIgnoreCase("giveMask")) {



            if(sender instanceof Player && !sender.hasPermission("corona.giveMask")){
                send(sender, noPerm);
                return true;
            }

            if(!config.getBoolean("config.mask.enabled")) {
                send(sender, disabled);
                return true;
            }

            if(args.length == 1) {
                if(sender instanceof ConsoleCommandSender) {
                    send(sender, consoleMask);
                    return true;
                }
                assert sender instanceof Player;
                addMask((Player) sender, sender);


            }else {

                Player toGive = Bukkit.getPlayer(args[1]);
                if(toGive != null) {
                    addMask(toGive, sender);
                }else {
                    send(sender, notOnline);
                }

            }
        }else if(args[0].equalsIgnoreCase("givePotion")) {


            if(sender instanceof Player && !sender.hasPermission("corona.givePotion")) {
                send(sender, noPerm);
                return true;
            }

            if(!config.getBoolean("config.cure potion.enabled")) {
                send(sender, potionDisabled);
                return true;
            }

            if(args.length == 1) {
                if(sender instanceof Player) {
                    addPotion((Player) sender, sender);
                }else {
                    send(sender, consolePotion);
                    return true;
                }


            } else {

                Player toGive = Bukkit.getPlayer(args[1]);
                if(toGive != null) {
                    addPotion(toGive, sender);
                } else {
                    send(sender, notOnline);
                }

            }


        }else if(args[0].equalsIgnoreCase("check")){
            if(args.length < 2){
                if(!sender.hasPermission("corona.check.self")){
                    send(sender, noPerm);
                    return true;
                }
                if(sender instanceof ConsoleCommandSender){
                    send(sender, "&cThe console cannot get infected");
                    return true;
                }
                boolean infected = plugin.getPlayers().getStringList("players.infected").contains(sender.getName());

                send(sender, this.checkSelf.replace("%infected%", infected ? checkInfected : checkHealthy));

            }else{
                if(!sender.hasPermission("corona.check.others")){
                    send(sender, noPerm);
                    return true;
                }
                if(Bukkit.getPlayer(args[1]) == null){
                    send(sender, notOnline);
                    return true;
                }

                boolean infected = plugin.getPlayers().getStringList("players.infected").contains(args[1]);

                send(sender, this.checkOthers.replace("%infected%", infected ? checkInfected : checkHealthy).replace("%player%", args[1]));


            }




            //unknown command
        }else {
            send(sender, "&cUnknown command, try &f/"+label+" help");

        }



        return true;
    }







    public ItemStack getMask() {
        final String path = "config.mask.";
        final String playerSkullTextureURL = plugin.getConfig().getString(path+".texture URL");
        final ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        final SkullMeta skull = (SkullMeta) head.getItemMeta();

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", playerSkullTextureURL).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField;
        try {
            profileField = skull.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skull, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
            e1.printStackTrace();
        }

        skull.setDisplayName(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(path+".name")));
        List<String> lore = new ArrayList<>();
        plugin.getConfig().getStringList(path+".lore").forEach(k -> lore.add(ChatColor.translateAlternateColorCodes('&', k)));
        skull.setLore(lore);
        head.setItemMeta(skull);
        head.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 10);

        return head;
    }

    public ItemStack getPotion(){
        String path = "config.cure potion";
        ItemStack potion = new ItemStack(Material.POTION);
        ItemMeta meta = potion.getItemMeta();
        ((PotionMeta)meta).addCustomEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1200, 4), true);
        ((PotionMeta)meta).addCustomEffect(new PotionEffect(PotionEffectType.WEAKNESS, 1200, 4), true);

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(path+".name")));
        List<String> lore = new ArrayList<>();
        for (String linea : plugin.getConfig().getStringList(path+".lore")) {
            lore.add(ChatColor.translateAlternateColorCodes('&', linea));
        }
        meta.setLore(lore);
        potion.setItemMeta(meta);

        return potion;
    }





    public boolean hasAvailableSlot(Inventory inv, ItemStack i){
        if(inv.firstEmpty() != -1) {
            return true;
        }else{
            for(int j = 0; j <= 35 ; j++) {
                if(inv.getItem(j).isSimilar(i) && inv.getItem(j).getAmount() < 64) {
                    return true;
                }
            }
        }


        return false;
    }


    public void addMask(Player player, CommandSender sender) {
        FileConfiguration config = plugin.getConfig();
        if(hasAvailableSlot(player.getInventory(), getMask())) {
            player.getInventory().addItem(getMask());

            if(player == sender) {
                send(sender,config.getString("config.messages.self received mask"));
            }else {
                send(sender, config.getString("config.messages.given mask").replace("%player%", player.getName()));
                send(player, config.getString("config.messages.received mask").replace("%player%", sender.getName()));
            }

        }else {
            send(sender, config.getString("config.messages.full inv").replace("%player%", player.getName()));
        }
    }

    public void addPotion(Player player, CommandSender sender){
        FileConfiguration config = plugin.getConfig();
        if(hasAvailableSlot(player.getInventory(), getPotion())) {
            player.getInventory().addItem(getPotion());

            if(player == sender) {
                send(sender,config.getString("config.messages.self received potion"));
            }else {
                send(sender, config.getString("config.messages.given potion").replace("%player%", player.getName()));
                send(player, config.getString("config.messages.received potion").replace("%player%", sender.getName()));
            }

        }else {
            send(sender, config.getString("config.messages.full inv").replace("%player%", player.getName()));
        }

    }





    public void infect(Player newinf, CommandSender infecter) {
        FileConfiguration config = plugin.getConfig();
        FileConfiguration players = plugin.getPlayers();
        String nowInfected = config.getString("config.messages.now infected");
        String justInfected = config.getString("config.messages.just infected someone");
        int toInf;

        if(players.contains("players.to infect."+infecter.getName())) {
            toInf = players.getInt("players.to infect."+infecter.getName())+1;
        }else {
            toInf = 1;
        }

        List<String> infected = players.getStringList("players.infected");
        infected.add(newinf.getName());
        players.set("players.infected", infected);
        if(infecter instanceof Player) {
            players.set("players.to infect."+infecter.getName(), toInf);
        }
        players.set("players.to infect."+newinf.getName(), 0);
        plugin.savePlayers();
        bCast(justInfected.replace("%infecter%", infecter.getName()).replace("%infected%", newinf.getName()));
        send(newinf, nowInfected);
    }

    public void cure(Player toCure, CommandSender curer){
        FileConfiguration config = plugin.getConfig();
        FileConfiguration players = plugin.getPlayers();
        String curedsmn = config.getString("config.messages.cured someone");
        String cured = config.getString("config.messages.cured you");
        String hascured = config.getString("config.messages.has cured");
        List<String> infected = players.getStringList("players.infected");


        infected.remove(toCure.getName());
        players.set("players.infected", infected);
        players.set("players.to infect."+toCure.getName(), 0);
        plugin.savePlayers();
        feelSymptoms.cancel();
        feelSymptoms.start();
        send(curer, curedsmn.replace("%cured%", toCure.getName()));
        send(toCure, cured.replace("%curer%", curer.getName()));
        bCast(hascured.replace("%curer%", curer.getName()).replace("%cured%", toCure.getName()));
    }
}
