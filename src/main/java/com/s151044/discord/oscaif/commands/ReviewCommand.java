package com.s151044.discord.oscaif.commands;

import com.s151044.discord.oscaif.utils.Messages;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class ReviewCommand implements Command{
    @Override
    public void action(GuildMessageReceivedEvent evt, String callName, String arguments) {
        if(arguments.isEmpty()){
            Messages.sendMessage(evt, "Please specify a course name.");
        }
        Messages.sendMessage(evt, "https://ust.space/review/" + arguments.replace(" ",""));
    }

    @Override
    public List<String> alias() {
        return List.of("ustspace","space");
    }

    @Override
    public String callName() {
        return "review";
    }

    @Override
    public String shortHelp() {
        return "Link to UST Space.";
    }

    @Override
    public String longHelp() {
        return "Returns a UST space link to the course specified.";
    }
}
