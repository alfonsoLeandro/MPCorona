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

    public boolean isInfected(String playerName){
        return this.infectedPlayers.contains(playerName);
    }

    public int getInfectedByPlayer(String playerName){
        return this.playerInfections.get(playerName);
    }


    public void infect(Player infected, CommandSender infecter){
        this.infectedPlayers.add(infected.getName());

        if(infecter instanceof Player) {
            this.playerInfections.put(infecter.getName(),
                    this.playerInfections.getOrDefault(infecter.getName(), 1));
        }
        this.messageSender.broadcast(null, Message.JUST_INFECTED_SOMEONE,
                "%infecter%", infecter.getName(),
                "%infected%", infected.getName());


        this.messageSender.send(infected, Message.NOW_INFECTED);
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
