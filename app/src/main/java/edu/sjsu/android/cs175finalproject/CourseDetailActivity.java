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
import edu.sjsu.android.cs175finalproject.Course.Assignment;

import java.util.ArrayList;
import java.util.Locale;

// This is something important, guys. This is the CourseDetail class that renders all the grade calculations.
// 1. Set Group weight
// TODO: If we need a database, we might have to fetch all the data here and generate the page.
public class CourseDetailActivity extends AppCompatActivity {

    private ArrayList<Assignment> assignmentList;
    private AssignmentAdapter assignmentAdapter;
    private String courseTitle;
    private double desiredGrade = 100;

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

        Button calculateMinimumGradeButton = findViewById(R.id.calculateDesiredGradeButton);
        calculateMinimumGradeButton.setOnClickListener(this::showRequiredGrade);
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
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        // Show the dialog
        builder.create().show();
    }

    private void showRequiredGrade(View view) {
        // Recalculate the course grade
        course.recalculate();

        // Assuming 'desiredGrade' is a field or passed value
        double minimumGrade = course.minimumGrade(desiredGrade);
        String letterGrade = course.getLetterGrade();
        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your required grade to achieve the desired grade:\n" +
                "Minimum Numeric Grade: " + String.format(Locale.US, "%.2f", minimumGrade) + "\n\n");
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

        builder.setPositiveButton("Add", (dialog, which) -> {
            try {
                String name = nameInput.getText().toString().trim();
                if (!name.matches("[a-zA-Z0-9-_]+")) {
                    throw new IllegalArgumentException("Assignment name can only contain letter (uppercase or lowercase), digit, or hyphen.");
                }
                String group = groupInput.getText().toString().trim();
                if (!group.matches("[a-zA-Z0-9-_]+")) {
                    throw new IllegalArgumentException("Group name can only contain letter (uppercase or lowercase), digit, or hyphen.");
                }
                double score = Double.parseDouble(scoreInput.getText().toString());
                if (score < 0) {
                    throw  new IllegalArgumentException("Invalid score");
                }
                double scorePossible = Double.parseDouble(scorePossibleInput.getText().toString());
                if (scorePossible < score) {
                    throw new IllegalArgumentException("Invalid possible score");
                }

                // UI stuff
                assignmentList.add(new Assignment(name + "\uD83D\uDCDA", score));

                Course.Assignment assignment = new Course.Assignment(score, scorePossible, group);
                course.addAssignment(group, assignment);

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