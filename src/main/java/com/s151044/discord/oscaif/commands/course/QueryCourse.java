package com.s151044.discord.oscaif.commands.course;

import com.s151044.discord.oscaif.commands.Command;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class QueryCourse implements Command {
    @Override
    public void action(GuildMessageReceivedEvent evt, String callName, String arguments) {

    }

    @Override
    public List<String> alias() {
        return List.of("query-course", "cquery", "clist", "coursequery");
    }

    @Override
    public String callName() {
        return "CourseQuery";
    }

    @Override
    public String shortHelp() {
        return "Looks up information related to an HKUST course.";
    }

    @Override
    public String longHelp() {
        return "Looks up information related to an HKUST course, such as CC status, prerequisites, and corequisites.";
    }
    private String readExprTree(){
        return null;
    }
}
