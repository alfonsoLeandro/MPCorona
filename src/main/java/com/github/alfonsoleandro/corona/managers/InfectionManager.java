package com.github.alfonsoleandro.corona.managers;

import com.github.alfonsoleandro.corona.Corona;
import com.github.alfonsoleandro.mputils.reloadable.Reloadable;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashSet;
import java.util.Set;

public class InfectionManager extends Reloadable {

    private final Corona plugin;
    private final Set<String> infectedPlayers = new HashSet<>();

    public InfectionManager(Corona plugin) {
        super(plugin);
        this.plugin = plugin;
        loadInfectedPlayers();
    }

    public boolean isInfected(String playerName){
        return this.infectedPlayers.contains(playerName);
    }


    private void loadInfectedPlayers(){
        FileConfiguration players = this.plugin.getPlayersYaml().getAccess();
        this.infectedPlayers.addAll(players.getStringList("players.infected"));
    }

    public void saveInfectedPlayersToFile() {
        FileConfiguration players = this.plugin.getPlayersYaml().getAccess();
        players.set("players.infected", this.infectedPlayers);
        this.plugin.getPlayersYaml().save(false);
    }


    @Override
    public void reload(boolean deep) {
        //TODO ?
    }
}
