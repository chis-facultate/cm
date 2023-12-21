package com.example.myapplication;

import com.example.myapplication.model.APIResponse;

public interface OnFetchDataListener {
    void onFetchData(APIResponse apiResponse);
    void onError();
}
