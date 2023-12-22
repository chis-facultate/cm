package com.example.myapplication.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.myapplication.FileManager;
import com.example.myapplication.FragmentEntry;
import com.example.myapplication.R;
import com.example.myapplication.adapter.DictionaryAdapter;

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

    private FileManager fileManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        fileManager = new FileManager("DictionaryFile", this);
        dictionary = fileManager.readDictionaryFromFile();

        tvEntryCounter = findViewById(R.id.id_textView_nrCuv);
        tvEntryCounter.setText(String.valueOf(dictionary.size()));

        // Recycler view adapter
        adapter = new DictionaryAdapter();
        adapter.setItemList(new ArrayList<>(dictionary.entrySet()));
        adapter.setOnItemClickListener(position -> {
            // Extrage intrarea care a fost apasata
            Map.Entry<String, String> entry = adapter.getItemList().get(position);

            // Paseaza datele catre fragment prin bundle
            Bundle bundle = new Bundle();
            bundle.putString("key", entry.getKey());
            bundle.putString("value", entry.getValue());

            FragmentEntry fragmentEntry = FragmentEntry.newInstance();
            fragmentEntry.setArguments(bundle);

            // display the fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.id_fragment_container, fragmentEntry)
                    .addToBackStack(null)
                    .commit();
        });

        // Recycler view
        RecyclerView recyclerView = findViewById(R.id.id_recyclerView);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);

        // Listener swipe
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper
                .SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                Map.Entry<String, String> deletedEntry = adapter.getItemList().get(position);

                // Sterge din fisier
                if(fileManager.deleteEntryFromFile(deletedEntry.getKey())) {
                    // Stergere din lista
                    adapter.getItemList().remove(position);
                    adapter.notifyItemRemoved(position);
                    // Sterge din dictionar din memorie
                    dictionary.remove(deletedEntry.getKey(), deletedEntry.getValue());
                    // Actualizeaza counter
                    tvEntryCounter.setText(String.valueOf(dictionary.size()));
                }
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);

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
                            if (fileManager.writeEntryInFile(key, value)) {
                                // Adauga in lista
                                List<Map.Entry<String, String>> itemList = adapter.getItemList();
                                itemList.add(new AbstractMap.SimpleEntry<>(key, value));
                                itemList.sort(Comparator.comparing(Map.Entry<String, String>::getKey));
                                adapter.notifyDataSetChanged();
                                // Adauga in dictionar
                                dictionary.put(key, value);
                                // Actualizeaza counter
                                tvEntryCounter.setText(String.valueOf(dictionary.size()));

                                Toast.makeText(this, R.string.entry_added,
                                        Toast.LENGTH_SHORT).show();
                            } else {
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

                if (text.equals("")) {
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

    @Override
    public void onBackPressed() {
        // Check if there are fragments in the back stack
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            // Pop all fragments from the back stack at once
            fragmentManager.popBackStackImmediate(null,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else {
            // No fragments in the back stack, proceed with default behavior
            super.onBackPressed();
        }
    }
}