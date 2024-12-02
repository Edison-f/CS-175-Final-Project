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

public class CourseDetailActivity extends AppCompatActivity {

    // This is something important guys, we need to use
    private ArrayList<Assignment> assignmentList;
    private AssignmentAdapter assignmentAdapter;
    private String courseTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        courseTitle = getIntent().getStringExtra("courseTitle");
        assignmentList = new ArrayList<>();

        TextView courseTitleView = findViewById(R.id.textViewCourseTitle);
        courseTitleView.setText(courseTitle);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewAssignments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        assignmentAdapter = new AssignmentAdapter(assignmentList);
        recyclerView.setAdapter(assignmentAdapter);

        Button addAssignmentButton = findViewById(R.id.addAssignmentButton);
        addAssignmentButton.setOnClickListener(this::showAddAssignmentDialog);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void showAddAssignmentDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_assignment, null);
        builder.setView(dialogView);

        TextView nameInput = dialogView.findViewById(R.id.assignmentNameInput);
        TextView scoreInput = dialogView.findViewById(R.id.assignmentScoreInput);
        TextView weightInput = dialogView.findViewById(R.id.assignmentWeightInput);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = nameInput.getText().toString().trim();
            double score = Double.parseDouble(scoreInput.getText().toString());
            double weight = Double.parseDouble(weightInput.getText().toString());

            assignmentList.add(new Assignment(name, score, weight));
            // If you dont add this line, it will be buggy slow
            assignmentAdapter.notifyDataSetChanged();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}
