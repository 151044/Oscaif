package com.s151044.discord.oscaif.commands;

import com.s151044.discord.oscaif.Main;
import com.s151044.discord.oscaif.utils.EmbedHelper;
import com.s151044.discord.oscaif.utils.Messages;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;
import java.util.Random;

public class OppositeDirection implements Command{
    private static final int MAX_LENGTH = 200;
    private Random rand = new Random();
    @Override
    public void action(GuildMessageReceivedEvent evt, String callName, String arguments) {
        String toOppose = arguments;
        if(toOppose.length() > MAX_LENGTH && !Main.isOwner(evt)){
            Messages.sendMessage(evt, "Message too long!");
            return;
        }
        int numRepeats = (int) Math.max(2, Math.floor(rand.nextGaussian() * 2.5 + 7.5));
        while(numRepeats * (toOppose.length() * 2) > 8000){
            numRepeats--;
        }
        StringBuilder build = new StringBuilder();
        for(int i  = 0; i < numRepeats; i++){
            build.append(toOppose);
            build.append(" > ");
            build.append(toOppose);
            build.append(" (Opposite direction)");
            build.append(" > ");
        }
        EmbedHelper.getLongEmbed(build.toString()).forEach(emb -> Messages.sendMessage(evt, emb));
    }

    @Override
    public List<String> alias() {
        return List.of("opdir", "opsdir", "oppositedirection");
    }

    @Override
    public String callName() {
        return "OppositeDirection";
    }

    @Override
    public String shortHelp() {
        return "Help (Opposite Direction)";
    }

    @Override
    public String longHelp() {
        return "Pads and repeats a message in the opposite direction a random number of times.";
    }
}
