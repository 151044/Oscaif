package com.s151044.discord.oscaif.commands;

import com.s151044.discord.oscaif.Main;
import com.s151044.discord.oscaif.utils.Messages;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class Shutdown implements Command {
    @Override
    public void action(MessageReceivedEvent evt, String callName, String arguments) {
        if(Main.isOwner(evt)){
            Main.shutdown();
        } else {
            Messages.send(evt, "You're not authorized to use this command.");
        }
    }

    @Override
    public List<String> alias() {
        return List.of("shutdown");
    }

    @Override
    public String callName() {
        return "Shutdown";
    }

    @Override
    public String shortHelp() {
        return "Shuts down the bot.";
    }

    @Override
    public String longHelp() {
        return "Shuts down the bot, cleanly.";
    }

    @Override
    public boolean hidden() {
        return true;
    }
}
