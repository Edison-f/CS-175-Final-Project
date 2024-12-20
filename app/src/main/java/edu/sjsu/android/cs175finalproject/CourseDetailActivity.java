package edu.sjsu.android.cs175finalproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import edu.sjsu.android.cs175finalproject.Course.Assignment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

// This is something important, guys. This is the CourseDetail class that renders all the grade calculations.
// 1. Set Group weight
public class CourseDetailActivity extends AppCompatActivity {

    private ArrayList<Assignment> assignmentList;
    private ArrayList<String> groupList;
    RecyclerView recyclerView;
    //private HashMap<String, Double> groupWeights;

    // this should sync with the course assignments
    private AssignmentAdapter assignmentAdapter;
    private Course course;

    private String courseTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.courseTitle = getIntent().getStringExtra("courseTitle");
        try {
            Log.wtf("Reading File", this.getFilesDir() + "/" + courseTitle);
            File file = new File(getFilesDir(), courseTitle);

            if (file.exists() && file.length() > 0) {
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream is = new ObjectInputStream(fis);
                try {
                    this.course = (Course) is.readObject();
                    this.assignmentList = (ArrayList<Assignment>) this.course.returnAssignmentList();
                    this.groupList = course.getGroups();

                    is.close();
                    fis.close();
                } catch (Exception e) {
                    init(this.courseTitle);
                }

            } else {
                init(this.courseTitle);
            }
        }
        catch (Exception e) {Log.wtf("Reading Course File Error", e.getMessage());}

        setContentView(R.layout.activity_course_detail);

        TextView courseTitleView = findViewById(R.id.textViewCourseTitle);
        courseTitleView.setText(courseTitle);

        recyclerView = findViewById(R.id.recyclerViewAssignments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        assignmentAdapter = new AssignmentAdapter(assignmentList, this.course);
        recyclerView.setAdapter(assignmentAdapter);

        Button addAssignmentButton = findViewById(R.id.addAssignmentButton);
        addAssignmentButton.setOnClickListener(this::showAddAssignmentDialog);

        Button calculateGradeButton = findViewById(R.id.calculateGradeButton);
        calculateGradeButton.setOnClickListener(this::showCalculatedGrade);

        Button calculateMinimumGradeButton = findViewById(R.id.calculateDesiredGradeButton);
        calculateMinimumGradeButton.setOnClickListener(this::showRequiredGrade);

        Button addGroupButton = findViewById(R.id.addGroupButton);
        addGroupButton.setOnClickListener(this::showAddGroupDialog);

        Button showGroupButton = findViewById(R.id.showGroupButton);
        showGroupButton.setOnClickListener(this::showShowGroupsDialog);
    }

    public void init (String courseTitle) {
        this.assignmentList = new ArrayList<>();
        groupList = new ArrayList<>();

        this.course = new Course();
        this.course = new Course();
    }

    @Override
    protected void onDestroy() {
        try {
            Log.wtf("Saving to", String.valueOf(this.getFilesDir()));
            FileOutputStream fos = getApplicationContext().openFileOutput(courseTitle, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);


            os.writeObject(course);
            os.close();
            fos.close();
        } catch (IOException e) {
            Log.wtf("Course Save Error", e.getMessage());
        }
        super.onDestroy();
    }


    private void showCalculatedGrade(View view) {
        course.recalculate();
        String letterGrade = course.getLetterGrade();
        String numericGrade = course.getNumericalGrade();
        for (String group : course.getGroups()) {
            Log.d("CourseDetailActivity", course.getAssignments() + "Group: " + group + ", Weight: " + course.groupWeights.get(group));
        }
        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Calculated Grade");
        builder.setMessage("Your current grade:\n" +
                "Letter Grade: " + letterGrade + "\n" +
                "Numeric Grade: " + String.format(Locale.US, "%.2f", Double.parseDouble(numericGrade)));
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        // Show the dialog
        builder.create().show();
    }

    private void showRequiredGrade(View view) {
        // Recalculate the course grade
        course.recalculate();
        TextView des = findViewById(R.id.desiredInput);
        if (des.getText().toString().isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Invalid Input")
                    .setMessage("Desired grade cannot be empty.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }
        try {
            double desiredGrade = Double.parseDouble(des.getText().toString());
            double minimumGrade = course.minimumGrade(desiredGrade);
            String message;
            if (minimumGrade == -1) {
                message = "Please use the 'Add Group' button to input group weights for" +
                        " the application to calculate the required score.\n";
            } else {
                message = "Your required grade to achieve the desired grade:\n" +
                        "Minimum Numeric Grade: " + String.format(Locale.US, "%.2f", minimumGrade) + "\n\n";
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Required Grade")
                    .setMessage(message) // Set the message here
                    .setPositiveButton("OK", null)
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show(); // Show the dialog
        } catch (IllegalArgumentException e) {
            new AlertDialog.Builder(this)
                    .setTitle("Invalid Input")
                    .setMessage(e.getMessage())
                    .setPositiveButton("OK", null)
                    .show();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void showShowGroupsDialog(View view) {
         // in order to make it more pretty, I decided to print out a table...
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_show_group, null);
        builder.setView(dialogView);

        TableLayout tableLayout = dialogView.findViewById(R.id.tableLayout);
        ArrayList<String[]> groupsData = new ArrayList<>();
        for (Map.Entry<String, Double> ent : this.course.groupWeights.entrySet()) {
            String groupName = ent.getKey();
            String groupWeights = ent.getValue().toString();
            groupsData.add(new String[]{groupName, groupWeights});
        }
        for (String[] row : groupsData) {
            TableRow tableRow = new TableRow(this);

            for (String cell : row) {
                TextView textView = new TextView(this);
                textView.setText(cell);
                textView.setPadding(16, 16, 16, 16);
                textView.setGravity(Gravity.CENTER);
                tableRow.addView(textView);
            }

            tableLayout.addView(tableRow);
        }
        builder.setNegativeButton("OK", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void showAddGroupDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_group, null);
        builder.setView(dialogView);
        TextView nameInput = dialogView.findViewById(R.id.groupNameInput);
        TextView weightInput = dialogView.findViewById(R.id.groupWeightInput);
        builder.setPositiveButton("Add", (dialog, which) -> {
            try {
                String groupName = nameInput.getText().toString().trim();
                if (!groupName.matches("[a-zA-Z0-9-_]+")) {
                    throw new IllegalArgumentException("Group name can only contain letter (uppercase or lowercase), digit, or hyphen.");
                }

                double weight = Double.parseDouble(weightInput.getText().toString());
                if (weight < 0) {
                    throw new IllegalArgumentException("Invalid weight");
                }

                // UI stuff
                groupList.add(groupName + " with weight " + weight);
                course.addGroup(groupName, weight);

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

    @SuppressLint("NotifyDataSetChanged")
    private void showAddAssignmentDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_assignment, null);
        builder.setView(dialogView);
        AtomicReference<Assignment> assignment = new AtomicReference<>();
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
                String scoreString = scoreInput.getText().toString();

                double scorePossible = Double.parseDouble(scorePossibleInput.getText().toString());

                double score = Double.parseDouble(scoreString);
                if (scorePossible < score) {
                    throw new IllegalArgumentException("Invalid possible score");
                }
                Assignment newAssignment = new Assignment(name, score, scorePossible, group);
                assignmentList.add(newAssignment);
                assignment.set(newAssignment);

                // UI stuff
                course.addAssignment(group, assignment.get());
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