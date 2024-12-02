package edu.sjsu.android.cs175finalproject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

// This is something important, guys. This is the CourseDetail class that renders all the grade calculations.
// TODO: If we need a database, we might have to fetch all the data here and generate the page.
public class CourseDetailActivity extends AppCompatActivity {

    private ArrayList<Assignment> assignmentList;
    private AssignmentAdapter assignmentAdapter;
    private String courseTitle;

    // in this course details class, we have our Course class
    private Course course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        // this is where we need to fetch from the DB I think.
        // I don't know how to do that yet, but we will figure it out.
        courseTitle = getIntent().getStringExtra("courseTitle");
        assignmentList = new ArrayList<>();
        this.course = new Course();
        this.course.setName(courseTitle);

        TextView courseTitleView = findViewById(R.id.textViewCourseTitle);
        courseTitleView.setText(courseTitle);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewAssignments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        assignmentAdapter = new AssignmentAdapter(assignmentList);
        recyclerView.setAdapter(assignmentAdapter);

        Button addAssignmentButton = findViewById(R.id.addAssignmentButton);
        addAssignmentButton.setOnClickListener(this::showAddAssignmentDialog);

        Button calculateGradeButton = findViewById(R.id.calculateGradeButton);
        calculateGradeButton.setOnClickListener(this::showCalculatedGrade);
    }
    private void showCalculatedGrade(View view) {
        course.recalculate();
        String letterGrade = course.getLetterGrade();
        String numericGrade = course.getNumericalGrade();

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Calculated Grade");
        builder.setMessage("Your current grade:\n" +
                "Letter Grade: " + letterGrade + "\n" +
                "Numeric Grade: " + String.format(Locale.US,"%.2f", Double.parseDouble(numericGrade)));

        // Add an OK button
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        // Show the dialog
        builder.create().show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void showAddAssignmentDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_assignment, null);
        builder.setView(dialogView);

        TextView nameInput = dialogView.findViewById(R.id.assignmentNameInput);
        TextView groupInput = dialogView.findViewById(R.id.assignmentGroup);
        TextView scoreInput = dialogView.findViewById(R.id.assignmentScoreInput);
        TextView scorePossibleInput = dialogView.findViewById(R.id.assignmentScorePossible);
        // TextView weightInput = dialogView.findViewById(R.id.assignmentWeightInput);

        builder.setPositiveButton("Add", (dialog, which) -> {
            try {
                String group = groupInput.getText().toString().trim();
                double score = Double.parseDouble(scoreInput.getText().toString());
                double scorePossible = Double.parseDouble(scorePossibleInput.getText().toString());
                String name = nameInput.getText().toString().trim();
                // UI stuff
                assignmentList.add(new Assignment(name, score));

                // Logic stuff
                Course.Assignment assignment = new Course.Assignment(score, scorePossible, group);
                course.addAssignment(group, assignment);
                course.recalculate();

                assignmentAdapter.notifyDataSetChanged();
            } catch (IllegalArgumentException e) {
                // Handle custom validation errors
                new AlertDialog.Builder(this)
                        .setTitle("Invalid Input")
                        .setMessage(e.getMessage())
                        .setPositiveButton("OK", null)
                        .show();
            } catch (Exception e) {
                // Handle unexpected errors
                new AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("An unexpected error occurred. Please try again.")
                        .setPositiveButton("OK", null)
                        .show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}