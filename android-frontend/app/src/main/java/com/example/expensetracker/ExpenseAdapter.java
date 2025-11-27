package com.example.expensetracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    public interface OnExpenseActionListener {
        void onEdit(Expense expense);
        void onDelete(Expense expense);
    }

    private List<Expense> expenses;
    private final OnExpenseActionListener listener;

    public ExpenseAdapter(List<Expense> expenses, OnExpenseActionListener listener) {
        this.expenses = expenses;
        this.listener = listener;
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenses.get(position);
        holder.textDescription.setText(expense.getDescription());
        holder.textAmount.setText("₹" + expense.getAmount());
        holder.textCategory.setText(expense.getCategory());
        holder.textDate.setText(expense.getDate());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(expense);
        });

        holder.textDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(expense);
        });
    }

    @Override
    public int getItemCount() {
        return expenses == null ? 0 : expenses.size();
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {

        TextView textDescription, textAmount, textCategory, textDate, textDelete;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            textDescription = itemView.findViewById(R.id.textDescription);
            textAmount = itemView.findViewById(R.id.textAmount);
            textCategory = itemView.findViewById(R.id.textCategory);
            textDate = itemView.findViewById(R.id.textDate);
            textDelete = itemView.findViewById(R.id.textDelete);
        }
    }
}
