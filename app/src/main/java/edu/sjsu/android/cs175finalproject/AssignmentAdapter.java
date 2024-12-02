package edu.sjsu.android.cs175finalproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

// this is not important
public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder> {

    private final ArrayList<Assignment> assignmentList;

    public AssignmentAdapter(ArrayList<Assignment> assignmentList) {
        this.assignmentList = assignmentList;
    }

    @NonNull
    @Override
    public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.assignment_item, parent, false);
        return new AssignmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position) {
        Assignment assignment = assignmentList.get(position);
        holder.nameTextView.setText(assignment.getName());
        holder.scoreTextView.setText(String.format("Score: %.2f", assignment.getScore()));
        //holder.weightTextView.setText(String.format("Weight: %.2f%%", assignment.getWeight()));
    }

    @Override
    public int getItemCount() {
        return assignmentList.size();
    }

    public static class AssignmentViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView scoreTextView;
        TextView weightTextView;

        public AssignmentViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.assignmentName);
            scoreTextView = itemView.findViewById(R.id.assignmentScore);
            weightTextView = itemView.findViewById(R.id.assignmentWeight);
        }
    }
}
