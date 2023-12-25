package com.example.myapplication;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.myapplication.adapter.PhoneticsAdapter;
import com.example.myapplication.model.APIResponse;

public class FragmentEntry extends Fragment implements OnFetchDataListener {

    private String key;
    private String value;
    private ObjectAnimator fadeIn;

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

        // La instantierea fragmentului se face call la API
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

        // Animatie
        TextView tvLoading = view.findViewById(R.id.id_tv_loading_fragment_layout);
        tvLoading.setAlpha(0f);
        fadeIn = ObjectAnimator.ofFloat(tvLoading, "alpha", 0f, 1f);
        fadeIn.setDuration(1000);
        fadeIn.setRepeatCount(ValueAnimator.INFINITE);
        fadeIn.start();

        return view;
    }

    @Override
    public void onFetchData(APIResponse apiResponse) {
        TextView tvLoad = getView().findViewById(R.id.id_tv_loading_fragment_layout);
        if (apiResponse == null) {
            fadeIn.cancel();
            tvLoad.setText(R.string.error);
        }
        else {
            fadeIn.cancel();
            tvLoad.setVisibility(View.GONE);
            // Afiseaza date obtinute
            PhoneticsAdapter adapter = new PhoneticsAdapter(getContext(), R.layout.list_view_item_layout,
                    apiResponse.getPhonetics());

            ListView listView = getView().findViewById(R.id.id_lv_phonetics);
            listView.setAdapter(adapter);
        }
    }

    @Override
    public void onError() {
        TextView tvLoad = getView().findViewById(R.id.id_tv_loading_fragment_layout);
        fadeIn.cancel();
        tvLoad.setText(R.string.error);
    }
}