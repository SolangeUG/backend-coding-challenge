package com.engagetech.expenses.service;

import com.engagetech.expenses.model.Expense;
import com.engagetech.expenses.model.ValueAddedTaxRate;
import com.engagetech.expenses.repository.ExpensesRepository;
import com.engagetech.expenses.repository.ValueAddedTaxRateRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Expenses Service Unit Tests
 */
@RunWith(SpringRunner.class)
public class ExpensesServiceTests {

    @TestConfiguration
    static class ExpensesServiceTestConfiguration {
        @Bean
        public ExpensesService expensesService() {
            return new ExpensesService();
        }
    }

    @Autowired
    private ExpensesService service;

    @MockBean
    private ExpensesRepository repository;

    @MockBean
    private ValueAddedTaxRateRepository vatRepository;

    private SimpleDateFormat dateFormat;
    private ValueAddedTaxRate current;

    @Before
    public void setUp() throws Exception {
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = dateFormat.parse("02/02/2018");
        Expense expense = new Expense(date, 404.05, "Hotel");

        current = new ValueAddedTaxRate(21.0, true);
        expense.setRate(current);

        List<Expense> expenses = new LinkedList<>();
        expenses.add(expense);

        Mockito.when(repository.getAllExpenses())
                .thenReturn(expenses);
    }

    @Test
    @DisplayName("Should return true when creating a new well formed expense")
    public void shouldReturnTrueOnCreatingWellFormedExpense() throws Exception {
        Date date = dateFormat.parse("04/02/2018");
        Expense expense = new Expense(date, 54.45, "Transport");
        expense.setRate(current);

        boolean result = service.addExpense(expense);
        assertTrue(result);
    }

    @Test
    @DisplayName("Should return false when creating a new ill formed expense")
    public void shouldReturnFalseOnCreatingIllFormedExpense() throws Exception {
        Date date = dateFormat.parse("08/02/2018");
        Expense expense = new Expense(date, 540.45, "");
        expense.setRate(current);

        boolean result = service.addExpense(expense);
        assertFalse(result);
    }

}
