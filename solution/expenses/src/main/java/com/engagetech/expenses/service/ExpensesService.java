package com.engagetech.expenses.service;

import com.engagetech.expenses.model.Currency;
import com.engagetech.expenses.model.Expense;
import com.engagetech.expenses.model.ValueAddedTaxRate;
import com.engagetech.expenses.repository.ExpensesRepository;
import com.engagetech.expenses.repository.ValueAddedTaxRateRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
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

    @Value("${application.currency.converter.api.path}")
    public String apipath;

    @Value("${application.currency.converter.api.key}")
    public String apiKey;

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

            // in case VAT amount is not supplied by client
            if (expense.getVAT() == null) {
                // compute VAT actual amount
                Double vat = (current.getRate() * expense.getAmount()) / 100;
                expense.setVAT(vat);
            }

            // in case the amount is supplied in EUR
            switch (expense.getCurrency()) {
                case EUR:
                    Double convertedAmount = convertAmount(expense);
                    expense.setAmount(convertedAmount);
                    break;
                default:
                    break;
            }

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
        boolean valid = expense != null
                            && expense.getDate() != null && expense.getAmount() != null
                            && expense.getReason() != null && !expense.getReason().isEmpty();

        // Check date validity
        if (valid) {
            Date now = new Date();
            valid = expense.getDate().before(now);
        }

        return valid;
    }

    /**
     * Convert expense amount from EUR to GBP
     * @param expense input expense
     * @return result of conversion
     */
    private Double convertAmount(Expense expense) {
        Double result = expense.getAmount();

        String requestURL = apipath + "?from={eur}&to={gbp}&quantity={amount}&api_key={key}";

        RestTemplate restTemplate = new RestTemplate();
        String jsonResult =
                restTemplate.getForObject(requestURL,String.class,
                    requestURL, Currency.EUR.toString(), Currency.GBP.toString(), expense.getAmount(),apiKey);

        if (! jsonResult.isEmpty()) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                JsonNode node = mapper.readTree(jsonResult);
                result = node.get("value").asDouble();
            } catch (IOException exception) {
                // do nothing for the time being
            }
        }

        return result;
    }

}
