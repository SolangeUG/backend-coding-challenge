package com.engagetech.expenses.controller;

import com.engagetech.expenses.model.Expense;
import com.engagetech.expenses.service.ExpensesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> createExpense(@RequestBody Expense expense) {
        boolean result = expenseService.addExpense(expense);
        String response;
        if (result) {
            response = "{\"message\":\"Expense successfully added!\"}";
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            response = "{\"message\":\"Expense creation failed!\"}";
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
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