package com.s151044.discord.oscaif.commands;

import com.s151044.discord.oscaif.Main;
import com.s151044.discord.oscaif.utils.EmbedHelper;
import com.s151044.discord.oscaif.utils.Messages;
import com.s151044.discord.oscaif.utils.ratelimit.LimitedExecutor;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class OppositeDirection implements Command{
    private static final int MAX_LENGTH = 200;
    private Random rand = new Random();
    private String toOppose;
    private MessageReceivedEvent evt;
    private LimitedExecutor executor = new LimitedExecutor(5, TimeUnit.SECONDS, () -> {
        if(toOppose.length() > MAX_LENGTH && !Main.isOwner(evt)){
            Messages.send(evt, "Message too long!");
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
        String toEmbed = build.toString();
        if(toEmbed.trim().startsWith(">")){
            toEmbed = "\\" + toEmbed.trim();
        }
        EmbedHelper.getLongEmbed(toEmbed).forEach(emb -> Messages.send(evt, emb));
    });
    @Override
    public void action(MessageReceivedEvent evt, String callName, String arguments) {
        long delayMs = executor.getDelay(TimeUnit.MILLISECONDS);
        if(delayMs > 0) {
            Messages.send(evt, "The 5B bus went too fast! Please wait for " + Messages.toTime(delayMs)
                    + " before trying this again.");
        } else {
            toOppose = arguments;
            this.evt = evt;
            executor.queueExecution();
        }
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
