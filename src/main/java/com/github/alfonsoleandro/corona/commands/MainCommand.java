package com.github.alfonsoleandro.corona.commands;

import com.github.alfonsoleandro.corona.commands.commandhandlers.*;
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
import org.jetbrains.annotations.NotNull;

public class MainCommand implements CommandExecutor {

    private final Corona plugin;
    private final AbstractHandler cor;
    //Messages
    private String noPerm;
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

    public MainCommand(Corona plugin){
        this.plugin = plugin;
        this.cor = new HelpHandler(this.plugin,
                new VersionHandler(this.plugin,
                        new ReloadHandler(this.plugin,
                                new InfectHandler(this.plugin,
                                        null)
                        )
                )
        );
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
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        FileConfiguration config = plugin.getConfig();
        this.cor.handle(sender, label, args);


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

        }else if(args[0].equalsIgnoreCase("cure")) {
            FileConfiguration players = plugin.getPlayersYaml().getAccess();

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
                boolean infected = plugin.getPlayersYaml().getAccess().getStringList("players.infected").contains(sender.getName());

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

                boolean infected = plugin.getPlayersYaml().getAccess().getStringList("players.infected").contains(args[1]);

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



    public void cure(Player toCure, CommandSender curer){
        FileConfiguration config = plugin.getConfig();
        FileConfiguration players = plugin.getPlayersYaml().getAccess();
        String curedsmn = config.getString("config.messages.cured someone");
        String cured = config.getString("config.messages.cured you");
        String hascured = config.getString("config.messages.has cured");
        List<String> infected = players.getStringList("players.infected");


        infected.remove(toCure.getName());
        players.set("players.infected", infected);
        players.set("players.to infect."+toCure.getName(), 0);
        plugin.getPlayersYaml().save(true);
//        feelSymptoms.cancel();
//        feelSymptoms.start();
        send(curer, curedsmn.replace("%cured%", toCure.getName()));
        send(toCure, cured.replace("%curer%", curer.getName()));
        bCast(hascured.replace("%curer%", curer.getName()).replace("%cured%", toCure.getName()));
    }
}
