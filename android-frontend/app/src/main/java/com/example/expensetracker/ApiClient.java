package com.example.expensetracker;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // Later:
    // - If backend runs on your laptop: "http://10.0.2.2:8080/"
    // - If backend in Codespaces with public URL: "https://<your-codespace>-8080.app.github.dev/"
    private static final String BASE_URL = "http://10.0.2.2:8080/";

    private static ExpenseApi expenseApi;

    public static ExpenseApi getExpenseApi() {
        if (expenseApi == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            expenseApi = retrofit.create(ExpenseApi.class);
        }
        return expenseApi;
    }
}
