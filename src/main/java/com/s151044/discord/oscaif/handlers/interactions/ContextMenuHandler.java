package com.s151044.discord.oscaif.handlers.interactions;

import com.s151044.discord.oscaif.commands.interactions.ContextDirection;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ContextMenuHandler extends ListenerAdapter {
    private ContextDirection direction = new ContextDirection();
    @Override
    public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event) {
        if(event.getName().equals("Opposite Direction")){
            direction.handleMessage(event);
        }
    }
}
