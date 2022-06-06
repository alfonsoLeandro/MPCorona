package com.github.alfonsoleandro.corona.events;

import com.github.alfonsoleandro.corona.Corona;
import com.github.alfonsoleandro.corona.utils.Message;
import com.github.alfonsoleandro.mputils.managers.MessageSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinEvent implements Listener {

    private final Corona plugin;
    private final MessageSender<Message> messageSender;

    public JoinEvent(Corona plugin) {
        this.plugin = plugin;
        this.messageSender = plugin.getMessageSender();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        //OP
        if(player.isOp()) {
            if(!this.plugin.getVersion().equals(this.plugin.getLatestVersion())) {
                this.messageSender.send(player, "&4New version available &7(&e"+this.plugin.getLatestVersion()+"&7)");
                this.messageSender.send(player, "&fhttps://bit.ly/2XTzden");
            }
        }
    }
}
