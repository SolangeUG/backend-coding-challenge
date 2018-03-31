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
import java.util.Date;
import java.util.List;

/**
 * A simple expenses business service implementation
 */
@Service
public class ExpensesService {

    @Autowired
    private ExpensesRepository expensesRepository;

    @Autowired
    private ValueAddedTaxRateRepository vatRateRepository;

    @Value("${application.currency.converter.api.path}")
    private String apipath;

    @Value("${application.currency.converter.api.key}")
    private String apiKey;

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

            // in case the amount is supplied in EUR
            switch (expense.getCurrency()) {
                case EUR:
                    // change amount into GBP
                    Double exchangeRate = getExchangeRate();
                    Double convertedAmount = exchangeRate * expense.getAmount();
                    expense.setAmount(convertedAmount);
                    break;

                default:
                    break;
            }

            // update VAT amount:
            Double vat = (current.getRate() * expense.getAmount()) / 100;
            expense.setVAT(vat);

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
     * Return latest exchange rate from EUR to GBP
     * @return currency exchange rate
     */
    private Double getExchangeRate() {
        Double exchangeRate = 1.0;

        if (apipath != null && !apipath.isEmpty()
                && apiKey != null && ! apiKey.isEmpty()) {

            /*
            The API call to retrieve the exchange rate looks like:
                http://data.fixer.io/api/latest?access_key=apiKey&symbols=GBP&format=1
            In return, we get a JSON object:
                {
                    "success":true,
                        "timestamp":1522339443,
                        "base":"EUR",
                        "date":"2018-03-29",
                        "rates":{
                            "GBP":0.876292
                        }
                }
            */

            String requestURL =
                    apipath
                            + "?access_key=" + apiKey
                            + "&symbols=" + Currency.GBP.toString()
                            + "&format=1";

            RestTemplate restTemplate = new RestTemplate();
            String jsonResult = restTemplate.getForObject(requestURL, String.class);

            if (! jsonResult.isEmpty()) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    JsonNode node = mapper.readTree(jsonResult);
                    Boolean success = node.get("success").asBoolean();
                    if (success) {
                        exchangeRate = node.get("rates").get("GBP").asDouble();
                    }
                } catch (IOException exception) {
                    // do nothing for the time being
                }
            }

            restTemplate.delete(requestURL);
        }

        return exchangeRate;
    }

}
