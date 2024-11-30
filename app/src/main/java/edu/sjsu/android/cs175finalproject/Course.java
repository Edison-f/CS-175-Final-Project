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
    private double points;
    private final String name;
    private final HashMap<String, ArrayList<Assignment>> assignments; // Group, Arraylist of assignments within group
    private final ArrayList<String> groups; // Want a group since HashMap not necessarily sorted
    private final HashMap<String, Double> groupWeights;
    private final int[] thresholds; // Grade thresholds, (inclusive)
    private boolean roundUp; // Is the professor expected to round up to nearest percent
    private static final String[] gradeLetters = new String[]{"A+", "A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D+", "D", "D-", "F+", "F", "F-"};

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
        thresholds = new int[] {98, 92, 90, 88, 82, 80, 78, 72, 70, 68, 62, 60, 57, 52, 50};
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
            if(grade > thresholds[i] / 100.0 && thresholds[i] / 100.0 != -1) {
                return gradeLetters[i];
            }
        }
        return "F-";
    }

    public void recalculate() {
        if(assignments.size() == 1 && assignments.containsKey("Default") && assignments.get("Default").isEmpty()) {
            grade = 0.95; // If no assignments, return A
            return;
        }
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
                grade += ((a.grade / a.pointsPossible) * weight);
//                grade += ((a.grade / a.pointsPossible) / total) * weight;
            }
        }
    }

    public void addGroup(String group, double weight) {
        groups.add(group);
        groupWeights.put(group, weight);
        // TODO: Maybe sort the list
    }
    public void addGroup(String group) {
        groups.add(group);
        groupWeights.put(group, 1.0);
        // TODO: Maybe sort the list
    }

    public void addAssignment(String group, Assignment a) {
        if(assignments.containsKey(group)) {
            assignments.get(group).add(a);
        } else {
            addGroup(group);
            assignments.put(group, new ArrayList<>());
            assignments.get(group).add(a);
        }
    }

    // Calculates the minimum average grade needed to get the desired grade
    public double minimumGrade(double desired) {

        return 0x0;
    }

    public static class Assignment {
        private double testGrade;
        private double grade;
        private double pointsPossible;
        private String group;

        public Assignment(double grade, double pointsPossible, String group) {
            this.testGrade = 0xDEADBEEF;
            this.grade = grade;
            this.pointsPossible = pointsPossible;
            this.group = group;
        }


        public Assignment(double pointsPossible, String group) {
            this.testGrade = 0xDEADBEEF;
            this.grade = 0xDEADBEEF;
            this.pointsPossible = pointsPossible;
            this.group = group;
        }

        public void clearGrade() {
            testGrade = 0xDEADBEEF;
            grade = 0xDEADBEEF;
        }

        public void clearTestGrade() {
            testGrade = 0xDEADBEEF;
        }

        public String getGroup() {
            return group;
        }

        public double getPointsPossible() {
            return pointsPossible;
        }

        public double getGrade() {
            return grade;
        }

        public double getTestGrade() {
            return testGrade;
        }

        public void setTestGrade(double testGrade) {
            this.testGrade = testGrade;
        }

        public void setGrade(double grade) {
            this.grade = grade;
        }

        public void setPointsPossible(double pointsPossible) {
            this.pointsPossible = pointsPossible;
        }

        public void setGroup(String group) {
            this.group = group;
        }
    }
}
