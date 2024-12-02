package edu.sjsu.android.cs175finalproject;

// this is not important
public class Assignment {
    private String name;
    private double score;
    private double weight; // Weight as a decimal (e.g., 0.2 for 20%)

    public Assignment(String name, double score, double weight) {
        this.name = name;
        this.score = score;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public double getScore() {
        return score;
    }

    public double getWeight() {
        return weight;
    }
}
