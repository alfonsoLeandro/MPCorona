package com.github.alfonsoleandro.corona.commands;

import com.github.alfonsoleandro.corona.commands.commandhandlers.*;
import com.github.alfonsoleandro.corona.Corona;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class MainCommand implements CommandExecutor {

    private final AbstractHandler cor;

    public MainCommand(Corona plugin){
        this.cor = new HelpHandler(plugin,
                new VersionHandler(plugin,
                        new ReloadHandler(plugin,
                                new InfectHandler(plugin,
                                        new CureHandler(plugin,
                                                new GiveMaskHandler(plugin,
                                                        new GivePotionHandler(plugin,
                                                                new CheckHandler(plugin, null)
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
    }




    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        this.cor.handle(sender, label, args);
        return true;
    }





}
