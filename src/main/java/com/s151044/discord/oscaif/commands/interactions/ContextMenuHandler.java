package com.s151044.discord.oscaif.commands.interactions;

import com.s151044.discord.oscaif.Main;
import com.s151044.discord.oscaif.utils.EmbedHelper;
import com.s151044.discord.oscaif.utils.Messages;
import com.s151044.discord.oscaif.utils.ratelimit.LimitedExecutor;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ContextMenuHandler extends ListenerAdapter {
    private static final int MAX_LENGTH = 200;
    private Random rand = new Random();
    private String toOppose;
    private MessageContextInteractionEvent evt;
    private LimitedExecutor executor = new LimitedExecutor(5, TimeUnit.SECONDS, () -> {
        if(toOppose.length() > MAX_LENGTH && !Main.isOwner(evt)){
            evt.reply("Message too long!").queue();
            return;
        }
        evt.deferReply().queue();
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
        evt.getHook().sendMessageEmbeds(EmbedHelper.getLongEmbed(build.toString())).queue();
    });
    @Override
    public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event) {
        if(event.getName().equals("Opposite Direction")){
            long delayMs = executor.getDelay(TimeUnit.MILLISECONDS);
            if(delayMs > 0) {
                event.reply("The 5B bus went too fast! Please wait for " + Messages.toTime(delayMs)
                        + " before trying this again.").queue();
            } else {
                toOppose = event.getTarget().getContentRaw();
                evt = event;
                executor.queueExecution();
            }
        }
    }
}
