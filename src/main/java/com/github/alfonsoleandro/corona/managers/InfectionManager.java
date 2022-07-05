package com.github.alfonsoleandro.corona.managers;

import com.github.alfonsoleandro.corona.Corona;
import com.github.alfonsoleandro.corona.utils.Message;
import com.github.alfonsoleandro.mputils.managers.MessageSender;
import com.github.alfonsoleandro.mputils.reloadable.Reloadable;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public class InfectionManager extends Reloadable {

    private final Corona plugin;
    private final MessageSender<Message> messageSender;
    private final Set<String> infectedPlayers = new HashSet<>();
    private final Map<String, Integer> playerInfections = new HashMap<>();

    public InfectionManager(Corona plugin) {
        super(plugin);
        this.plugin = plugin;
        this.messageSender = plugin.getMessageSender();
        loadInfectedPlayers();
        loadPlayerInfections();
    }

    public Set<String> getInfectedPlayers() {
        return this.infectedPlayers;
    }

    public boolean isInfected(String playerName){
        return this.infectedPlayers.contains(playerName);
    }

    public int getInfectedByPlayer(String playerName){
        return this.playerInfections.get(playerName);
    }


    public void infect(Player infected, String infecter, boolean infectedByPlayer){
        this.infectedPlayers.add(infected.getName());

        if(infectedByPlayer) {
            this.playerInfections.put(infecter,
                    this.playerInfections.getOrDefault(infecter, 1));
        }
        this.messageSender.broadcast(null, Message.JUST_INFECTED_SOMEONE,
                "%infecter%", infecter,
                "%infected%", infected.getName());


        this.messageSender.send(infected, Message.NOW_INFECTED);
    }

    public void cure(Player toCure, CommandSender curer){
        this.infectedPlayers.remove(toCure.getName());
        this.playerInfections.remove(toCure.getName());

        this.messageSender.send(curer, Message.CURED_SOMEONE,
                "%cured%", toCure.getName());
        this.messageSender.send(curer, Message.CURED_YOU,
                "%curer%", curer.getName());
        this.messageSender.broadcast(null, Message.HAS_CURED,
                "%curer%", curer.getName(),
                "%cured%", toCure.getName());
    }

    public void cureByPotion(Player toCure){
        this.infectedPlayers.remove(toCure.getName());
        this.playerInfections.remove(toCure.getName());

        this.messageSender.broadcast(null, Message.SOMEONE_CURED_BY_POTION,
                "%cured%", toCure.getName());
    }


    private void loadInfectedPlayers(){
        FileConfiguration players = this.plugin.getPlayersYaml().getAccess();
        this.infectedPlayers.addAll(players.getStringList("players.infected"));
    }

    private void loadPlayerInfections(){
        ConfigurationSection playerInfectionSection = this.plugin.getPlayersYaml().getAccess()
                .getConfigurationSection("players.to infect");
        if(playerInfectionSection == null) return;
        for(String playerName : playerInfectionSection.getKeys(false)){
            this.playerInfections.put(playerName, playerInfectionSection.getInt(playerName));
        }
    }

    public void saveToFile() {
        FileConfiguration players = this.plugin.getPlayersYaml().getAccess();
        players.set("players.infected", this.infectedPlayers);

        for(String playerName : this.playerInfections.keySet()){
            players.set("players.to infect."+playerName, this.playerInfections.get(playerName));
        }

        this.plugin.getPlayersYaml().save(false);
    }


    @Override
    public void reload(boolean deep) {
        //TODO ?
    }
}
