package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    protected static TreeMap<String, String> dictionary;

    private DictionaryAdapter adapter;
    private ActivityResultLauncher<Intent> activityResultLauncherAddEntry;
    private TextView tvEntryCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        dictionary = readDictionaryFromFile();

        tvEntryCounter = findViewById(R.id.id_textView_nrCuv);
        tvEntryCounter.setText(String.valueOf(dictionary.size()));

        // Recycler view
        RecyclerView recyclerView = findViewById(R.id.id_recyclerView);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL));

        adapter = new DictionaryAdapter(new ArrayList<>(dictionary.entrySet()));
        recyclerView.setAdapter(adapter);

        //
        activityResultLauncherAddEntry = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Extrage datele returnate la incheierea activitatii noi
                        Intent data = result.getData();
                        if (data == null) {
                            throw new AssertionError();
                        } else {
                            String key = data.getStringExtra("NEW_KEY");
                            String value = data.getStringExtra("NEW_VALUE");

                            // Adauga in fisier
                            SharedPreferences sharedPreferences = getSharedPreferences("DictionaryFile", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(key, value);

                            // Apply the changes
                            if (editor.commit()) {
                                List<Map.Entry<String, String>> itemList = adapter.getItemList();
                                itemList.add(new AbstractMap.SimpleEntry<>(key, value));
                                itemList.sort(Comparator.comparing(Map.Entry<String, String>::getKey));

                                adapter.notifyDataSetChanged();

                                dictionary.put(key, value);

                                tvEntryCounter.setText(String.valueOf(dictionary.size()));

                                Toast.makeText(this, R.string.entry_added,
                                        Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(this, R.string.error,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dictionary_menu, menu);
        // Search bar
        MenuItem searchItem = menu.findItem(R.id.id_menu_item_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        // Listener search bar
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String text) {
                adapter.setItemList(new ArrayList<>(dictionary.entrySet()));

                text = text.trim();

                if (text.equals("")){
                    adapter.notifyDataSetChanged();
                    return true;
                }

                text = text.toLowerCase();

                String finalText = text;
                List<Map.Entry<String, String>> filteredItemList = adapter.getItemList()
                        .stream()
                        .filter(entry -> entry.getKey().toLowerCase().contains(finalText)
                                | entry.getValue().toLowerCase().contains(finalText))
                        .collect(Collectors.toList());

                adapter.setItemList(filteredItemList);
                adapter.notifyDataSetChanged();
                return true;
            }
        });
        searchView.setOnCloseListener(() -> {
            adapter.setItemList(new ArrayList<>(dictionary.entrySet()));
            adapter.notifyDataSetChanged();
            return false;
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void onAddButtonClick(MenuItem menuItem) {
        activityResultLauncherAddEntry.launch(new Intent(this, AddEntryActivity.class));
    }

    private TreeMap<String, String> readDictionaryFromFile() {
        SharedPreferences sharedPreferences = getSharedPreferences("DictionaryFile", MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPreferences.getAll();

        TreeMap<String, String> treeMap = new TreeMap<>();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().toString();
            treeMap.put(key, value);
        }

        return treeMap;
    }
}