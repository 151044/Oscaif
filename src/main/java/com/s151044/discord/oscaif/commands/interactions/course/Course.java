package com.s151044.discord.oscaif.commands.interactions.course;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Course {
    private String dept = "";
    private String code = "";
    private boolean isFull = false;
    private List<String> attrs = new ArrayList<>();
    private String desc = "";
    private String title = "";
    private int credits = -1;
    private List<CCType> ccType = new ArrayList<>();

    public Course(String dept, String code) {
        this.dept = dept;
        this.code = code;
    }

    public Course(JsonObject object){
        isFull = true;
        JsonArray attributes = object.getAsJsonArray("attrs");
        if(attributes != null) {
            attributes.forEach(element -> attrs.add(element.getAsString()));
        }
        dept = object.get("dept").getAsString();
        code = object.get("code").getAsString();
        title = object.get("title").getAsString();
        credits = object.get("credits").getAsInt();
        desc = object.get("description").getAsString();
        if(object.has("ccType")) {
            ccType = List.of(
                    new CCType(36,
                            object.get("ccType").getAsString(),
                            object.get("isSsc").getAsBoolean()));
        }
    }

    public String getDept() {
        return dept;
    }

    public String getCode() {
        return code;
    }

    public boolean isFull() {
        return isFull;
    }

    public List<String> getAttrs() {
        return attrs;
    }

    public String getDesc() {
        return desc;
    }

    public String getTitle() {
        return title;
    }

    public int getCredits() {
        return credits;
    }

    public boolean isSsc() {
        return ccType.stream().anyMatch(c -> c.ccCredits == 36 && c.isSsc);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(dept, course.dept) && Objects.equals(code, course.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dept, code);
    }

    public List<CCType> getCcTypes() {
        return ccType;
    }

    public static class CCType {
        private int ccCredits = -1;
        private String category = "";
        private boolean isSsc = false;

        public CCType(int credits, String category, boolean isSsc) {
            ccCredits = credits;
            this.category = category;
            this.isSsc = isSsc;
        }

        public int getCredits() {
            return ccCredits;
        }

        public String getCategory() {
            return category;
        }

        @Override
        public String toString() {
            return ccCredits + "-credit Common Core in " + String.join(", ", category);
        }
    }
}
