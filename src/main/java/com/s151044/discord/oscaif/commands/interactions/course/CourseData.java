package com.s151044.discord.oscaif.commands.interactions.course;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class CourseData {
    private static final int SUGGEST_LIMIT = 25;
    private List<Course> courses = new ArrayList<>();
    // For faster suggestions
    private Map<String, List<String>> depts = new HashMap<>();
    private Gson gson;
    public CourseData(Gson gson, Path courseData) throws IOException {
        JsonObject root = gson.fromJson(Files.newBufferedReader(courseData), JsonElement.class).getAsJsonObject();
        root.entrySet().forEach(entry -> {
            Course course = new Course(entry.getValue().getAsJsonObject());
            courses.add(course);
            depts.computeIfAbsent(course.getDept(), (ignored) -> new ArrayList<>()).add(course.getCode());
        });
    }

    public CourseData(Gson gson, URL url) {

    }

    public List<String> suggestDepts(String initial) {
        return depts.keySet().stream()
                .filter(s -> s.startsWith(initial))
                .limit(SUGGEST_LIMIT)
                .collect(Collectors.toList());
    }
    public List<String> suggestCode(String dept, String initial) {
        if(depts.containsKey(dept)) {
            return depts.get(dept).stream()
                    .filter(s -> s.startsWith(initial))
                    .limit(SUGGEST_LIMIT)
                    .collect(Collectors.toList());
        } else {
            return List.of();
        }
    }
    public Optional<Course> getCourse(String dept, String code) {
        return courses.stream()
                .filter(course -> course.getDept().equals(dept))
                .filter(course -> course.getCode().equals(code))
                .findFirst();
    }
    public List<Course> getByCcArea(String area, boolean sscOnly) {
        return courses.stream()
                .filter(course -> course.getCcType().getCategories().contains(area))
                .filter(course -> sscOnly ? course.isSsc() : true)
                .collect(Collectors.toList());
    }
}
