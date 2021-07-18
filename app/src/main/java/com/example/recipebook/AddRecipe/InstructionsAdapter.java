package com.example.recipebook.AddRecipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipebook.R;

import java.util.ArrayList;
import java.util.List;

public class InstructionsAdapter extends RecyclerView.Adapter<InstructionsAdapter.InstructionsViewHolder> {
    Context context;

    ArrayList<String> instructions;

    public InstructionsAdapter(Context ct,ArrayList<String> instructions){
        this.context = ct;
        this.instructions = instructions;
    }

    @NonNull
    @Override
    public InstructionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.instructions_ingridients, parent , false);
        return new InstructionsViewHolder(view,instructions.get(0));
    }

    @Override
    public void onBindViewHolder(@NonNull InstructionsAdapter.InstructionsViewHolder holder, int position) {
        System.out.println(position);

    }

    @Override
    public int getItemCount() {
        return instructions.size();
    }

    public static class InstructionsViewHolder extends RecyclerView.ViewHolder{
        EditText dataText;
        public InstructionsViewHolder(@NonNull View itemView,String text) {
            super(itemView);
            dataText = (EditText)itemView.findViewById(R.id.text);
            dataText.setText(text);
        }

    }
}


