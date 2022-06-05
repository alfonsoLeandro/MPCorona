package com.github.alfonsoleandro.corona.functions;

import com.github.alfonsoleandro.corona.Corona;
import com.github.alfonsoleandro.corona.managers.InfectionManager;
import com.github.alfonsoleandro.corona.managers.Settings;
import com.github.alfonsoleandro.corona.utils.Message;
import com.github.alfonsoleandro.mputils.managers.MessageSender;
import com.github.alfonsoleandro.mputils.reloadable.Reloadable;
import com.github.alfonsoleandro.mputils.sound.SoundUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class FeelSymptoms extends Reloadable {

    private final Corona plugin;
    private final InfectionManager infectionManager;
    private final Settings settings;
    private final MessageSender<Message> messageSender;
    private final Random r;
    private BukkitTask runnable;

    public FeelSymptoms(Corona plugin) {
        super(plugin);
        this.plugin = plugin;
        this.infectionManager = plugin.getInfectionManager();
        this.settings = plugin.getSettings();
        this.messageSender = plugin.getMessageSender();
        this.r = new Random();
    }


    public void start() {
        int time = this.settings.getSymptomsIntervalTicks();
        List<String> efectos = this.settings.getPossibleSymptoms();

        this.runnable = new BukkitRunnable(){

            public void run() {
                Set<String> infected = FeelSymptoms.this.infectionManager.getInfectedPlayers();
                if(infected.isEmpty()) {
                    return;
                }
                for(String name : infected) {
                    Player player = Bukkit.getPlayer(name);
                    if(player == null) {
                        continue;
                    }
                    if(FeelSymptoms.this.settings.getDisabledWorlds().contains(player.getWorld().getName()) &&
                            FeelSymptoms.this.settings.isSymptomsDisabledInDisabledWorlds()) {
                        return;
                    }

                    String[] efecto = efectos.get(FeelSymptoms.this.r.nextInt(efectos.size()-1))
                            .split(",");
                    PotionEffect effect = new PotionEffect(Objects.requireNonNull(PotionEffectType.getByName(efecto[0])),
                            Integer.parseInt(efecto[1])*20,
                            Integer.parseInt(efecto[2])
                    );
                    player.addPotionEffect(effect);
                    if(FeelSymptoms.this.settings.isSymptomSoundEnabled()) {
                        SoundUtils.playSound(player, FeelSymptoms.this.settings.getSymptomsSound());
                    }
                    FeelSymptoms.this.messageSender.send(player, Message.FEELING_SYMPTOMS,
                            "%symptom%", efecto[0]);
                }


            }
        }.runTaskTimer(this.plugin, time, time);
    }



    public void cancel() {
        this.runnable.cancel();
    }

    @Override
    public void reload(boolean deep) {
        cancel();
        start();
    }
}
