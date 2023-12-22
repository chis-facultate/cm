package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;
import java.util.TreeMap;

public class FileManager {
    private String fileName;
    private Context context;

    public FileManager(String fileName, Context context) {
        this.fileName = fileName;
        this.context = context;
    }

    public TreeMap<String, String> readDictionaryFromFile() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, context.MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPreferences.getAll();

        TreeMap<String, String> treeMap = new TreeMap<>();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().toString();
            treeMap.put(key, value);
        }

        return treeMap;
    }

    public boolean writeEntryInFile(String key, String value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public boolean deleteEntryFromFile(String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        return editor.commit();
    }
}
