package com.example.expensetracker;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ExpenseApi {

    @POST("/expenses")
    Call<Expense> createExpense(@Body ExpenseRequest request);

    @PUT("/expenses/{id}")
    Call<Expense> updateExpense(@Path("id") String id, @Body ExpenseRequest request);

    @DELETE("/expenses/{id}")
    Call<Void> deleteExpense(@Path("id") String id);

    @GET("/expenses")
    Call<java.util.List<Expense>> getExpenses(
            @Query("userId") String userId,
            @Query("category") String category,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate
    );
}
