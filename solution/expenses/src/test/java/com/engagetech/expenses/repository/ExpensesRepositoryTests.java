package com.engagetech.expenses.repository;

import com.engagetech.expenses.model.Currency;
import com.engagetech.expenses.model.Expense;
import com.engagetech.expenses.model.ValueAddedTaxRate;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

/**
 * Expenses Repository Unit Tests
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
public class ExpensesRepositoryTests {

    @Autowired
    private TestEntityManager manager;

    @Autowired
    private ExpensesRepository repository;

    @Autowired
    private ValueAddedTaxRateRepository vatRepository;

    private SimpleDateFormat dateFormat;

    @Before
    public void setUp() {
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    }

    @Test
    @DisplayName("Should create a new expense and add it to the list of all expenses")
    public void shouldCreateExpenseAndReturnAnUpdatedExpensesList() throws Exception {
        ValueAddedTaxRate current = vatRepository.findEnabled();

        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = dateFormat.parse("02/02/2018");
        Expense expense = new Expense(date, 204.50, Currency.GBP, "Accomodation");
        expense.setRate(current);
        expense.setVAT(40.9);

        manager.persist(expense);
        manager.flush();

        List<Expense> expenses = repository.getAllExpenses();
        assertEquals("Accomodation", expenses.get(0).getReason());
    }
}
