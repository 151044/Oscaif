package com.s151044.discord.oscaif.commands;

import com.s151044.discord.oscaif.utils.Messages;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class ReviewCommand implements Command{
    private static final int MAX_LENGTH = 20;
    @Override
    public void action(MessageReceivedEvent evt, String callName, String arguments) {
        if(arguments.isEmpty()){
            Messages.send(evt, "Please specify a course name.");
            return;
        }
        if(arguments.length() > MAX_LENGTH){
            Messages.send(evt, "Course name is too long.");
            return;
        }
        Messages.send(evt, "https://ust.space/review/" + arguments.replace(" ","")
                .replace("\n", ""));
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
