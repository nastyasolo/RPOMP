package com.example.rpomp81;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class DogAdapter extends RecyclerView.Adapter<DogAdapter.DogViewHolder> {
    private List<Dog> dogList;
    private OnDogClickListener listener;
    private SharedPreferences preferences;

    public interface OnDogClickListener {
        void onDogClick(Dog dog);
    }

    public DogAdapter(List<Dog> dogList, OnDogClickListener listener, SharedPreferences preferences) {
        this.dogList = dogList;
        this.listener = listener;
        this.preferences = preferences;
    }

    @NonNull
    @Override
    public DogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dog, parent, false);
        return new DogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DogViewHolder holder, int position) {
        try {
            Dog dog = dogList.get(position);
            holder.nameTextView.setText(dog.getName());

            boolean showAge = preferences.getBoolean("show_age", true);
            boolean showDescription = preferences.getBoolean("show_description", true);

            if (showAge) {
                holder.ageTextView.setText("Возраст: " + dog.getAge() + " лет");
                holder.ageTextView.setVisibility(View.VISIBLE);
            } else {
                holder.ageTextView.setVisibility(View.GONE);
            }

            if (showDescription) {
                holder.descriptionTextView.setText(dog.getDescription());
                holder.descriptionTextView.setVisibility(View.VISIBLE);
            } else {
                holder.descriptionTextView.setVisibility(View.GONE);
            }

            Glide.with(holder.itemView.getContext())
                    .load(dog.getImage())
                    .error(R.drawable.ic_launcher_background)
                    .into(holder.imageView);

            holder.itemView.setOnClickListener(v -> listener.onDogClick(dog));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(holder.itemView.getContext(), "Ошибка при загрузке данных: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void updateData(List<Dog> newData) {
        this.dogList = newData;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return dogList.size();
    }

    static class DogViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, ageTextView, descriptionTextView;
        ImageView imageView;

        public DogViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.dogName);
            ageTextView = itemView.findViewById(R.id.dogAge);
            descriptionTextView = itemView.findViewById(R.id.dogDescription);
            imageView = itemView.findViewById(R.id.dogImage);
        }
    }
}