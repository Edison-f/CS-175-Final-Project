package edu.sjsu.android.cs175finalproject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class Course {

    enum GradingType {
        PERCENT, // Groups have different weights
        POINTS, // Assignments are given unweighted points (Similar to CS-175 grade scale)
    }

    // TODO: Probably want to retrieve data from files / database

    private double grade;
    private String name;
    private HashMap<String, ArrayList<Assignment>> assignments; // Group, Arraylist of assignments within group
    private ArrayList<String> groups; // Want a group since HashMap not necessarily sorted

    public Course() {
        grade = 100;
        name = "Placeholder";
        assignments = new HashMap<>();
        groups = new ArrayList<>();
        groups.add("Default");
        assignments.put("Default", new ArrayList<>());
    }

    public String getName() {
        return name;
    }

    public String getNumericalGrade() {
        DecimalFormat df = new DecimalFormat("0.00")
        return df.format(grade);
    }

    public String getLetterGrade() {
        // TODO
        return "TODO";
    }

    public void recalculate() {
        // TODO
    }

    public void addGroup(String group) {
        groups.add(group);
        // TODO: Maybe sort the list
    }

    public class Assignment {
        public double grade;
        public String group;
    }
}
