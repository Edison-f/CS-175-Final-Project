package edu.sjsu.android.cs175finalproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainScreen#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainScreen extends Fragment {

    private RecyclerView recyclerView;
    private CourseAdapter courseAdapter;
    private static ArrayList<String> courseList;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MainScreen() {
        // Required empty public constructor
    }

    public static MainScreen newInstance(String param1, String param2) {
        MainScreen fragment = new MainScreen();
        Bundle args = new Bundle();
        // You can pass parameters using this bundle if needed
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // mParam1 = getArguments().getString(ARG_PARAM1);
            // mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_screen, container, false);

        // Initialize RecyclerView and other components
        recyclerView = view.findViewById(R.id.recyclerViewCourses);

        // Assuming you want a vertical list
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Example data - replace with your own data source
        courseList = new ArrayList<>();
        courseList.add("✏\uFE0F Course 1");
        courseList.add("✏\uFE0F Course 2");
        courseList.add("✏\uFE0F Course 3");

        // Initialize adapter and set it to RecyclerView
        courseAdapter = new CourseAdapter(courseList);
        recyclerView.setAdapter(courseAdapter);

        return view;
    }
}
