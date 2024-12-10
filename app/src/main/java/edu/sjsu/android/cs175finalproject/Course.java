package edu.sjsu.android.cs175finalproject;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class Course implements Serializable {

    enum GradingType { // TODO
        PERCENT, // Groups have different weights
        POINTS, // Assignments are given unweighted points (Similar to CS-175 grade scale)
    }

    // TODO: Probably want to retrieve data from files / database

    private double grade; // The grade in the course
    private String name; // The name of the course
    private final HashMap<String, ArrayList<Assignment>> assignments; //A HashMap of groups as keys and an array of assignments as the value // Group, Arraylist of assignments within group
    private final ArrayList<String> groups; // An array of groups (probably not needed) // Want a group since HashMap not necessarily sorted
    final HashMap<String, Double> groupWeights; // The weights corresponding to each group
    private final int[] thresholds; // Grade thresholds, (inclusive)
    private boolean roundUp; // Is the professor expected to round up to nearest percent (currently unused) // TODO
    private static final String[] gradeLetters = new String[]{"A+", "A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D+", "D", "D-", "F+", "F", "F-"}; // Grade letters that should correspond to thresholds


    /**
     * Creates a new course
     */
    public Course() {
        grade = 1.0;
        name = "Placeholder";
        assignments = new HashMap<>();
        groups = new ArrayList<>();
        groupWeights = new HashMap<>();
        //groups.add("Default");
//        groupWeights.put("Default", 1.0);
        //assignments.put("Default", new ArrayList<>());
        // To make a grade unachievable set to value -1
        // If no threshold met, give F
        //  A+, A,  A-, B+, B,  B-, C+, C,  C-, D+,  D, D-, F+, F, F-
        thresholds = new int[]{98, 92, 90, 88, 82, 80, 78, 72, 70, 68, 62, 60, 57, 52, 50};
    }

    /**
     * Creates a named course
     * @param name The name of the course
     */
    public Course(String name) {
        grade = 1.0;
        this.name = name;
        assignments = new HashMap<>();
        groups = new ArrayList<>();
        groupWeights = new HashMap<>();
        //groups.add("Default");
//        groupWeights.put("Default", 1.0);
        //assignments.put("Default", new ArrayList<>());
        // To make a grade unachievable set to value -1
        // If no threshold met, give F
        //  A+, A,  A-, B+, B,  B-, C+, C,  C-, D+,  D, D-, F+, F, F-
        thresholds = new int[]{98, 92, 90, 88, 82, 80, 78, 72, 70, 68, 62, 60, 57, 52, 50};
    }

    /**
     * Formats the numerical grade into a decimal % value to 2 decimal places
     * @return The formatted grade
     */
    public String getNumericalGrade() {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(grade * 100);
    }

    /**
     * Uses the thresholds to return the grade corresponding to the current grade
     * @return The letter grade
     */
    public String getLetterGrade() {
        for (int i = 0; i < thresholds.length; i++) {
            if (grade >= thresholds[i] / 100.0 && thresholds[i] / 100.0 != -1) {
                return gradeLetters[i];
            }
        }
        return "F-";
    }

    /**
     *   This is to remove the assignment from the UI
     */
    public void removeAssignment(String group, String assignmentName) {
        if (assignments.containsKey(group)) {
            ArrayList<Assignment> groupAssignments = assignments.get(group);
            groupAssignments.removeIf(assignment -> assignment.getName().equals(assignmentName));

            // Remove the group if empty (optional)
            if (groupAssignments.isEmpty()) {
                assignments.remove(group);
            }
        }
    }

    /**
     * Recalculates the grade using all assignments with a grade value set
     * Call after adding or removing an assignment(s) or group(s)
     * Default grade is an A
     */
    public void recalculate() {
        if (groupWeights.isEmpty() && assignments.containsKey("Default") && assignments.get("Default").isEmpty()) {
            grade = 0.95; // If no assignments, return A
            return;
        }
        grade = 0;
        double totalWeight = 0;
        boolean valid = false;
        for (String group :
                groupWeights.keySet()) {
            double total = 0;
            boolean once = true;
            ArrayList<Assignment> arr = assignments.get(group);
            if (arr == null) {
                continue;
            }
            Double weight = groupWeights.get(group);
            if (weight == null) {
                weight = 1.0;
            }
            for (Assignment a :
                    arr) {
                if (a.grade != a.testGrade) {
                    total += a.pointsPossible;
                }
            }
            for (Assignment a :
                    arr) {
                if (a.grade != a.testGrade) {
                    valid = true;
                    grade += ((a.grade / a.pointsPossible) / (total / a.pointsPossible)) * weight;
                    if(once) {
                        once = false;
                        totalWeight += weight;
                    }
                }
            }
        }
        grade /= totalWeight;
        if(!valid) {
            grade = 0.95;
        }
    }

    /**
     * Adds a new group
     * @param group The group name
     * @param weight The arbitrary weight value
     */
    public void addGroup(String group, double weight) {
        groups.add(group);
        groupWeights.put(group, weight);
        // TODO: Maybe sort the list
    }

    /**
     * Adds a new group with a default weight of 1.0
     * @param group The group name
     */
    public void addGroup(String group) {
        groups.add(group);
        groupWeights.put(group, 1.0);
        // TODO: Maybe sort the list
    }

    /**
     * Sets the group's weight to the specified value
     * A 50-50 weight could be 1.0 and 1.0, 20-80 could be 5 and 20, a 1/3 1/3 1/3 split would have all equal values, etc.
     * Weights are arbitrary and need to scale properly with each other, keep units consistent (use percents that add up to 1)
     * @param group The group name
     * @param weight The arbitrary group weight
     */
    public void setGroupWeight(String group, double weight) {
        groupWeights.put(group, weight);
    }

    public ArrayList<String> getGroups() {
        return groups;
    }
    public HashMap<String, ArrayList<Assignment>> getAssignments() {
        return assignments;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Adds a new assignment, creating one if absent
     * @param group The group name
     * @param a The assignment
     */
    public void addAssignment(String group, Assignment a) {
        if (!groupWeights.containsKey(group)) {
            groupWeights.put(group, 1.0);
        }
        if (assignments.containsKey(group)) {
            assignments.get(group).add(a);
        } else {
            if(!groupWeights.containsKey(group)) {
                addGroup(group);
            }
            assignments.put(group, new ArrayList<>());
            assignments.get(group).add(a);
        }
    }


    //
    // value: avg()
    // Calculates the minimum average grade needed to get the desired grade
    public double minimumGrade(double desired) {
        // Recalculate the current grade to ensure accuracy before calculation
        recalculate();

        // Check if the desired grade is lower than or equal to the current grade
        if (desired <= grade) {
            return 0.0; // No additional points needed; already meeting or exceeding the desired score
        }

        // Calculate the total weight of assignments already completed
        double currentWeight = 0.0;
        double weightedGradeSum = 0.0;
        for (String group : groupWeights.keySet()) {
            if (assignments.get(group) != null) {
                for (Assignment a : assignments.get(group)) {
                    if (a.grade != a.testGrade) { // Assignment with a valid grade
                        double weight = groupWeights.get(group);
                        currentWeight += weight;
                        weightedGradeSum += (a.grade / a.pointsPossible) * weight;
                        break;
                    }
                }
            }
        }

        // Calculate remaining weight for the upcoming assignment
        double remainingWeight = 1.0 - currentWeight;
        if (remainingWeight <= 0) {
            return -1; // No weight left for new assignments
        }

        // Calculate the minimum grade needed on the upcoming assignment
        double minGradeNeeded = (desired / 100.0 - weightedGradeSum) / remainingWeight;
        return minGradeNeeded * 100; // Return as a percentage between 0 and 100
    }

    public static class Assignment implements Serializable{
        private final double testGrade; // Testgrade is used to see if a grade is valid or not, will be set to 0xDEADBEEF
        private double grade; // The current grade, equal to 0xDEADBEEF if not set
        private double pointsPossible; // Total number of pts. possible
        private String group; // The group name (idk if this needs to be here)
        private String name;

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

        public Assignment(String name, double grade) {
        this.testGrade = 0xDEADBEEF;
            this.grade = grade;
            this.name = name;
        }

        public void clearGrade() {
            grade = testGrade;
        }


        public String getGroup() {
            return this.group;
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

        public void setGrade(double grade) {
            this.grade = grade;
        }

        public void setPointsPossible(double pointsPossible) {
            this.pointsPossible = pointsPossible;
        }
        public String getName() {return this.name;}

        public void setGroup(String group) {
            this.group = group;
        }

        public double getScore() {return this.grade;}
    }
}
