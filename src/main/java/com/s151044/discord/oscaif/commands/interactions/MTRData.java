package com.s151044.discord.oscaif.commands.interactions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class MTRData {
    private Map<String, String> fullName = new HashMap<>();
    private Map<String, Map<String, String>> lines = new HashMap<>();
    public MTRData() throws IOException {
        Path p = Path.of("mtr/Lines.txt");
        for(String s : Files.readAllLines(p)){
            if(!s.isEmpty()) {
                int space = s.indexOf(' ');
                fullName.put(s.substring(0, space), s.substring(space + 1));
                Map<String, String> stations = new HashMap<>();
                for(String sta : Files.readAllLines(Path.of("mtr/" + s.substring(space + 1)))) {
                    int stationSpace = s.indexOf(' ');
                    stations.put(sta.substring(0, stationSpace), sta.substring(stationSpace + 1));
                }
                lines.put(s.substring(0, space), stations);
            }
        }
    }
    public Set<String> getFullStations(){
        return lines.values().stream()
                .map(Map::values).flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }
    public Set<String> getAbbrs(){
        return lines.values().stream()
                .map(Map::keySet).flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }
    public Optional<String> fullFromAbbr(String abbr){
        return lines.values().stream()
                .map(Map::entrySet).flatMap(Collection::stream)
                .filter(entry -> entry.getKey().equals(abbr))
                .map(Map.Entry::getKey)
                .findFirst();
    }
    public Set<String> getSuggestions(String prefix){
        return lines.values().stream()
                .map(Map::values).flatMap(Collection::stream)
                .filter(str -> str.startsWith(prefix))
                .collect(Collectors.toSet());
    }
    public Set<String> getLines(){
        return Set.copyOf(fullName.values());
    }
    public Optional<String> getLineFromAbbr(String abbr){
        return fullName.entrySet().stream()
                .filter(ent -> ent.getKey().equals(abbr))
                .map(Map.Entry::getValue)
                .findFirst();
    }
    public Set<String> getStationLines(String stationAbbr){
        return lines.entrySet().stream()
                .filter(ent -> ent.getValue().containsKey(stationAbbr))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }
}
