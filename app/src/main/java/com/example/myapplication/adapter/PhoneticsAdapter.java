package com.example.myapplication.adapter;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

        // Get the current item from the data set
        Phonetic currentItem = (Phonetic) getItem(position);

        // Set the item text in the TextView
        TextView tv = view.findViewById(R.id.id_tv_phonetic);
        tv.setText(currentItem.getText());

        // Add listener on button
        Button btn = view.findViewById(R.id.id_btn_listen);
        btn.setOnClickListener(v -> {
            String audioUrl = currentItem.getAudio();
            MediaPlayer player = new MediaPlayer();
            try{
                player.setAudioStreamType(AudioManager.STREAM_MUSIC); //trebuie setat pe music altfel nu merge
                player.setDataSource(audioUrl);
                player.prepare(); // instructiune blocanta
                player.start();
            }catch (IOException e){
                e.printStackTrace();
            }
        });

        return view;
    }
}
