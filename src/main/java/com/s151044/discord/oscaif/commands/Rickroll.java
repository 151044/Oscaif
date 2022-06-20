package com.s151044.discord.oscaif.commands;

import com.s151044.discord.oscaif.utils.Messages;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;
import java.util.Random;

public class Rickroll implements Command {
    private List<String> msg = List.of("https://www.youtube.com/watch?v=dQw4w9WgXcQ", "https://imgur.com/NQinKJB");
    private Random rand = new Random();
    @Override
    public void action(GuildMessageReceivedEvent evt, String callName, String arguments) {
        Messages.sendMessage(evt, msg.get(rand.nextInt(msg.size())));
    }

    @Override
    public List<String> alias() {
        return List.of();
    }

    @Override
    public String callName() {
        return "rr";
    }

    @Override
    public String shortHelp() {
        return "A surprise, but a welcome one.";
    }

    @Override
    public String longHelp() {
        return "No spoilers!";
    }
}
