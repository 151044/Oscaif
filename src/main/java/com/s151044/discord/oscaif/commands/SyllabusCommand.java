package com.s151044.discord.oscaif.commands;

import com.s151044.discord.oscaif.utils.Messages;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Arrays;
import java.util.List;
public class SyllabusCommand implements Command{
    private static final String BASE = "http://ugadmin.ust.hk/prog_crs/ug/202223/pdf/22-23";
    private static final String BASE_MINOR = "http://ugadmin.ust.hk/prog_crs/ug/202223/pdf/minor-";

    @Override
    public void action(GuildMessageReceivedEvent evt, String callName, String arguments) {
         String[] args = arguments.split(" ");
         if(args[0].equals("")) {
             Messages.sendMessage(evt, "Please enter the program shorthand.");
         } else if (args.length == 1) {
             if(args[0].length() > 6){
                 args[0] = "iim";
                 Messages.sendMessage(evt, "Entered argument too long; Assuming IIM.");
             }
             Messages.sendMessage(evt, BASE + args[0] + ".pdf");
         } else {
             if(args[1].length() > 6){
                 args[1] = "iim";
                 Messages.sendMessage(evt, "Entered argument too long; Assuming IIM.");
             }
             args[1] = args[1].toLowerCase();
             if(args[0].equals("major") || args[0].equals("maj")){
                 Messages.sendMessage(evt, BASE + args[1] + ".pdf");
             } else if(args[0].equals("minor") || args[0].equals("min")){
                 Messages.sendMessage(evt, BASE_MINOR + args[1] + ".pdf");
             } else if(args[0].equals("extmajor") || args[0].equals("extm")){
                Messages.sendMessage(evt, BASE + "extm-" + args[1] + ".pdf");
             } else if(args[0].equals("schoolreq") || args[0].equals("req")){
                if(args[1].equals("ssci") || args[1].equals("sbm")) {
                    Messages.sendMessage(evt, BASE + args[1] + "_requirements.pdf");
                } else {
                    Messages.sendMessage(evt, "No such school requirement!");
                }
             } else {
                 Messages.sendMessage(evt, "Unknown keyword. Valid keywords are any of " +
                         "[major/maj/minor/min/extmajor/extm/schoolreq/req].");
             }
         }
    }

    @Override
    public List<String> alias() {
        return List.of("syl", "syllabus");
    }

    @Override
    public String callName() {
        return "Syllabus";
    }

    @Override
    public String shortHelp() {
        return "Provides a link to the syllabus of a selected major/minor/extended major.";
    }

    @Override
    public String longHelp() {
        return "Provides a link to the syllabus of a selected major/minor/extended major.\n" +
                "Format: &Syllabus [keyword] program\n" +
                "keyword can be any of: [major/maj/minor/min/extmajor/extm/schoolreq/req]" +
                "The keyword is optional, and the command defaults to showing a major program.";
    }
}
