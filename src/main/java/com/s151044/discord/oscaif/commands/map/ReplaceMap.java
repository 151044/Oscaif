package com.s151044.discord.oscaif.commands.map;

import com.s151044.discord.oscaif.Main;
import com.s151044.discord.oscaif.commands.Command;
import com.s151044.discord.oscaif.utils.Messages;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;
import java.util.Map;

public class ReplaceMap implements Command {
    private Map<String, String> toUrl;
    public ReplaceMap(Map<String, String> toUrl){
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
            toUrl.remove(keyword);
            Messages.sendMessage(evt, "Successfully removed " + keyword + " mapping.");
        } else {
            Messages.sendMessage(evt, "Cannot find " + keyword + "!");
        }
        Main.flushMessage();
    }

    @Override
    public List<String> alias() {
        return List.of("rm", "remove");
    }

    @Override
    public String callName() {
        return "Remove";
    }

    @Override
    public String shortHelp() {
        return "Removes a saved keyword created by &map.";
    }

    @Override
    public String longHelp() {
        return "Removes a saved keyword created by &map.\nReturns an error message if the keyword is not found.";
    }
}
