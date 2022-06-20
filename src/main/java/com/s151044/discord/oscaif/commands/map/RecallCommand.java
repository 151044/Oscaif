package com.s151044.discord.oscaif.commands.map;

import com.s151044.discord.oscaif.Main;
import com.s151044.discord.oscaif.commands.Command;
import com.s151044.discord.oscaif.utils.Messages;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;
import java.util.Map;

public class RecallCommand implements Command {
    private Map<String, String> toUrl;

    public RecallCommand(Map<String, String> toUrl){
        this.toUrl = toUrl;
    }
    @Override
    public void action(GuildMessageReceivedEvent evt, String callName, String arguments) {
        String keyword = evt.getMessage().getContentRaw().substring(callName.length() + 2);
        if(keyword.isEmpty()){
            Messages.sendMessage(evt, "Please enter a keyword.");
            return;
        }
        if(toUrl.containsKey(keyword)){
            Messages.sendMessage(evt, toUrl.get(keyword));
        } else {
            Messages.sendMessage(evt, "Cannot find " + keyword + "!");
        }
        Main.flushMessage();
    }

    @Override
    public List<String> alias() {
        return List.of("recall", "call");
    }

    @Override
    public String callName() {
        return "Recall";
    }

    @Override
    public String shortHelp() {
        return "Recalls a message saved by &map.";
    }

    @Override
    public String longHelp() {
        return "Sends the link of a message saved by &map. Returns an error message if the argument has not been saved by &map.";
    }
}
