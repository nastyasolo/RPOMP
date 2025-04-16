package com.example.rpomp81;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DogListFragment extends Fragment {

    private RecyclerView recyclerView;
    private DogAdapter dogAdapter;
    private List<Dog> fullDogList = new ArrayList<>();
    private List<Dog> currentDogList = new ArrayList<>();
    private RequestQueue requestQueue;
    private SharedPreferences preferences;
    private int currentPage = 0;
    private static final int ITEMS_PER_PAGE = 10;

    private TextView pageNumberTextView;

    public DogListFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dog_list, container, false);

        preferences = requireActivity().getSharedPreferences("AppSettings", requireContext().MODE_PRIVATE);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dogAdapter = new DogAdapter(currentDogList, this::openDogDetail, preferences);
        recyclerView.setAdapter(dogAdapter);

        Button loadButton = view.findViewById(R.id.loadButton);
        requestQueue = Volley.newRequestQueue(requireContext());
        loadButton.setOnClickListener(v -> loadDogs());

        Button switchViewButton = view.findViewById(R.id.switchViewButton);
        switchViewButton.setOnClickListener(v -> switchViewMode());

        Button changeRowCountButton = view.findViewById(R.id.changeRowCountButton);
        changeRowCountButton.setOnClickListener(v -> changeRowCount());

        ImageButton prevPageButton = view.findViewById(R.id.prevPageButton);
        ImageButton nextPageButton = view.findViewById(R.id.nextPageButton);
        pageNumberTextView = view.findViewById(R.id.pageNumberTextView);

        prevPageButton.setOnClickListener(v -> {
            if (currentPage > 0) {
                currentPage--;
                updatePage();
            }
        });

        nextPageButton.setOnClickListener(v -> {
            if ((currentPage + 1) * preferences.getInt("row_count", ITEMS_PER_PAGE) < fullDogList.size()) {
                currentPage++;
                updatePage();
            }
        });

        return view;
    }

    private void updatePage() {
        int itemsPerPage = preferences.getInt("row_count", ITEMS_PER_PAGE);
        int minAge = preferences.getInt("age_filter", 0);

        // Фильтруем список по возрасту
        List<Dog> filteredList = filterDogsByAge(fullDogList, minAge);

        int start = currentPage * itemsPerPage;
        int end = Math.min(start + itemsPerPage, filteredList.size());

        currentDogList.clear();
        currentDogList.addAll(filteredList.subList(start, end));
        dogAdapter.notifyDataSetChanged();

        pageNumberTextView.setText("Страница: " + (currentPage + 1));
    }

    private List<Dog> filterDogsByAge(List<Dog> dogs, int minAge) {
        List<Dog> filteredList = new ArrayList<>();
        for (Dog dog : dogs) {
            if (dog.getAge() >= minAge) {
                filteredList.add(dog);
            }
        }
        return filteredList;
    }

    private static final String DEFAULT_URL = "https://gist.githubusercontent.com/nastyasolo/440d72025dcd6552d030fcead11492b8/raw/e0ca4f644bfea7c2e7b3710a3c012da29620715d/dogs.json";

    private void loadDogs() {
        String serverUrl = preferences.getString("server_url", DEFAULT_URL);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, serverUrl, null,
                response -> {
                    try {
                        JSONArray jsonArray = response.getJSONArray("dogs");
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Dog>>() {}.getType();
                        fullDogList = gson.fromJson(jsonArray.toString(), listType);

                        currentPage = 0;
                        updatePage(); // Применяем настройки после загрузки данных
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Ошибка обработки JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getContext(), "Ошибка загрузки данных: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );

        requestQueue.add(request);
    }

    private void openDogDetail(Dog dog) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, DogDetailFragment.newInstance(dog))
                .addToBackStack(null)
                .commit();
    }

    private boolean isListView = true;

    private void switchViewMode() {
        if (isListView) {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        isListView = !isListView;
        dogAdapter.notifyDataSetChanged();
    }

    private void changeRowCount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Изменить количество строк");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("Введите количество строк");
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            try {
                int newRowCount = Integer.parseInt(input.getText().toString());
                preferences.edit().putInt("row_count", newRowCount).apply();
                Toast.makeText(getContext(), "Количество строк изменено", Toast.LENGTH_SHORT).show();
                updatePage(); // Обновляем список после изменения настроек
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Некорректное значение", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    public List<Dog> getDogList() {
        return fullDogList;
    }
}