package com.engagetech.expenses.repository;


import com.engagetech.expenses.model.Expense;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;

/**
 * Repository interface for CRUD operations on expenses data
 */
@Repository
public interface ExpensesRepository extends CrudRepository<Expense, Long> {

    /**
     * Return a list of all expenses
     *
     * @return expense list
     */
    default List<Expense> getAllExpenses() {
        Iterable<Expense> iterable = this.findAll();
        List<Expense> expenses = new LinkedList<>();
        iterable.forEach(expenses::add);
        return expenses;
    }

}