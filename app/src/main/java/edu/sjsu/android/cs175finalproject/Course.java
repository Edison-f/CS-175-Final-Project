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
    private HashMap<String, Double> groupWeights;
    private int[] thresholds; // Grade thresholds, (inclusive)
    private boolean roundUp; // Is the professor expected to round up to nearest percent
    private static String[] gradeLetters = new String[]{"A+", "A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D+", "D", "D-", "F+", "F", "F-"};

    public Course() {
        grade = 100;
        name = "Placeholder";
        assignments = new HashMap<>();
        groups = new ArrayList<>();
        groupWeights = new HashMap<>();
        groups.add("Default");
        groupWeights.put("Default", 1.0);
        assignments.put("Default", new ArrayList<>());
                            // To make a grade unachievable set to value -1
                            // If no threshold met, give F
                            //  A+, A,  A-, B+, B,  B-, C+, C,  C-, D+,  D, D-, F+, F, F-
        thresholds = new int[] {98, 92, 90, 88, 82, 80, 78, 72, 70, 68, 62, 60, -1, -1, -1};
    }

    public String getName() {
        return name;
    }

    public String getNumericalGrade() {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(grade);
    }

    public String getLetterGrade() {
        for (int i = 0; i < thresholds.length; i++) {
            if(grade > thresholds[i] && thresholds[i] != -1) {
                return gradeLetters[i];
            }
        }
        return "F";
    }

    public void recalculate() {
        grade = 0;
        for (String group :
                groups) {
            ArrayList<Assignment> arr = assignments.get(group);
            if(arr == null) {
                continue;
            }
            double total = 0;
            for (Assignment a :
                    arr) {
                total += a.pointsPossible;
            }
            for (Assignment a :
                    arr) {

                Double weight = groupWeights.get(group);
                if(weight == null) {
                    weight = 1.0;
                }
                grade += ((a.grade / a.pointsPossible) / total) * weight;
            }
        }
    }

    public void addGroup(String group) {
        groups.add(group);
        // TODO: Maybe sort the list
    }

    public class Assignment {
        public double grade;
        public double pointsPossible;
        public String group;
    }
}
