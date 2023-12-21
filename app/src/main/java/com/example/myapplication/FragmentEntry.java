package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.myapplication.model.APIResponse;
import com.example.myapplication.model.Phonetic;

public class FragmentEntry extends Fragment implements OnFetchDataListener {

    private String key;
    private String value;

    public FragmentEntry() {
        // Required empty public constructor
    }

    public static FragmentEntry newInstance() {
        return new FragmentEntry();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Access the data from the arguments bundle
        Bundle args = getArguments();
        if (args == null) {
            return;
        }

        key = args.getString("key", "error");
        value = args.getString("value", "error");

        RequestManager requestManager = new RequestManager();
        requestManager.getWordData(this, value);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_layout, container, false);

        TextView tvKey = view.findViewById(R.id.id_tv_key_fragment_layout);
        TextView tvVal = view.findViewById(R.id.id_tv_val_fragment_layout);

        tvKey.setText(key);
        tvVal.setText(value);

        return view;
    }

    @Override
    public void onFetchData(APIResponse apiResponse) {
        if (apiResponse == null) {
            Toast.makeText(getContext(), "RESPONSE NOT SUCCESSFULL", Toast.LENGTH_SHORT).show();
            return;
        }
        // Afiseaza date obtinute
        ArrayAdapter adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,
                apiResponse.getPhonetics());

        ListView listView = getView().findViewById(R.id.id_lv_phonetics);
        listView.setAdapter(adapter);
    }

    @Override
    public void onError() {
        Toast.makeText(getContext(), "ERROR CALL", Toast.LENGTH_SHORT).show();
    }
}