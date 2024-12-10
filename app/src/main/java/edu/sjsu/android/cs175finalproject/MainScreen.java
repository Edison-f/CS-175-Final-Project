package edu.sjsu.android.cs175finalproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainScreen#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainScreen extends Fragment {

    private RecyclerView recyclerView;
    private CourseAdapter courseAdapter;
    private ArrayList<String> courseList;

    public MainScreen() {
    }

    public static MainScreen newInstance() {
        return new MainScreen();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the correct layout
        View view = inflater.inflate(R.layout.fragment_main_screen, container, false);

        // Initialize button and set its click listener
        view.findViewById(R.id.serialize).setOnClickListener(v -> {
        });
        Button addCourseButton = view.findViewById(R.id.btnAddCourse);
        addCourseButton.setOnClickListener(v -> {
            // Show a dialog to get the course name from the user
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Add Course");

            // Create an EditText input field
            final EditText input = new EditText(requireContext());
            input.setHint("Enter course name");

            // Add the EditText to the dialog
            builder.setView(input);

            // Set up the dialog buttons
            builder.setPositiveButton("Add", (dialog, which) -> {
                String courseName = input.getText().toString().trim();
                if (!courseName.isEmpty()) {
                    addCourse(courseName); // Add the course
                } else {
                    Toast.makeText(getContext(), "Course name cannot be empty", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            // Show the dialog
            builder.show();
        });

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewCourses);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize course list and adapter <== This is just some default courses (if we dont have DB... we can fake it)
        courseList = new ArrayList<>();
        try {
            FileInputStream fis = new FileInputStream(getContext().getFilesDir() + "/" + "courseList");
            Scanner in = new Scanner(fis);
            while (in.hasNextLine()) {
                courseList.add(in.nextLine());
            }
            Log.wtf("Worked", "Read");

        } catch (Exception e) {
            Log.wtf("Read", e.getMessage());
            courseList.add("✏️ CS175");
            courseList.add("✏️ CS101");
            courseList.add("✏️ CS146");
        }
        courseAdapter = new CourseAdapter(courseList, this::onCourseClick);
        recyclerView.setAdapter(courseAdapter);

        return view;
    }

    // Method to add a course
    public void addCourse(String courseTitle) {
        if (courseList != null && courseAdapter != null) {
            courseList.add(courseTitle); // Add the course to the list
            courseAdapter.notifyItemInserted(courseList.size() - 1); // Notify the adapter
        }
        try {
            FileOutputStream fos = new FileOutputStream(getContext().getFilesDir() + "/" + "courseList", false);
            for (String course : courseList) {
                fos.write((course + "\n").getBytes());
                Log.wtf("Course data", (course + "\n"));
            }
            fos.close();
            Log.wtf("Worked", "Saved");
        } catch (IOException e) {
            Log.wtf("save error", e.getMessage());
        }
    }

    private void onCourseClick(String courseTitle) {
        // Navigate to CourseDetailActivity with the selected course
        Intent intent = new Intent(getContext(), CourseDetailActivity.class);
        intent.putExtra("courseTitle", courseTitle);
        startActivity(intent);
    }
}
