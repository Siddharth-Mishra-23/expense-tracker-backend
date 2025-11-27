package com.example.expensetracker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements ExpenseAdapter.OnExpenseActionListener {

    private static final String TAG = "MainActivity";

    private androidx.recyclerview.widget.RecyclerView recyclerView;
    private ExpenseAdapter adapter;
    private List<Expense> expenseList = new ArrayList<>();

    private android.widget.TextView textTotalAmount, textFoodTotal, textTravelTotal, textShoppingTotal, textOtherTotal;
    private Spinner spinnerCategory;
    private FloatingActionButton fabAdd;
    private String userId;

    private final String[] categories = new String[] {"All", "Food", "Travel", "Shopping", "Other"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userId = getIntent().getStringExtra("USER_ID");
        if (userId == null || userId.trim().isEmpty()) {
            userId = "user123";
        }

        initViews();
        setupCategoryFilter();
        setupRecyclerView();
        setupFab();

        fetchExpenses(null);
    }

    private void initViews() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setSubtitle("User: " + userId);

        recyclerView = findViewById(R.id.recyclerViewExpenses);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        fabAdd = findViewById(R.id.fabAddExpense);

        textTotalAmount = findViewById(R.id.textTotalAmount);
        textFoodTotal = findViewById(R.id.textFoodTotal);
        textTravelTotal = findViewById(R.id.textTravelTotal);
        textShoppingTotal = findViewById(R.id.textShoppingTotal);
        textOtherTotal = findViewById(R.id.textOtherTotal);
    }

    private void setupCategoryFilter() {
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(catAdapter);

        spinnerCategory.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selected = categories[position];
                String categoryFilter = "All".equals(selected) ? null : selected;
                fetchExpenses(categoryFilter);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // no-op
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new ExpenseAdapter(expenseList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupFab() {
        fabAdd.setOnClickListener(v -> openExpenseDialog(null));
    }

    private void fetchExpenses(String categoryFilter) {
        ApiClient.getExpenseApi()
                .getExpenses(userId, categoryFilter, null, null)
                .enqueue(new Callback<List<Expense>>() {
                    @Override
                    public void onResponse(Call<List<Expense>> call, Response<List<Expense>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            expenseList = response.body();
                            adapter.setExpenses(expenseList);
                            updateSummary();
                        } else {
                            Toast.makeText(MainActivity.this, "Failed to load expenses: " + response.code(), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Fetch error code: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Expense>> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Fetch failure", t);
                    }
                });
    }

    private void updateSummary() {
        double total = 0;
        Map<String, Double> perCategory = new HashMap<>();
        perCategory.put("Food", 0.0);
        perCategory.put("Travel", 0.0);
        perCategory.put("Shopping", 0.0);
        perCategory.put("Other", 0.0);

        for (Expense e : expenseList) {
            total += e.getAmount();
            String cat = e.getCategory();
            if (!perCategory.containsKey(cat)) {
                perCategory.put(cat, 0.0);
            }
            perCategory.put(cat, perCategory.get(cat) + e.getAmount());
        }

        textTotalAmount.setText("Total: ₹" + (long) total);
        textFoodTotal.setText("Food: ₹" + (long) perCategory.get("Food"));
        textTravelTotal.setText("Travel: ₹" + (long) perCategory.get("Travel"));
        textShoppingTotal.setText("Shopping: ₹" + (long) perCategory.get("Shopping"));
        textOtherTotal.setText("Other: ₹" + (long) perCategory.get("Other"));
    }

    @Override
    public void onEdit(Expense expense) {
        openExpenseDialog(expense);
    }

    @Override
    public void onDelete(Expense expense) {
        new AlertDialog.Builder(this)
                .setTitle("Delete expense")
                .setMessage("Are you sure you want to delete this expense?")
                .setPositiveButton("Delete", (d, w) -> deleteExpense(expense))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteExpense(Expense expense) {
        ApiClient.getExpenseApi()
                .deleteExpense(expense.getId())
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                            // Refresh with current filter
                            String selected = (String) spinnerCategory.getSelectedItem();
                            String categoryFilter = "All".equals(selected) ? null : selected;
                            fetchExpenses(categoryFilter);
                        } else {
                            Toast.makeText(MainActivity.this, "Delete failed: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openExpenseDialog(Expense existing) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(existing == null ? "Add Expense" : "Edit Expense");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_expense, null);
        builder.setView(view);

        EditText etAmount = view.findViewById(R.id.etAmount);
        EditText etDescription = view.findViewById(R.id.etDescription);
        EditText etDate = view.findViewById(R.id.etDate);
        Spinner spinnerDialogCategory = view.findViewById(R.id.spinnerDialogCategory);

        String[] dialogCategories = new String[] {"Food", "Travel", "Shopping", "Other"};
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                dialogCategories
        );
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDialogCategory.setAdapter(catAdapter);

        if (existing != null) {
            etAmount.setText(String.valueOf(existing.getAmount()));
            etDescription.setText(existing.getDescription());
            etDate.setText(existing.getDate());
            // set spinner selection
            for (int i = 0; i < dialogCategories.length; i++) {
                if (dialogCategories[i].equalsIgnoreCase(existing.getCategory())) {
                    spinnerDialogCategory.setSelection(i);
                    break;
                }
            }
        }

        builder.setPositiveButton(existing == null ? "Add" : "Update", null);
        builder.setNegativeButton("Cancel", (d, w) -> d.dismiss());

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dlg -> {
            android.widget.Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positive.setOnClickListener(v -> {
                String amountStr = etAmount.getText().toString().trim();
                String desc = etDescription.getText().toString().trim();
                String date = etDate.getText().toString().trim();
                String category = (String) spinnerDialogCategory.getSelectedItem();

                if (amountStr.isEmpty() || desc.isEmpty() || date.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
                    return;
                }

                double amount;
                try {
                    amount = Double.parseDouble(amountStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Invalid amount.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    Toast.makeText(MainActivity.this, "Date must be yyyy-MM-dd.", Toast.LENGTH_SHORT).show();
                    return;
                }

                ExpenseRequest req = new ExpenseRequest(userId, amount, desc, date, category);

                if (existing == null) {
                    createExpense(req, dialog);
                } else {
                    updateExpense(existing.getId(), req, dialog);
                }
            });
        });

        dialog.show();
    }

    private void createExpense(ExpenseRequest req, AlertDialog dialog) {
        ApiClient.getExpenseApi()
                .createExpense(req)
                .enqueue(new Callback<Expense>() {
                    @Override
                    public void onResponse(Call<Expense> call, Response<Expense> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Expense added", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            String selected = (String) spinnerCategory.getSelectedItem();
                            String categoryFilter = "All".equals(selected) ? null : selected;
                            fetchExpenses(categoryFilter);
                        } else {
                            Toast.makeText(MainActivity.this, "Add failed: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Expense> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateExpense(String id, ExpenseRequest req, AlertDialog dialog) {
        ApiClient.getExpenseApi()
                .updateExpense(id, req)
                .enqueue(new Callback<Expense>() {
                    @Override
                    public void onResponse(Call<Expense> call, Response<Expense> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Expense updated", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            String selected = (String) spinnerCategory.getSelectedItem();
                            String categoryFilter = "All".equals(selected) ? null : selected;
                            fetchExpenses(categoryFilter);
                        } else {
                            Toast.makeText(MainActivity.this, "Update failed: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Expense> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
