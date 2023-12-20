package com.example.myapplication;

import androidx.fragment.app.Fragment;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentEntry#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentEntry extends Fragment {

    public FragmentEntry() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment.
     */
    public static FragmentEntry newInstance() {
        return new FragmentEntry();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_layout, container, false);

        // Access the data from the arguments bundle
        Bundle args = getArguments();
        if(args != null) {
            String key = args.getString("key", "error");
            String value = args.getString("value", "error");

            TextView tvKey = view.findViewById(R.id.id_tv_key_fragment_layout);
            TextView tvVal = view.findViewById(R.id.id_tv_val_fragment_layout);

            tvKey.setText(key);
            tvVal.setText(value);
        }
        return view;
    }
}