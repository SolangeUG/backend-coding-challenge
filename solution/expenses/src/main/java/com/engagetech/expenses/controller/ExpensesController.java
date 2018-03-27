package com.engagetech.expenses.controller;

import com.engagetech.expenses.model.Expense;
import com.engagetech.expenses.service.ExpensesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * REST API endpoints controller
 */
@CrossOrigin
@RestController
@RequestMapping(value = "${application.api.path}",
                produces = {"application/json; charset=UTF-8"})
public class ExpensesController {

    @Autowired
    private ExpensesService expenseService;

    /**
     * Create a new expense
     * @param expense the new expense
     * @return an updated list of all expenses
     */
    @PostMapping(value = "${application.api.expenses.endpoint}")
    @ResponseBody
    public ResponseEntity<List<Expense>> createExpense(@RequestBody Expense expense) {
        if (expenseService.addExpense(expense)) {
            // TODO #3: change the response body returned value from list of expenses to string for example
            // TODO #3: return "Saved" on success and "One or more required fields is empty" on failure
            List<Expense> expenses = expenseService.getAllExpenses();
            return new ResponseEntity<>(expenses, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Return a user's expenses
     * @return a list of the user's expenses
     */
    @GetMapping(value = "${application.api.expenses.endpoint}")
    @ResponseBody
    public ResponseEntity<List<Expense>> getAllExpenses() {
        List<Expense> expenses = expenseService.getAllExpenses();
        return new ResponseEntity<>(expenses, HttpStatus.OK);
    }

}