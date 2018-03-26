package com.engagetech.expenses.service;

import com.engagetech.expenses.model.Expense;
import com.engagetech.expenses.model.ValueAddedTaxRate;
import com.engagetech.expenses.repository.ExpensesRepository;
import com.engagetech.expenses.repository.ValueAddedTaxRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * A simple expenses service implementation
 */
@Service
public class ExpensesService {

    @Autowired
    private ExpensesRepository expensesRepository;

    @Autowired
    private ValueAddedTaxRateRepository vatRateRepository;

    /**
     * Return a list of all expenses
     * @return all expenses
     */
    public List<Expense> getAllExpenses() {
        return expensesRepository.getAllExpenses();
    }

    /**
     * Add a new expense to the list of all expenses
     * @param expense the new expense
     * @return the operation result
     */
    public boolean addExpense(Expense expense) {
        if (! isValid(expense)) {
            return false;
        } else {
            ValueAddedTaxRate current = vatRateRepository.findEnabled();
            expense.setRate(current);
            expensesRepository.save(expense);
            return true;
        }
    }

    /**
     * Validate expense data
     * @param expense input expense to validate
     * @return true if all the mandatory information is supplied
     *         false otherwise
     */
    private boolean isValid(Expense expense) {
        return expense != null
                && expense.getDate() != null && expense.getValue() != null
                && expense.getReason() != null && !expense.getReason().isEmpty();
    }

}
