package com.github.alfonsoleandro.corona.events;

import com.github.alfonsoleandro.corona.Corona;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinEvent implements Listener {

    private Corona plugin;

    public JoinEvent(Corona plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        //OP
        if(player.isOp()) {
            if(!plugin.getVersion().equals(plugin.getLatestVersion())) {
                String prefix = plugin.getConfig().getString("config.prefix");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+"&4New version available &7(&e")+plugin.getLatestVersion()+ChatColor.GRAY+")");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+"&fhttps://bit.ly/2XTzden") );
            }
        }
    }
}
