package com.example.recipebook.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipebook.R;
import com.example.recipebook.activities.AddRecipeActivity;

import java.util.ArrayList;

public class InstructionsAdapter extends RecyclerView.Adapter<InstructionsAdapter.InstructionsViewHolder> {
    Context context;
    RecyclerView instructionsRecycler;

    ArrayList<String> instructions;

    public InstructionsAdapter(Context ct,ArrayList<String> instructions,RecyclerView instructionsRecycler){
        this.context = ct;
        this.instructions = instructions;
        this.instructionsRecycler = instructionsRecycler;
    }

    @NonNull
    @Override
    public InstructionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.instructions_ingridients, parent , false);
        return new InstructionsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InstructionsAdapter.InstructionsViewHolder holder, int position) {
        holder.dataText.setText(instructions.get(position));
    }


    @Override
    public int getItemCount() {
        return instructions.size();
    }

    public class InstructionsViewHolder extends RecyclerView.ViewHolder{
        EditText dataText;
        public InstructionsViewHolder(@NonNull View itemView) {
            super(itemView);
            dataText = (EditText)itemView.findViewById(R.id.text);


            itemView.findViewById(R.id.delete).setOnClickListener(v -> {
                instructions.remove(getAdapterPosition());
                instructionsRecycler.removeViewAt(getAdapterPosition());
                notifyItemRemoved(getAdapterPosition());
                notifyItemRangeChanged(getAdapterPosition(), instructions.size());
            });
        }


    }
}


