package com.s151044.discord.oscaif.commands.interactions;

import java.util.*;

/**
 * A list of commands for a specific prefix.
 */
public class SlashCommandList {
    private Map<String, SlashCommand> commandMap;
    private Map<List<String>,String> aliasMap;
    public SlashCommandList() {
        commandMap = new HashMap<>();
        aliasMap = new HashMap<>();
    }

    /**
     * Tries to get a command if it exists by its alias or name.
     * @param byName The name of the command, or its alias, to search for
     * @return An optional containing the requested command, or an empty optional if it cannot be found
     */
    public Optional<SlashCommand> tryGet(String byName){
        if(commandMap.containsKey(byName)){
            return Optional.of(commandMap.get(byName));
        }
        if(aliasMap.keySet().stream().flatMap(Collection::stream).anyMatch(str -> str.equals(byName))){
            return Optional.of(commandMap.get(aliasMap.entrySet().stream().filter(ent -> ent.getKey().contains(byName)).findFirst().map(ent -> ent.getValue()).get()));
        }else{
            return Optional.empty();
        }
    }

    /**
     * Adds a command to this command list.
     * @param toAdd The command to add
     */
    public void addCommand(SlashCommand toAdd){
        commandMap.put(toAdd.callName(), toAdd);
        aliasMap.put(toAdd.alias(), toAdd.callName());
    }
    public List<SlashCommand> getCommands(){
        return List.copyOf(commandMap.values());
    }

    public int getSize(){
        return commandMap.size();
    }
}
