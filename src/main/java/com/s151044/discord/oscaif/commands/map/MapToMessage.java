package com.s151044.discord.oscaif.commands.map;

import com.s151044.discord.oscaif.Main;
import com.s151044.discord.oscaif.commands.Command;
import com.s151044.discord.oscaif.utils.Messages;
import com.s151044.discord.oscaif.utils.ratelimit.LimitedExecutor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MapToMessage implements Command {
    private Map<String, String> toUrl;
    private Message refMsg;
    private GuildMessageReceivedEvent evt;
    private String arguments;
    private LimitedExecutor executor = new LimitedExecutor(10, TimeUnit.MINUTES, () -> {
        Message refMsg = evt.getMessage().getReferencedMessage();
        String keyword = arguments;
        if(keyword.isEmpty()){
            Messages.sendMessage(evt, "Please enter a keyword.");
            return;
        }
        if(toUrl.containsKey(keyword)){
            Messages.sendMessage(evt, "Keyword " + keyword + " exists. Remove with the &remove command first.");
            return;
        }
        if(refMsg != null){
            String jumpUrl = refMsg.getJumpUrl();
            toUrl.put(keyword, jumpUrl);
            Messages.sendMessage(evt, "Successfully linked " + keyword + " with message.");
        } else {
            Messages.sendMessage(evt, "Please reference a message.");
        }
        Main.flushMessage();
    });

    public MapToMessage(Map<String, String> toUrl){
        this.toUrl = toUrl;
    }
    @Override
    public void action(GuildMessageReceivedEvent evt, String callName, String arguments) {
        long delayMs = executor.getDelay(TimeUnit.MILLISECONDS);
        if (delayMs > 0) {
            Messages.sendMessage(evt, "The mapping action is on delay. Time left: " + Messages.toTime(delayMs));
        } else {
            this.evt = evt;
            this.refMsg = evt.getMessage().getReferencedMessage();
            this.arguments = arguments;
            executor.queueExecution();
        }
    }

    @Override
    public List<String> alias() {
        return List.of("map");
    }

    @Override
    public String callName() {
        return "Map";
    }

    @Override
    public String shortHelp() {
        return "Maps an alias to a message.";
    }

    @Override
    public String longHelp() {
        return "Maps the supplied argument to the link of a message. \nInvoke by replying to a message with &map argument.";
    }
}
