package com.s151044.discord.oscaif;

import com.s151044.discord.oscaif.commands.*;
import com.s151044.discord.oscaif.commands.interactions.MTREta;
import com.s151044.discord.oscaif.commands.interactions.SlashCommandList;
import com.s151044.discord.oscaif.handlers.interactions.ContextMenuHandler;
import com.s151044.discord.oscaif.commands.interactions.MTRData;
import com.s151044.discord.oscaif.commands.map.ListMap;
import com.s151044.discord.oscaif.commands.map.MapToMessage;
import com.s151044.discord.oscaif.commands.map.RecallCommand;
import com.s151044.discord.oscaif.commands.map.ReplaceMap;
import com.s151044.discord.oscaif.handlers.MessageHandler;
import com.s151044.discord.oscaif.handlers.interactions.SlashHandler;
import com.s151044.discord.oscaif.utils.Messages;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static JDA jda;
    private static Map<String,String> toUrl;

    private static Message tags;

    public static void main(String[] args) throws LoginException, InterruptedException, IOException {

        CommandList cmd = new CommandList();
        SlashCommandList slashList = new SlashCommandList();

        jda = JDABuilder.createDefault(System.getenv("BOT_TOKEN"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(
                        new MessageHandler("&", cmd),
                        new ContextMenuHandler(),
                        new SlashHandler(slashList))
                .build();
        jda.awaitReady();

        toUrl = new HashMap<>();
        //Hardcoding
        TextChannel channel = jda.getGuildById("758326787129737226").getTextChannelById("982147954351685632");
        tags = channel.retrieveMessageById(channel.getLatestMessageId()).complete();
        for(String s : tags.getContentRaw().split("\n")){
            String[] entry = s.split("\t");
            if(s.isEmpty() || entry.length < 2){
                continue;
            }
            toUrl.put(entry[0].trim(), entry[1].trim());
        }
        if(!tags.getAuthor().isBot()){
            StringBuilder sb = new StringBuilder();
            toUrl.forEach((key, value) -> sb.append(key).append("\t").append(value).append("\n"));
            Messages.sendMessage(tags, sb.toString());
            tags = channel.retrieveMessageById(channel.getLatestMessageId()).complete();
        }

        MTRData data = new MTRData();

        cmd.addCommand(new OppositeDirection());
        cmd.addCommand(new Rickroll());
        cmd.addCommand(new EchoCommand());
        cmd.addCommand(new MapToMessage(toUrl));
        cmd.addCommand(new RecallCommand(toUrl));
        cmd.addCommand(new ReplaceMap(toUrl));
        cmd.addCommand(new ListMap(toUrl));
        cmd.addCommand(new GoopRetriever());
        cmd.addCommand(new Shutdown());
        cmd.addCommand(new ReviewCommand());
        cmd.addCommand(new SyllabusCommand());
        cmd.addCommand(new SetupInteractions(slashList));

        slashList.addCommand(new MTREta(data));
    }
    public static void shutdown(){
        flushMessage();
        System.exit(0);
    }
    public static void flushMessage(){
        StringBuilder sb = new StringBuilder();
        toUrl.forEach((key, value) -> sb.append(key).append("\t").append(value).append("\n"));
        tags.editMessage(sb.toString()).complete();
    }
    public static boolean isOwner(MessageReceivedEvent evt){
        return evt.getAuthor().getId().equals(System.getenv("OWNER_ID"));
    }
    public static boolean isOwner(MessageContextInteractionEvent evt){
        Member member = evt.getMember();
        return member != null && member.getId().equals(System.getenv("OWNER_ID"));
    }
}
