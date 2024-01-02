package com.example.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.R;

public class AddEntryActivity extends AppCompatActivity {

    private EditText etKeyField;
    private EditText etValueField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_entry_activity);

        etKeyField = findViewById(R.id.id_et_key_field);
        etValueField = findViewById(R.id.id_et_value_field);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.add_entry_menu, menu);
        return true;
    }

    public void onButtonSaveClick(MenuItem menuItem){
        // Extrage datele introduse
        String key = etKeyField.getText().toString();
        String value = etValueField.getText().toString();

        boolean success = true;

        // Verifica datele introduse
        if(key.equals("")) {
            Toast.makeText(this, R.string.wrong_key_input,
                    Toast.LENGTH_SHORT).show();
            success = false;
        }
        if(value.equals("")) {
            Toast.makeText(this, R.string.wrong_val_input,
                    Toast.LENGTH_SHORT).show();
            success = false;
        }
        for(String dictKey: MainActivity.dictionary.keySet()){
            if (key.equals(dictKey)){
                Toast.makeText(this, R.string.key_exists,
                        Toast.LENGTH_SHORT).show();
                success = false;
            }
        }

        if (success) {
            // Trimite perechea cheie, valoare catre activitatea anterioara
            setResult(Activity.RESULT_OK, new Intent()
                    .putExtra("NEW_KEY", key)
                    .putExtra("NEW_VALUE", value));
            // Revenim la activitatea anterioara
            finish();
        }
    }

    public void onButtonDiscardClick(MenuItem menuItem){
        finish();
    }
}