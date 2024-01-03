package com.example.myapplication.adapter;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.model.Phonetic;

import java.io.IOException;
import java.util.List;

public class PhoneticsAdapter extends ArrayAdapter {

    public PhoneticsAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_view_item_layout,
                    parent, false);
        }

        TextView tv = view.findViewById(R.id.id_tv_phonetic);
        ImageButton btn = view.findViewById(R.id.id_btn_listen);

        // Get the current item from the data set
        Phonetic currentItem = (Phonetic) getItem(position);
        String phoneticTranslation = currentItem.getText();
        String audioUrl = currentItem.getAudio();

        // Verifica daca am primit date incomplete de la API
        boolean missingPhoneticTranslation = phoneticTranslation.equals("");
        boolean missingAudio = audioUrl.equals("");

        if (missingPhoneticTranslation && missingAudio) {
            view.setVisibility(View.GONE);
        } else if (missingAudio) {
            tv.setText(phoneticTranslation);
            btn.setVisibility(View.INVISIBLE);
        } else {
            tv.setText(phoneticTranslation);
            btn.setOnClickListener(v -> {
                MediaPlayer player = new MediaPlayer();
                try {
                    player.setAudioStreamType(AudioManager.STREAM_MUSIC); //trebuie setat pe music altfel nu merge
                    player.setDataSource(audioUrl);
                    player.prepare(); // instructiune blocanta
                    player.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        return view;
    }
}
