package com.example.myapplication;

import com.example.myapplication.model.APIResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class RequestManager {

    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.dictionaryapi.dev/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public interface CallDictionary {
        @GET("entries/en/{word}")
        Call<List<APIResponse>> call(@Path("word") String word);
    }

    public void getWordData(OnFetchDataListener listener, String word) {
        // Creeaza o imnplementare a interfetei date ca parametru folosind adnotarile
        CallDictionary callDictionaryImpl = retrofit.create(CallDictionary.class);
        Call<List<APIResponse>> call = callDictionaryImpl.call(word);

        try {
            call.enqueue(new Callback<List<APIResponse>>() {
                @Override
                public void onResponse(Call<List<APIResponse>> call,
                                       Response<List<APIResponse>> response) {
                    if (!response.isSuccessful()) {
                        System.out.println(response);
                        return;
                    }
                    listener.onFetchData(response.body().get(0));
                }
                @Override
                public void onFailure(Call<List<APIResponse>> call, Throwable t) {
                    listener.onError();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
