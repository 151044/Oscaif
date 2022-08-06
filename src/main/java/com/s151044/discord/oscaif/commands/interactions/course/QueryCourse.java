package com.s151044.discord.oscaif.commands.interactions.course;

import com.google.gson.Gson;
import com.s151044.discord.oscaif.commands.interactions.SlashCommand;
import com.s151044.discord.oscaif.utils.EmbedHelper;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class QueryCourse implements SlashCommand {

    private CourseData data;

    public QueryCourse(CourseData courseData){
        data = courseData;
    }


    @Override
    public void action(SlashCommandInteractionEvent evt) {
        evt.deferReply().queue();
        InteractionHook hook = evt.getHook();
        String dept = evt.getOption("dept").getAsString();
        String code = evt.getOption("code").getAsString();
        Optional<Course> optCourse = data.getCourse(dept, code);
        if(optCourse.isEmpty()) {
            hook.sendMessage("Cannot find this course!").queue();
            return;
        }
        Course course = optCourse.get();
        String attributes = String.join("\n", course.getAttrs());
        EmbedHelper.getLongEmbed(
                String.format("**Description:** %s\n**Attributes:** %s\n**Common Core Area:** %s\n**SSC:** %s",
                course.getDesc(),
                attributes.isEmpty() ? "None" : attributes,
                course.getCcType().isEmpty() ? "None" : course.getCcType(),
                course.isSsc() ? "Yes" : "No"),
                String.format("%s %s - %s (%d credit(s))",
                        course.getDept(),
                        course.getCode(),
                        course.getTitle(),
                        course.getCredits()))
                .forEach(embed -> hook.sendMessageEmbeds(embed).queue());
    }

    @Override
    public String callName() {
        return "course-info";
    }

    @Override
    public SlashCommandData commandInfo() {
        return Commands.slash("course-info", "Fetches information about a course.")
                .addOption(OptionType.STRING, "dept", "The department to search for.", true, true)
                .addOption(OptionType.STRING, "code", "The course code ton search for.", true, true);
    }

    @Override
    public void handleAutocomplete(CommandAutoCompleteInteractionEvent evt) {
        String option = evt.getFocusedOption().getName();
        String prefix = evt.getFocusedOption().getValue();
        switch(option) {
            case "dept" -> evt.replyChoiceStrings(data.suggestDepts(prefix)).queue();
            case "code" -> {
                OptionMapping optionData = evt.getOption("dept");
                if(optionData == null) {
                    evt.replyChoiceStrings(List.of()).queue();
                    return;
                }
                String dept = optionData.getAsString();
                evt.replyChoiceStrings(data.suggestCode(dept, evt.getFocusedOption().getValue())).queue();
            }
        }
    }
}
