package com.example.rpomp81;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;

public class DogDetailFragment extends Fragment {
    private static final String ARG_DOG = "dog";

    public static DogDetailFragment newInstance(Dog dog) {
        DogDetailFragment fragment = new DogDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DOG, dog);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dog_detail, container, false);

        try {
            TextView nameTextView = view.findViewById(R.id.dogName);
            TextView descriptionTextView = view.findViewById(R.id.dogDescription);
            TextView ageTextView = view.findViewById(R.id.dogAge);
            ImageView imageView = view.findViewById(R.id.dogImage);

            if (getArguments() != null) {
                Dog dog = (Dog) getArguments().getSerializable(ARG_DOG);
                if (dog != null) {
                    nameTextView.setText(dog.getName());
                    descriptionTextView.setText(dog.getDescription());
                    ageTextView.setText("Возраст: " + dog.getAge() + " лет");

                    Glide.with(this)
                            .load(dog.getImage())
                            .error(R.drawable.ic_launcher_background)
                            .into(imageView);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Ошибка при загрузке данных: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return view;
    }
}
